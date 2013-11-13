package ca.etsmtl.log792.pdavid.sketch.graphic.util;

import ca.etsmtl.log792.pdavid.sketch.graphic.Drawing;
import ca.etsmtl.log792.pdavid.sketch.graphic.GraphicsWrapper;
import ca.etsmtl.log792.pdavid.sketch.graphic.MultitouchFramework;

public class SimpleWhiteboard implements Runnable {

    public MultitouchFramework multitouchFramework = null;
    public GraphicsWrapper gw = null;

    Thread thread = null;
    boolean threadSuspended = false;

    int mouse_x, mouse_y;

    Drawing drawing = new Drawing();

    UserContext[] userContexts = null;

    boolean palettePositionsInitialized = false;

    // This should be called after the GraphicsWrapper knows the window
    // dimensions
    private void positionPalettes() {
        // initialize the positions of the palettes
        if (Constant.NUM_USERS == 1) {
            userContexts[0].setPositionOfCenterOfPalette(gw.getWidth() / 2, gw.getHeight() / 2);
        } else {
            // Compute a circular layout of the palettes
            float radius = Math.min(gw.getWidth(), gw.getHeight()) / 4;
            for (int j = 0; j < Constant.NUM_USERS; ++j) {
                float angleInRadians = (float) (2 * Math.PI * j / Constant.NUM_USERS);
                userContexts[j].setPositionOfCenterOfPalette(
                        gw.getWidth() / 2 + (float) (radius * Math.cos(angleInRadians)), gw.getHeight() / 2
                        + (float) (radius * Math.sin(angleInRadians)));
            }
        }
    }

    public SimpleWhiteboard(MultitouchFramework mf, GraphicsWrapper gw) {
        multitouchFramework = mf;
        this.gw = gw;
//        multitouchFramework.setPreferredWindowSize(Constant.INITIAL_WINDOW_WIDTH, Constant.INITIAL_WINDOW_HEIGHT);

        userContexts = new UserContext[Constant.NUM_USERS];
        for (int j = 0; j < Constant.NUM_USERS; ++j) {
            userContexts[j] = new UserContext(drawing);
        }

        gw.setFontHeight(Constant.TEXT_HEIGHT);

    }

    // Called by the framework at startup time.
    public void startBackgroundWork() {
        if (thread == null) {
            thread = new Thread(this);
            threadSuspended = false;
            thread.start();
        } else {
            if (threadSuspended) {
                threadSuspended = false;
                synchronized (this) {
//                    notify();
                }
            }
        }
    }

    public void stopBackgroundWork() {
        threadSuspended = true;
    }

    public void run() {
        try {
            int sleepIntervalInMilliseconds = 1000;
            while (true) {

                // Here's where the thread does some work
                synchronized (this) {
                    // System.out.println("some background work");
                    // ...
                }
                // multitouchFramework.requestRedraw();

                // Now the thread checks to see if it should suspend itself
                if (threadSuspended) {
                    synchronized (this) {
                        while (threadSuspended) {
//                            wait();
                        }
                    }
                }
                Thread.sleep(sleepIntervalInMilliseconds); // interval given in
                // milliseconds
            }
        } catch (InterruptedException e) {
        }
    }

    public synchronized void draw() {
        if (!palettePositionsInitialized) {
            positionPalettes();
            palettePositionsInitialized = true;
        }

        gw.clear(1, 1, 1);
        gw.setColor(0, 0, 0);
//        gw.setupForDrawing();

        gw.setCoordinateSystemToWorldSpaceUnits();
//        gw.enableAlphaBlending();

        drawing.draw(gw);

        gw.setCoordinateSystemToPixels();

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
                multitouchFramework, doOtherUserContextsHaveCursors);
        if (redrawRequested)
            multitouchFramework.requestRedraw();
    }
}
