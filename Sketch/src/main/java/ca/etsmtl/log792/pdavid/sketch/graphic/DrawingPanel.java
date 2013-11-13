package ca.etsmtl.log792.pdavid.sketch.graphic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.HashMap;

import ca.etsmtl.log792.pdavid.sketch.graphic.shape.Point2D;
import ca.etsmtl.log792.pdavid.sketch.graphic.util.Constant;
import ca.etsmtl.log792.pdavid.sketch.graphic.util.UserContext;

/**
 * Created by pdavid on 28/10/13.
 */
public class DrawingPanel extends SurfaceView implements SurfaceHolder.Callback {

    private PanelThread _thread;
    Paint paint = new Paint();

    static GraphicsWrapper gw = new GraphicsWrapper();
    Drawing drawing = new Drawing();
    UserContext[] userContexts = null;

    public static final int TOUCH_EVENT_DOWN = 0;
    public static final int TOUCH_EVENT_MOVE = 1;
    public static final int TOUCH_EVENT_UP = 2;
    HashMap<Integer, Point2D> idsAndPositions = new HashMap<Integer, Point2D>(); // used to detect changes in position

    MultitouchFramework mf = null;
    private OnTouchListener touchListener;

    public DrawingPanel(Context context) {
        super(context);
        init();
        if (!isInEditMode()) {
            getHolder().addCallback(this);
        }
    }

    public DrawingPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        if (!isInEditMode()) {
            getHolder().addCallback(this);
        }
    }

    public DrawingPanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        if (!isInEditMode()) {
            getHolder().addCallback(this);
        }
        setBackgroundColor(getResources().getColor(android.R.color.white));
    }

    private void init() {

        setFocusable(true);
        setFocusableInTouchMode(true);

        setOnTouchListener(getTouchListener());
        setBackgroundColor(Color.WHITE);

        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);

        userContexts = new UserContext[Constant.NUM_USERS];
        for (int j = 0; j < Constant.NUM_USERS; ++j) {
            userContexts[j] = new UserContext(drawing);
        }

        gw.setFontHeight(Constant.TEXT_HEIGHT);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        gw.set(paint, canvas);

        gw.clear(1, 1, 1);
        gw.setColor(0, 0, 0);
//        gw.setupForDrawing();

        gw.setCoordinateSystemToWorldSpaceUnits();
//        gw.enableAlphaBlending();

        drawing.draw(gw);

        gw.setCoordinateSystemToPixels();


        if (!this.isInEditMode()) {
            gw.setFontHeight(Constant.TEXT_HEIGHT);
            for (int j = 0; j < Constant.NUM_USERS; ++j) {
                userContexts[j].draw(gw);
            }

            // Draw some text to indicate the number of fingers touching the user
            // interface.
            // This is useful for debugging.
            int totalNumCursors = 0;
            String s = "[";
            for (int j = 0; j < Constant.NUM_USERS; ++j) {
                totalNumCursors += userContexts[j].getNumCursors();
                s += (j == 0 ? "" : "+") + userContexts[j].getNumCursors();
            }
            s += " contacts]";
            if (totalNumCursors > 0) {
                gw.setColor(0, 0, 0);
                gw.drawString(Constant.TEXT_HEIGHT, 2 * Constant.TEXT_HEIGHT, s);
            }
        }

        /**
         * OLD
         */
        // gw.clear( 0.0f, 0.0f, 0.0f );
        // gw.setCoordinateSystemToWorldSpaceUnits();
        // gw.setLineWidth( 1 );
        // gw.setCoordinateSystemToPixels();

