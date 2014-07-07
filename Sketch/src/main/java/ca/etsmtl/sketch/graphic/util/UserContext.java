package ca.etsmtl.sketch.graphic.util;

import android.graphics.Color;

import java.util.ArrayList;

import ca.etsmtl.sketch.graphic.CursorContainer;
import ca.etsmtl.sketch.graphic.Drawing;
import ca.etsmtl.sketch.graphic.GraphicsWrapper;
import ca.etsmtl.sketch.graphic.MultitouchSurfaceView;
import ca.etsmtl.sketch.graphic.MyCursor;
import ca.etsmtl.sketch.graphic.Vector2D;
import ca.etsmtl.sketch.graphic.shape.AlignedRectangle2D;
import ca.etsmtl.sketch.graphic.shape.Point2D;
import ca.etsmtl.sketch.graphic.shape.Stroke;
//import ca.etsmtl.sketch.network.NetworkClient;
//import ca.etsmtl.sketch.network.NetworkServer;

public class UserContext {
    //    public static int movePalette_buttonIndex = 1;
    public static int ink_buttonIndex = 0;
    public static int select_buttonIndex = 2;
//    private NetworkServer server = null;
//    private NetworkClient client = null;
    //    public static int black_buttonIndex = 3;
    public static int manipulate_buttonIndex = 4;
    public static int camera_buttonIndex = 5;
    //    public static int red_buttonIndex = 6;
//    public static int green_buttonIndex = 7;
    public static int vertFlip_buttonIndex = 8;
    public static int horizFlip_buttonIndex = 9;
    public static int remove_buttonIndex = 10;
    public static int frameAll_buttonIndex = 11;
    public static int frameSelected_buttonIndex = 12;
    public static int undo_buttonIndex = 12;
    public static int redo_buttonIndex = 13;
    //    public static int savePNG_buttonIndex = 14;
//    public static int saveSVG_buttonIndex = 15;
//    public static int sendMailPNG_buttonIndex = 16;
//    public static int sendMailSVG_buttonIndex = 17;
//    public static int start_server = 18;
//    public static int connect_IP = 19;
//    public static int connect_mul = 20;
//    public static int stop_connection = 21;
    public static int currentlyActiveModalButton = 0;
    public float current_red = 0;
    public float current_green = 0;
    public float current_blue = 0;
    public float current_alpha = 0;
    public int current_color = Color.BLACK;
    //    private Palette palette = new Palette();
    private CursorContainer cursorContainer = new CursorContainer();
    private Drawing drawing = null;
    private int networkMode = Constant.NM_NONE;
    private ArrayList<Stroke> selectedStrokes = new ArrayList<Stroke>();

    public UserContext(Drawing d) {
        drawing = d;
    }

    /**
     * returns true if any cursors are currently being handled by this user context
     *
     * @return
     */
    public boolean hasCursors() {
        return cursorContainer.getNumCursors() > 0;
    }

    public int getNumCursors() {
        return cursorContainer.getNumCursors();
    }

    /**
     * returns true if the given cursor (identified by its id) is currently being handled by this user context
     *
     * @param id
     * @return
     */
    public boolean hasCursorID(int id) {
        return cursorContainer.findIndexOfCursorById(id) > -1;
    }

    public void draw(GraphicsWrapper gw) {

        // draw filled rectangles over the selected strokes
//        gw.setCoordinateSystemToWorldSpaceUnits();
        for (Stroke s : selectedStrokes) {
            AlignedRectangle2D r = s.getBoundingRectangle();
            gw.setColor(1.0f, 0.5f, 0, 0.2f); // transparent orange
            Vector2D diagonal = r.getDiagonal();
            gw.fillRect(r.getMin().x(), r.getMin().y(), diagonal.x(), diagonal.y());
        }

//        gw.setCoordinateSystemToPixels();

        // draw cursors
        for (int i = 0; i < cursorContainer.getNumCursors(); ++i) {
            MyCursor cursor = cursorContainer.getCursorByIndex(i);
            if (cursor.type == MyCursor.TYPE_NOTHING)
                gw.setColor(0.5f, 0, 0, 0.65f); // red (because this cursor is
                // being ignored)
            else
                gw.setColor(0, 0.5f, 0.5f, 0.65f); // cyan
            gw.fillCircle(cursor.getCurrentPosition().x() - 10, cursor.getCurrentPosition().y() - 10, 10);

            if (cursor.type == MyCursor.TYPE_INKING) {
                // draw ink trail
                gw.setColor(0, 0, 0);
                gw.drawPolyline(cursor.getPositions());
            } else if (cursor.type == MyCursor.TYPE_SELECTION) {
                if (cursor.doesDragLookLikeLassoGesture()) {
                    // draw filled polygon
                    gw.setColor(0, 0, 0, 0.2f);
                    gw.fillPolygon(cursor.getPositions());
                } else {
                    // draw polyline to indicate that a lasso could be started
                    gw.setColor(0, 0, 0);
                    gw.drawPolyline(cursor.getPositions());

                    // also draw selection rectangle
                    gw.setColor(0, 0, 0, 0.2f);
                    Vector2D diagonal = Point2D.diff(cursor.getCurrentPosition(), cursor.getFirstPosition());
                    gw.fillRect(cursor.getFirstPosition().x(), cursor.getFirstPosition().y(), diagonal.x(),
                            diagonal.y());
                }
            }
        }
    }

