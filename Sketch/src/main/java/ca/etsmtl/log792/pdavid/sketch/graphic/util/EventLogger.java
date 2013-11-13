package ca.etsmtl.log792.pdavid.sketch.graphic.util;

import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.HashMap;

import ca.etsmtl.log792.pdavid.sketch.graphic.GraphicsWrapper;
import ca.etsmtl.log792.pdavid.sketch.graphic.shape.Point2D;

public class EventLogger {
    private static final int MAX_NUM_ITEMS = 30;
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 700;

    ArrayList<EventLogItem> items = new ArrayList<EventLogItem>();
    HashMap<Integer, Point2D> locations = new HashMap<Integer, Point2D>();

    private String generateDescription(MotionEvent event) {
        String s = "";
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                s = "DO";
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                s = "PD";
                break;
            case MotionEvent.ACTION_MOVE:
                s = "MO";
                break;
            case MotionEvent.ACTION_UP:
                s = "UP";
                break;
            case MotionEvent.ACTION_POINTER_UP:
                s = "PU";
                break;
            case MotionEvent.ACTION_CANCEL:
                s = "CA";
                break;
            default:
                s = "?" + event.getActionMasked();
                break;
        }
        s = s + "[" + event.getActionIndex() + "]";
        for (int i = 0; i < event.getPointerCount(); ++i) {
            int id = event.getPointerId(i);
            if (i > 0)
                s = s + ",";
            s = s + id;
            Point2D p = locations.get(new Integer(id));
            if (p != null && !p.equals(new Point2D(event.getX(i), event.getY(i))))
                s = s + "*";
        }

        // store locations to compare with them next time
        locations.clear();
        for (int i = 0; i < event.getPointerCount(); ++i) {
            locations.put(new Integer(event.getPointerId(i)), new Point2D(event.getX(i), event.getY(i)));
        }

        return s;
    }

    public void log(String message) {
        int N = items.size();
        if (N > 0 && message.equals(items.get(N - 1).description)) {
            items.get(N - 1).count++;
        } else {
            items.add(new EventLogItem(message));
            while (items.size() > MAX_NUM_ITEMS)
                items.remove(0);
        }
    }

    public void log(MotionEvent e) {
        log(generateDescription(e));
    }

    public void draw(GraphicsWrapper gw) {
        gw.setColor(0, 0, 0, 0.3f);
        gw.fillRect(5, 0, WINDOW_WIDTH - 10, WINDOW_HEIGHT);
        gw.setColor(1, 1, 1, 0.5f);
        int fontHeight = WINDOW_HEIGHT / (1 + MAX_NUM_ITEMS);
        gw.setFontHeight(fontHeight);
        for (int row = 0; row < items.size(); ++row) {
            String d = items.get(row).description;
            int c = items.get(row).count;
            if (c > 1)
                d = d + "(x" + c + ")";
            gw.drawString(15, (row + 1.5f) * fontHeight, d);
        }
    }

    class EventLogItem {
        public String description;
        public int count;

        public EventLogItem(String d) {
            description = d;
            count = 1;
        }
    }
}