//        client.draw();
//
//        if (debugEvents) {
//            logger.draw(gw);
//        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setWillNotDraw(false); //Allows us to use invalidate() to call onDraw()

        _thread = new PanelThread(getHolder(), this); //Start the thread that
        _thread.setRunning(true);                     //will make calls to
        _thread.start();                              //onDraw()
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            _thread.setRunning(false);                //Tells thread to stop
            _thread.join();                           //Removes thread from mem.
        } catch (InterruptedException e) {
        }
    }

    private OnTouchListener getTouchListener() {
        if (touchListener == null) {
            touchListener = new OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {

//                    logger.log(event);

                    int type = TOUCH_EVENT_MOVE;
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_POINTER_DOWN:
                            type = TOUCH_EVENT_DOWN;
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_POINTER_UP:
                        case MotionEvent.ACTION_CANCEL:
                            type = TOUCH_EVENT_UP;
                            break;
                        case MotionEvent.ACTION_MOVE:
                        default:
                            type = TOUCH_EVENT_MOVE;
                            break;
                    }

                    if (type == TOUCH_EVENT_MOVE) {
                        // need to check which coordinates have changed, and
                        // call client's method once for each change
                        for (int i = 0; i < event.getPointerCount(); ++i) {
                            int id = event.getPointerId(i);
                            Point2D p_old = idsAndPositions.get(new Integer(id));
                            float x_new = event.getX(i);
                            float y_new = event.getY(i);
                            Point2D p_new = new Point2D(x_new, y_new);
                            if (p_old != null && !p_old.equals(p_new))
                                processMultitouchInputEvent(id, x_new, y_new, type);
                        }
                    } else {
                        int id = event.getPointerId(event.getActionIndex());
                        float x = event.getX(event.getActionIndex());
                        float y = event.getY(event.getActionIndex());
                        processMultitouchInputEvent(id, x, y, type);
                    }

                    // store locations to compare with them next time
                    idsAndPositions.clear();
                    for (int i = 0; i < event.getPointerCount(); ++i) {
                        idsAndPositions.put(new Integer(event.getPointerId(i)),
                                new Point2D(event.getX(i), event.getY(i)));
                    }

//                    v.invalidate(); // TODO XXX this shouldn't be necessary, but
                    // will force a redraw

                    return true; // indicates we have consumed the event
                }
            };
        }
        return touchListener;
    }

    /**
     * Returns the index of the user context that is most appropriate for handling this event.
     *
     * @param id
     * @param x
     * @param y
     * @return
     */
    private int findIndexOfUserContextForMultitouchInputEvent(int id, float x, float y) {

        // If there is a user context that already has a cursor with the given
        // id,
        // then we return the index of that user context.
        for (int j = 0; j < Constant.NUM_USERS; ++j) {
            if (userContexts[j].hasCursorID(id))
                return j;
        }

        // None of the user contexts have a cursor with the given id.
        // So, we find the user context whose palette is closest to the given
        // event location.
        // (Later, that user context will create a new cursor with the given id,
        // so it will continue to process future events for the same cursor.)
        int indexOfClosestUserContext = 0;
        float distanceOfClosestUserContext = userContexts[0].distanceToPalette(x, y);
        for (int j = 1; j < Constant.NUM_USERS; ++j) {
            float candidateDistance = userContexts[j].distanceToPalette(x, y);
            if (candidateDistance < distanceOfClosestUserContext) {
                indexOfClosestUserContext = j;
                distanceOfClosestUserContext = candidateDistance;
            }
        }
        return indexOfClosestUserContext;
    }

    public synchronized void processMultitouchInputEvent(int id, float x, float y, int type) {
        // System.out.println("event: "+id+", "+x+", "+y+", "+type);

        int indexOfUserContext = findIndexOfUserContextForMultitouchInputEvent(id, x, y);

        boolean doOtherUserContextsHaveCursors = false;
        for (int j = 0; j < Constant.NUM_USERS; ++j) {
            if (j != indexOfUserContext && userContexts[j].hasCursors()) {
                doOtherUserContextsHaveCursors = true;
                break;
            }
        }

        boolean redrawRequested = userContexts[indexOfUserContext].processMultitouchInputEvent(id, x, y, type, gw,
                mf, doOtherUserContextsHaveCursors);
//        if (redrawRequested) {
//            requestRedraw();
//        }
    }

    /**
     * REPAINT THREAD
     */
    class PanelThread extends Thread {
        private SurfaceHolder _surfaceHolder;
        private DrawingPanel _panel;
        private boolean _run = false;


        public PanelThread(SurfaceHolder surfaceHolder, DrawingPanel panel) {
            _surfaceHolder = surfaceHolder;
            _panel = panel;
        }


        public void setRunning(boolean run) { //Allow us to stop the thread
            _run = run;
        }


        @Override
        public void run() {
            Canvas c;
            while (_run) {     //When setRunning(false) occurs, _run is
                c = null;      //set to false and loop ends, stopping thread
                try {
                    c = _surfaceHolder.lockCanvas(null);
                    synchronized (_surfaceHolder) {
                        //Insert methods to modify positions of items in onDraw()
                        postInvalidate();

                    }
                } finally {
                    if (c != null) {
                        _surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }

}
