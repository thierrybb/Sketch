package ca.etsmtl.sketch.graphic.util;

public class SimpleWhiteboard {

//    public MultitouchSurfaceView multitouchFramework = null;
//    public GraphicsWrapper gw = null;
//    Drawing drawing = new Drawing();
//    UserContext[] userContexts = null;
//    public SimpleWhiteboard(MultitouchSurfaceView mf, GraphicsWrapper gw) {
//        multitouchFramework = mf;
//        this.gw = gw;
//
//        userContexts = new UserContext[Constant.NUM_USERS];
//        for (int j = 0; j < Constant.NUM_USERS; ++j) {
//            userContexts[j] = new UserContext(drawing);
//        }
//
//        gw.setFontHeight(Constant.TEXT_HEIGHT);
//
//    }
//
//    public synchronized void draw() {
//        gw.clear(1, 1, 1);
//        gw.setColor(0, 0, 0);
//        gw.setCoordinateSystemToWorldSpaceUnits();
//        drawing.draw(gw);
//
//        gw.setCoordinateSystemToPixels();
//
//        gw.setFontHeight(Constant.TEXT_HEIGHT);
//        for (int j = 0; j < Constant.NUM_USERS; ++j) {
//            userContexts[j].draw(gw);
//        }
//
//    }
//
//    /**
//     * Returns the index of the user context that is most appropriate for handling this event.
//     *
//     * @param id
//     * @param x
//     * @param y
//     * @return
//     */
//    private int findIndexOfUserContextForMultitouchInputEvent(int id, float x, float y) {
//
//        // If there is a user context that already has a cursor with the given
//        // id,
//        // then we return the index of that user context.
//        for (int j = 0; j < Constant.NUM_USERS; ++j) {
//            if (userContexts[j].hasCursorID(id))
//                return j;
//        }
//
//        // None of the user contexts have a cursor with the given id.
//        // So, we find the user context whose palette is closest to the given
//        // event location.
//        // (Later, that user context will create a new cursor with the given id,
//        // so it will continue to process future events for the same cursor.)
//        int indexOfClosestUserContext = 0;
//        return indexOfClosestUserContext;
//    }
//
//    public synchronized void processMultitouchInputEvent(int id, float x, float y, int type) {
//
//        int indexOfUserContext = findIndexOfUserContextForMultitouchInputEvent(id, x, y);
//
//        boolean doOtherUserContextsHaveCursors = false;
//        for (int j = 0; j < Constant.NUM_USERS; ++j) {
//            if (j != indexOfUserContext && userContexts[j].hasCursors()) {
//                doOtherUserContextsHaveCursors = true;
//                break;
//            }
//        }
//
//        userContexts[indexOfUserContext].processMultitouchInputEvent(id, x, y, type, gw,
//                multitouchFramework, doOtherUserContextsHaveCursors);
//    }
//
//    public Drawing getDrawing() {
//        return drawing;
//    }
//
//    public void setColor(int c) {
//        gw.setColor(c);
//    }
}
