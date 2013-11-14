package ca.etsmtl.log792.pdavid.sketch.graphic;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import java.util.HashMap;

import ca.etsmtl.log792.pdavid.sketch.graphic.shape.Point2D;
import ca.etsmtl.log792.pdavid.sketch.graphic.shape.Stroke;
import ca.etsmtl.log792.pdavid.sketch.graphic.util.Constant;
import ca.etsmtl.log792.pdavid.sketch.graphic.util.EventLogger;
import ca.etsmtl.log792.pdavid.sketch.graphic.util.SimpleWhiteboard;

public class MultitouchFramework extends SurfaceView {

    public static MultitouchFramework mf = null; // stores a singleton

    public static final int TOUCH_EVENT_DOWN = 0;
    public static final int TOUCH_EVENT_MOVE = 1;
    public static final int TOUCH_EVENT_UP = 2;
    public boolean connected = false;

    Paint paint = new Paint();

    GraphicsWrapper gw = new GraphicsWrapper();
    Activity activity;
    private SimpleWhiteboard client = null;

    HashMap<Integer, Point2D> idsAndPositions = new HashMap<Integer, Point2D>(); // used to detect changes in position

    EventLogger logger = new EventLogger();
    boolean debugEvents = false;

    OnTouchListener touchListener;

    public MultitouchFramework(Context context) {
        super(context);
        init();
    }

    public MultitouchFramework(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    private void init() {
        assert mf == null;
        mf = this;

        setFocusable(true);
        setFocusableInTouchMode(true);

        setOnTouchListener(getTouchListener());
        setBackgroundColor(Color.WHITE);

        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);

        client = new SimpleWhiteboard(this, gw);
        client.startBackgroundWork();
    }

    public Bitmap savePNG(String basename) {

        Drawing drawing = client.getDrawing();

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

    public String generateSVG(String basename) {

        Drawing drawing = client.getDrawing();

        GraphicsWrapper gw_img = new GraphicsWrapper();
        gw_img.resize(gw.getWidth(), gw.getHeight());
        gw_img.frame(drawing.getBoundingRectangle(), true);

        String res = "";
        res += "<?xml version=\"1.0\" standalone=\"no\"?>\n<!DOCTYPE svg PUBLIC \""
                + "-//W3C//DTD SVG 1.1//EN\" \n  \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n<svg width=\""
                + gw.getWidth() + "px\" height=\"" + gw.getHeight() + "px\" viewBox=\"0 0 " + gw.getWidth() + " "
                + gw.getHeight() + " " + "\"\nxmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n<g>\n";
        for (Stroke s : drawing.strokes) {
            res += s.writeSVG(gw_img);
            res += "\n";
        }
        res += "</g>\n</svg>";
        return res;
    }


    public void requestRedrawInUiThread() {
        // invalidate();

        // The simplest thing to do here would be to call invalidate() on
        // ourself directly,
        // however this won't work if the caller is not in the UI thread.
        // Thus, we indirectly cause invalidate() to be called.
        final View view = this;
        activity.runOnUiThread(new Runnable() {
            public void run() {
                view.invalidate();
            }
        });
    }

    public void requestRedraw() {
        invalidate();
    }

    public static void log(String message) {
        if (mf != null && mf.debugEvents) {
            mf.logger.log(message);
            System.out.println(message);
            if (Looper.myLooper() == Looper.getMainLooper()) // the same as (

                mf.requestRedraw();
            else
                mf.requestRedrawInUiThread();
        }
    }

    protected void onDraw(Canvas canvas) {
        // The view is constantly redrawn by this method
        gw.set(paint, canvas);
        client.draw();
    }

    private OnTouchListener getTouchListener() {
        if (touchListener == null) {
            touchListener = new OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {

                    logger.log(event);

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
                                client.processMultitouchInputEvent(id, x_new, y_new, type);
                        }
                    } else {
                        int id = event.getPointerId(event.getActionIndex());
                        float x = event.getX(event.getActionIndex());
                        float y = event.getY(event.getActionIndex());
                        client.processMultitouchInputEvent(id, x, y, type);
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

}
