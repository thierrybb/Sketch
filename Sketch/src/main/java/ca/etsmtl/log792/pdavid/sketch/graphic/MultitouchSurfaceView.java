package ca.etsmtl.log792.pdavid.sketch.graphic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.HashMap;

import ca.etsmtl.log792.pdavid.sketch.R;
import ca.etsmtl.log792.pdavid.sketch.graphic.shape.Point2D;
import ca.etsmtl.log792.pdavid.sketch.graphic.shape.Stroke;
import ca.etsmtl.log792.pdavid.sketch.graphic.util.Constant;
import ca.etsmtl.log792.pdavid.sketch.graphic.util.EventLogger;
import ca.etsmtl.log792.pdavid.sketch.graphic.util.UserContext;

public class MultitouchSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    public static final int TOUCH_EVENT_DOWN = 0;

    public static final int TOUCH_EVENT_MOVE = 1;
    public static final int TOUCH_EVENT_UP = 2;
    private Paint paint = new Paint();
    private PanelThread _thread;
    private GraphicsWrapper gw = new GraphicsWrapper();
    private HashMap<Integer, Point2D> idsAndPositions = new HashMap<Integer, Point2D>(); // used to detect changes in position
    private EventLogger logger = new EventLogger();
    private OnTouchListener touchListener;
    private Drawing drawing = new Drawing();
    private UserContext[] userContexts = null;

    public MultitouchSurfaceView(Context context) {
        super(context);
        init();
    }

    public MultitouchSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    private void init() {

        getHolder().addCallback(this);

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
//        simpleWhiteboard = new SimpleWhiteboard(this, gw);
//        simpleWhiteboard.startBackgroundWork();
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
        return indexOfClosestUserContext;
    }

    public synchronized void processMultitouchInputEvent(int id, float x, float y, int type) {

        int indexOfUserContext = findIndexOfUserContextForMultitouchInputEvent(id, x, y);

        boolean doOtherUserContextsHaveCursors = false;
        for (int j = 0; j < Constant.NUM_USERS; ++j) {
            if (j != indexOfUserContext && userContexts[j].hasCursors()) {
                doOtherUserContextsHaveCursors = true;
                break;
            }
        }

        userContexts[indexOfUserContext].processMultitouchInputEvent(id, x, y, type, gw,
                this, doOtherUserContextsHaveCursors);
    }

    public Drawing getDrawing() {
        return drawing;
    }

    /**
     * Generates a Bitmap of the canvas
     *
     * @return Bitmap with all strokes of the sketch
     */
    public Bitmap generatePNG() {

        GraphicsWrapper gw_img = new GraphicsWrapper();
        gw_img.resize(gw.getWidth(), gw.getHeight());
        gw_img.frame(drawing.getBoundingRectangle(), true);

        Bitmap bitmap = null;
        bitmap = Bitmap.createBitmap(gw.getWidth(), gw.getHeight(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setStrokeWidth(Math.max(1.0f,
                Constant.INK_THICKNESS_IN_WORLD_SPACE_UNITS / gw.getScaleFactorInWorldSpaceUnitsPerPixel()));
        paint.setAntiAlias(true);
        Canvas canvas = new Canvas();
        canvas.setBitmap(bitmap);
        canvas.drawColor(Color.WHITE);
        for (Stroke s : drawing.strokes) {
            if (s.getColor_blue() == 0 && s.getColor_green() == 0 && s.getColor_red() == 0) {
                paint.setColor(Color.BLACK);
            } else if (s.getColor_blue() == 0 && s.getColor_green() == 1 && s.getColor_red() == 0) {
                paint.setColor(Color.GREEN);
            } else if (s.getColor_blue() == 0 && s.getColor_green() == 0 && s.getColor_red() == 1) {
                paint.setColor(Color.RED);
            }

            for (int i = 1; i < s.getPoints().size(); i++) {
                Point2D previousPoint = gw_img.convertWorldSpaceUnitsToPixels(s.getPoints().get(i - 1));
                Point2D currentPoint = gw_img.convertWorldSpaceUnitsToPixels(s.getPoints().get(i));
                canvas.drawLine(previousPoint.x(), previousPoint.y(), currentPoint.x(), currentPoint.y(), paint);
            }
        }
        return bitmap;
    }

    /**
     * Generates an XML 1.0 file with <svg/>
     *
     * @return XML String
     */
    @SuppressWarnings("ConstantConditions")
    public String generateSVG() {

        GraphicsWrapper gw_img = new GraphicsWrapper();
        gw_img.resize(gw.getWidth(), gw.getHeight());
        gw_img.frame(drawing.getBoundingRectangle(), true);

        String res = "";
        res += String.format(getContext().getString(R.string.default_svg_string), gw.getWidth(), gw.getHeight(), gw.getWidth(), gw.getHeight());
        for (Stroke s : drawing.strokes) {
            res += "<g>";
            res += s.writeSVG(gw_img);
            res += "</g>\n";
        }
        res += "</svg>";
        return res;
    }

    protected void onDraw(Canvas canvas) {
        gw.set(paint, canvas);
        gw.clear(1, 1, 1);
        gw.setColor(0, 0, 0);
        gw.setCoordinateSystemToWorldSpaceUnits();
        drawing.draw(gw);

        gw.setCoordinateSystemToPixels();

        gw.setFontHeight(Constant.TEXT_HEIGHT);
        for (int j = 0; j < Constant.NUM_USERS; ++j) {
            userContexts[j].draw(gw);
        }
    }

    private OnTouchListener getTouchListener() {
        if (touchListener == null) {
            touchListener = new OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {

                    logger.log(event);

                    int type;
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
                        // call simpleWhiteboard's method once for each change
                        for (int i = 0; i < event.getPointerCount(); ++i) {
                            int id = event.getPointerId(i);
                            Point2D p_old = idsAndPositions.get(id);
                            float x_new = event.getX(i);
                            float y_new = event.getY(i);
                            Point2D p_new = new Point2D(x_new, y_new);
                            if (p_old != null && !p_old.equals(p_new)) {
                                processMultitouchInputEvent(id, x_new, y_new, type);
                            }
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
                        idsAndPositions.put(event.getPointerId(i),
                                new Point2D(event.getX(i), event.getY(i)));
                    }

                    return true; // indicates we have consumed the event
                }
            };
        }
        return touchListener;
    }

    public void setColor(int i) {
        userContexts[0].setCurrent_color(i);
    }

    ////////////////////////////////////
    //  Surface Callbacks
    ////////////////////////////////////

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

    ////////////////////////////////////
    // REPAINT THREAD
    ////////////////////////////////////

    class PanelThread extends Thread {
        private SurfaceHolder _surfaceHolder;
        private MultitouchSurfaceView _panel;
        private boolean _run = false;


        public PanelThread(SurfaceHolder surfaceHolder, MultitouchSurfaceView panel) {
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