    public void setCurrent_color(int current_color) {
        this.current_color = current_color;
    }

    /**
     * @param id
     * @param x                              in pixels
     * @param y                              in pixels
     * @param type
     * @param gw
     * @param mf
     * @param doOtherUserContextsHaveCursors
     * @return true if a redraw is requested
     */
    public boolean processMultitouchInputEvent(int id, float x, float y, int type, final GraphicsWrapper gw, final MultitouchSurfaceView mf, boolean doOtherUserContextsHaveCursors) {
        // Find the cursor that corresponds to the event id, if such a cursor already exists.
        // If no such cursor exists, the below index will be -1, and the
        // reference to cursor will be null.
        int cursorIndex = cursorContainer.findIndexOfCursorById(id);
        MyCursor cursor = (cursorIndex == -1) ? null : cursorContainer.getCursorByIndex(cursorIndex);

        if (cursor == null) {

            if (type == MultitouchSurfaceView.TOUCH_EVENT_UP) {
                // This should never happen, but if it does, just ignore the event.
                return false;
            }

            // The event does not correspond to any existing cursor.
            // In other words, this is a new finger touching the screen.
            // The event is probably of type TOUCH_EVENT_DOWN.
            // A new cursor will need to be created for the event.

            // The event did not occur inside the 
            // This new finger may have been placed down to start
            // drawing a stroke, or start camera manipulation, etc.
            // We branch according to the current mode.
            //
            if (currentlyActiveModalButton == ink_buttonIndex) {
                // start drawing a stroke
                cursorIndex = cursorContainer.updateCursorById(id, x, y);
                cursor = cursorContainer.getCursorByIndex(cursorIndex);
                cursor.setType(MyCursor.TYPE_INKING);
            } else if (currentlyActiveModalButton == select_buttonIndex) {
                // The new finger should only start selecting
                // if there is not already another finger performing selection.
                if (cursorContainer.getNumCursorsOfGivenType(MyCursor.TYPE_SELECTION) == 0) {
                    cursorIndex = cursorContainer.updateCursorById(id, x, y);
                    cursor = cursorContainer.getCursorByIndex(cursorIndex);
                    cursor.setType(MyCursor.TYPE_SELECTION);
                } else {
                    cursorIndex = cursorContainer.updateCursorById(id, x, y);
                    cursor = cursorContainer.getCursorByIndex(cursorIndex);
                    cursor.setType(MyCursor.TYPE_NOTHING);
                }
            } else if (currentlyActiveModalButton == manipulate_buttonIndex) {
                // The new finger should only manipulate the selection
                // if there are not already 2 fingers manipulating the selection.
                if (cursorContainer.getNumCursorsOfGivenType(MyCursor.TYPE_DIRECT_MANIPULATION) < 2) {
                    cursorIndex = cursorContainer.updateCursorById(id, x, y);
                    cursor = cursorContainer.getCursorByIndex(cursorIndex);
                    cursor.setType(MyCursor.TYPE_DIRECT_MANIPULATION);
                } else {
                    cursorIndex = cursorContainer.updateCursorById(id, x, y);
                    cursor = cursorContainer.getCursorByIndex(cursorIndex);
                    cursor.setType(MyCursor.TYPE_NOTHING);
                }
            } else if (currentlyActiveModalButton == camera_buttonIndex) {
                // The new finger should only manipulate the camera
                // if there are not already 2 fingers manipulating the camera.
                if (cursorContainer.getNumCursorsOfGivenType(MyCursor.TYPE_CAMERA_PAN_ZOOM) < 2) {
                    cursorIndex = cursorContainer.updateCursorById(id, x, y);
                    cursor = cursorContainer.getCursorByIndex(cursorIndex);
                    cursor.setType(MyCursor.TYPE_CAMERA_PAN_ZOOM);
                } else {
                    cursorIndex = cursorContainer.updateCursorById(id, x, y);
                    cursor = cursorContainer.getCursorByIndex(cursorIndex);
                    cursor.setType(MyCursor.TYPE_NOTHING);
                }
            }
        } else {
            // The event corresponds to an already existing cursor
            // (and the cursor was probably created during an earlier event of type TOUCH_EVENT_DOWN).
            // The current event is probably of type TOUCH_EVENT_MOVE or TOUCH_EVENT_UP.


            if (type == MultitouchSurfaceView.TOUCH_EVENT_MOVE) {
                // The event is a move event, and corresponds to an existing cursor.
                // Is the location of the event different from the last reported location?
                Point2D newPosition = new Point2D(x, y);
                if (cursor.getCurrentPosition().equals(newPosition)) {
                    // The event's location is the same as last time.
                    // Don't bother processing the event any further.
                    return false; // do not request a redraw
                }
            }

            // We branch according to the type of cursor.
            //
            if (cursor.type == MyCursor.TYPE_NOTHING) {
                // Update the cursor with its new position.
                cursorContainer.updateCursorById(id, x, y);

                if (type == MultitouchSurfaceView.TOUCH_EVENT_UP)
                    cursorContainer.removeCursorByIndex(cursorIndex);
            } else if (cursor.type == MyCursor.TYPE_INTERACTING_WITH_WIDGET) {
                if (type == MultitouchSurfaceView.TOUCH_EVENT_UP) {
                    // The user lifted their finger off of a palette button.
                    cursorContainer.removeCursorByIndex(cursorIndex);

//                    if (!buttons.get(cursor.indexOfButton).isSticky) {
//                        buttons.get(cursor.indexOfButton).isPressed = false;
//                    }
                } else {
                    // Earlier, the user pressed down on a button in the palette,
                    // and now they are dragging their finger over the button
                    // (and possibly onto other buttons).
                    // If this is the "move palette" button, we move the 
//                    if (cursor.indexOfButton == movePalette_buttonIndex) {
//                        movePalette(x - cursor.getCurrentPosition().x(), y - cursor.getCurrentPosition().y());
//                    }
                    cursorIndex = cursorContainer.updateCursorById(id, x, y);
                }
            } else if (cursor.type == MyCursor.TYPE_INKING) {
                if (type == MultitouchSurfaceView.TOUCH_EVENT_UP) {
                    // up event
                    cursorIndex = cursorContainer.updateCursorById(id, x, y);

                    // Add the newly drawn stroke to the drawing
                    Stroke newStroke = new Stroke();
//                    newStroke.setColor(current_red, current_green, current_blue);
                    newStroke.setColor(current_color);
                    for (Point2D p : cursor.getPositions()) {
                        newStroke.addPoint(gw.convertPixelsToWorldSpaceUnits(p));
                    }
                    drawing.addStroke(newStroke);//, server, client, null);

                    cursorContainer.removeCursorByIndex(cursorIndex);
                } else {
                    // drag event; just update the cursor with the new position
                    cursorIndex = cursorContainer.updateCursorById(id, x, y);
                }
            } else if (cursor.type == MyCursor.TYPE_CAMERA_PAN_ZOOM) {
                if (type == MultitouchSurfaceView.TOUCH_EVENT_UP) {
                    // up event
                    cursorContainer.removeCursorByIndex(cursorIndex);
                } else {
                    // drag event
                    cursorIndex = cursorContainer.updateCursorById(id, x, y);

                    if (cursorContainer.getNumCursorsOfGivenType(MyCursor.TYPE_CAMERA_PAN_ZOOM) == 2) {
                        MyCursor cursor0 = cursorContainer.getCursorByType(MyCursor.TYPE_CAMERA_PAN_ZOOM, 0);
                        MyCursor cursor1 = cursorContainer.getCursorByType(MyCursor.TYPE_CAMERA_PAN_ZOOM, 1);
                        gw.panAndZoomBasedOnDisplacementOfTwoPoints(
                                id == cursor0.id ? cursor0.getPreviousPosition() : cursor0.getCurrentPosition(),
                                id == cursor1.id ? cursor1.getPreviousPosition() : cursor1.getCurrentPosition(),
                                cursor0.getCurrentPosition(),
                                cursor1.getCurrentPosition()
                        );
                    } else if (cursorContainer.getNumCursorsOfGivenType(MyCursor.TYPE_CAMERA_PAN_ZOOM) == 1) {
                        gw.pan(
                                cursor.getCurrentPosition().x() - cursor.getPreviousPosition().x(),
                                cursor.getCurrentPosition().y() - cursor.getPreviousPosition().y()
                        );
                    }
                }
            } else if (cursor.type == MyCursor.TYPE_SELECTION) {
                if (type == MultitouchSurfaceView.TOUCH_EVENT_UP) {
                    // up event
                    cursorIndex = cursorContainer.updateCursorById(id, x, y);

                    // Update the selection
                    if (cursor.doesDragLookLikeLassoGesture()) {
                        // complete a lasso selection

                        // Need to transform the positions of the cursor from pixels to world space coordinates.
                        // We will store the world space coordinates in the following data structure.
                        ArrayList<Point2D> lassoPolygonPoints = new ArrayList<Point2D>();
                        for (Point2D p : cursor.getPositions()) {
                            lassoPolygonPoints.add(gw.convertPixelsToWorldSpaceUnits(p));
                        }

                        selectedStrokes.clear();
                        for (Stroke s : drawing.strokes) {
                            if (s.isContainedInLassoPolygon(lassoPolygonPoints))
                                selectedStrokes.add(s);
                        }
                    } else {
                        // complete a rectangle selection

                        AlignedRectangle2D selectedRectangle = new AlignedRectangle2D(
                                gw.convertPixelsToWorldSpaceUnits(cursor.getFirstPosition()),
                                gw.convertPixelsToWorldSpaceUnits(cursor.getCurrentPosition())
                        );

                        selectedStrokes.clear();
                        for (Stroke s : drawing.strokes) {
                            if (s.isContainedInRectangle(selectedRectangle))
                                selectedStrokes.add(s);
                        }
                    }

                    cursorContainer.removeCursorByIndex(cursorIndex);
                } else {
                    // drag event; just update the cursor with the new position
                    cursorIndex = cursorContainer.updateCursorById(id, x, y);
                }
            } else if (cursor.type == MyCursor.TYPE_DIRECT_MANIPULATION) {
                if (type == MultitouchSurfaceView.TOUCH_EVENT_UP) {
                    // up event
                    cursorContainer.removeCursorByIndex(cursorIndex);
                } else {
                    // drag event
                    cursorIndex = cursorContainer.updateCursorById(id, x, y);

                    if (cursorContainer.getNumCursorsOfGivenType(MyCursor.TYPE_DIRECT_MANIPULATION) == 2) {
                        MyCursor cursor0 = cursorContainer.getCursorByType(MyCursor.TYPE_DIRECT_MANIPULATION, 0);
                        MyCursor cursor1 = cursorContainer.getCursorByType(MyCursor.TYPE_DIRECT_MANIPULATION, 1);

                        // convert cursor positions to world space
                        Point2D cursor0_currentPosition_worldSpace = gw.convertPixelsToWorldSpaceUnits(cursor0.getCurrentPosition());
                        Point2D cursor1_currentPosition_worldSpace = gw.convertPixelsToWorldSpaceUnits(cursor1.getCurrentPosition());
                        Point2D cursor0_previousPosition_worldSpace = gw.convertPixelsToWorldSpaceUnits(cursor0.getPreviousPosition());
                        Point2D cursor1_previousPosition_worldSpace = gw.convertPixelsToWorldSpaceUnits(cursor1.getPreviousPosition());

                        for (Stroke s : selectedStrokes) {
                            Point2DUtil.transformPointsBasedOnDisplacementOfTwoPoints(
                                    s.getPoints(),
                                    id == cursor0.id ? cursor0_previousPosition_worldSpace : cursor0_currentPosition_worldSpace,
                                    id == cursor1.id ? cursor1_previousPosition_worldSpace : cursor1_currentPosition_worldSpace,
                                    cursor0_currentPosition_worldSpace,
                                    cursor1_currentPosition_worldSpace
                            );
                            s.markBoundingRectangleDirty();
                        }
                        drawing.markBoundingRectangleDirty();
                    } else if (cursorContainer.getNumCursorsOfGivenType(MyCursor.TYPE_DIRECT_MANIPULATION) == 1) {
                        // convert cursor positions to world space
                        Point2D cursor_currentPosition_worldSpace = gw.convertPixelsToWorldSpaceUnits(cursor.getCurrentPosition());
                        Point2D cursor_previousPosition_worldSpace = gw.convertPixelsToWorldSpaceUnits(cursor.getPreviousPosition());

                        // compute translation vector
                        Vector2D translationVector = Point2D.diff(cursor_currentPosition_worldSpace, cursor_previousPosition_worldSpace);

                        // apply the translation to the selected strokes
                        for (Stroke s : selectedStrokes) {
                            for (Point2D p : s.getPoints()) {
                                p.copy(Point2D.sum(p, translationVector));
                            }
                            s.markBoundingRectangleDirty();
                        }
                        drawing.markBoundingRectangleDirty();
                    }
                }
            }
        }

        return true; // request a redraw
    }
}

