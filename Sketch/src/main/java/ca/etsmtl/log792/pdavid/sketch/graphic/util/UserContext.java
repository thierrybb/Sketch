package ca.etsmtl.log792.pdavid.sketch.graphic.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ca.etsmtl.log792.pdavid.sketch.graphic.CursorContainer;
import ca.etsmtl.log792.pdavid.sketch.graphic.Drawing;
import ca.etsmtl.log792.pdavid.sketch.graphic.GraphicsWrapper;
import ca.etsmtl.log792.pdavid.sketch.graphic.MultitouchFramework;
import ca.etsmtl.log792.pdavid.sketch.graphic.MyCursor;
import ca.etsmtl.log792.pdavid.sketch.graphic.Vector2D;
import ca.etsmtl.log792.pdavid.sketch.graphic.shape.AlignedRectangle2D;
import ca.etsmtl.log792.pdavid.sketch.graphic.shape.Point2D;
import ca.etsmtl.log792.pdavid.sketch.graphic.shape.Stroke;
import ca.etsmtl.log792.pdavid.sketch.network.NetworkClient;
import ca.etsmtl.log792.pdavid.sketch.network.NetworkClientStopper;
import ca.etsmtl.log792.pdavid.sketch.network.NetworkServer;
import ca.etsmtl.log792.pdavid.sketch.network.NetworkServerStopper;

public class UserContext {
    private Palette palette = new Palette();
    private CursorContainer cursorContainer = new CursorContainer();
    private Drawing drawing = null;
    private NetworkServer server = null;
    private NetworkClient client = null;

    private int networkMode = Constant.NM_NONE;

    private ArrayList<Stroke> selectedStrokes = new ArrayList<Stroke>();

    public UserContext(Drawing d) {
        drawing = d;
    }

    public void setPositionOfCenterOfPalette(float x, float y) {
        palette.x0 = Math.round(x - palette.width / 2);
        palette.y0 = Math.round(y - palette.height / 2);
    }

    /**
     * @param delta_x displacement, in pixels
     * @param delta_y displacement, in pixels
     */
    public void movePalette(float delta_x, float delta_y) {
        palette.x0 += Math.round(delta_x);
        palette.y0 += Math.round(delta_y);
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

    /**
     * returns the distance between the given point and the center of the palette of this user context
     *
     * @param x pixel coordinates
     * @param y pixel coordinates
     * @return the distance between x & y
     */
    public float distanceToPalette(float x, float y) {
        return Point2D.diff(palette.getCenter(), new Point2D(x, y)).length();
    }

    public void draw(GraphicsWrapper gw) {

        palette.draw(gw);

        // draw filled rectangles over the selected strokes
        gw.setCoordinateSystemToWorldSpaceUnits();
        for (Stroke s : selectedStrokes) {
            AlignedRectangle2D r = s.getBoundingRectangle();
            gw.setColor(1.0f, 0.5f, 0, 0.2f); // transparent orange
            Vector2D diagonal = r.getDiagonal();
            gw.fillRect(r.getMin().x(), r.getMin().y(), diagonal.x(), diagonal.y());
        }

        gw.setCoordinateSystemToPixels();

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

    private static String getDateAndTimeAsString() {
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd'_at_'hh-mm-ss");
        return ft.format(date);
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
    public boolean processMultitouchInputEvent(int id, float x, float y, int type, final GraphicsWrapper gw, final MultitouchFramework mf, boolean doOtherUserContextsHaveCursors) {
        // Find the cursor that corresponds to the event id, if such a cursor already exists.
        // If no such cursor exists, the below index will be -1, and the
        // reference to cursor will be null.
        int cursorIndex = cursorContainer.findIndexOfCursorById(id);
        MyCursor cursor = (cursorIndex == -1) ? null : cursorContainer.getCursorByIndex(cursorIndex);

        if (cursor == null) {

            if (type == MultitouchFramework.TOUCH_EVENT_UP) {
                // This should never happen, but if it does, just ignore the
                // event.
                return false;
            }

            // The event does not correspond to any existing cursor.
            // In other words, this is a new finger touching the screen.
            // The event is probably of type TOUCH_EVENT_DOWN.
            // A new cursor will need to be created for the event.

            if (palette.contains(x, y)) {
                // The event occurred inside the palette.

                if (cursorContainer.getNumCursors() == 0) {
                    // There are currently no cursors engaged for this user
                    // context.
                    // In other words, this new finger is the only finger for
                    // the user context.
                    // So, we allow the event for the new finger to activate a
                    // button in the palette.
                    // We branch according to the button under the event.
                    //
                    int indexOfButton = palette.indexOfButtonContainingTheGivenPoint(x, y);
                    if (indexOfButton == palette.movePalette_buttonIndex) {
                        palette.buttons.get(indexOfButton).isPressed = true;

                        // Cause a new cursor to be created to keep track of
                        // this event id in the future
                        cursorIndex = cursorContainer.updateCursorById(id, x, y);
                        cursor = cursorContainer.getCursorByIndex(cursorIndex);
                        cursor.setType(MyCursor.TYPE_INTERACTING_WITH_WIDGET, indexOfButton);
                    } else if (indexOfButton == palette.ink_buttonIndex || indexOfButton == palette.select_buttonIndex
                            || indexOfButton == palette.manipulate_buttonIndex
                            || indexOfButton == palette.camera_buttonIndex) {
                        // We transition to the mode corresponding to the button
                        palette.buttons.get(palette.currentlyActiveModalButton).isPressed = false;
                        palette.currentlyActiveModalButton = indexOfButton;
                        palette.buttons.get(indexOfButton).isPressed = true;

                        // Cause a new cursor to be created to keep track of
                        // this event id in the future
                        cursorIndex = cursorContainer.updateCursorById(id, x, y);
                        cursor = cursorContainer.getCursorByIndex(cursorIndex);
                        cursor.setType(MyCursor.TYPE_INTERACTING_WITH_WIDGET, indexOfButton);
                    } else if (indexOfButton == palette.black_buttonIndex || indexOfButton == palette.red_buttonIndex
                            || indexOfButton == palette.green_buttonIndex) {
                        // We transition to the color corresponding to the
                        // button
                        palette.buttons.get(palette.currentlyActiveColorButton).isPressed = false;
                        palette.currentlyActiveColorButton = indexOfButton;
                        palette.buttons.get(indexOfButton).isPressed = true;

                        if (indexOfButton == palette.black_buttonIndex) {
                            palette.current_red = 0;
                            palette.current_green = 0;
                            palette.current_blue = 0;
                        } else if (indexOfButton == palette.red_buttonIndex) {
                            palette.current_red = 1.0f;
                            palette.current_green = 0;
                            palette.current_blue = 0;
                        } else if (indexOfButton == palette.green_buttonIndex) {
                            palette.current_red = 0;
                            palette.current_green = 1.0f;
                            palette.current_blue = 0;
                        }

                        // Cause a new cursor to be created to keep track of
                        // this event id in the future
                        cursorIndex = cursorContainer.updateCursorById(id, x, y);
                        cursor = cursorContainer.getCursorByIndex(cursorIndex);
                        cursor.setType(MyCursor.TYPE_INTERACTING_WITH_WIDGET, indexOfButton);
                    } else if (indexOfButton == palette.horizFlip_buttonIndex) {
                        palette.buttons.get(indexOfButton).isPressed = true;

                        // Cause a new cursor to be created to keep track of
                        // this event id in the future
                        cursorIndex = cursorContainer.updateCursorById(id, x, y);
                        cursor = cursorContainer.getCursorByIndex(cursorIndex);
                        cursor.setType(MyCursor.TYPE_INTERACTING_WITH_WIDGET, indexOfButton);

                        // Flip the selected strokes horizontally (around a
                        // vertical axis)
                        for (Stroke s : selectedStrokes) {
                            Point2D center = s.getBoundingRectangle().getCenter();
                            for (Point2D p : s.getPoints()) {
                                p.copy(center.x() - (p.x() - center.x()), p.y());
                            }
                            s.markBoundingRectangleDirty();
                        }
                        drawing.markBoundingRectangleDirty();
                    } else if (indexOfButton == palette.vertFlip_buttonIndex) {
                        palette.buttons.get(indexOfButton).isPressed = true;

                        // Cause a new cursor to be created to keep track of
                        // this event id in the future
                        cursorIndex = cursorContainer.updateCursorById(id, x, y);
                        cursor = cursorContainer.getCursorByIndex(cursorIndex);
                        cursor.setType(MyCursor.TYPE_INTERACTING_WITH_WIDGET, indexOfButton);

                        // Flip the selected strokes vertically (around a
                        // horizontal axis)
                        for (Stroke s : selectedStrokes) {
                            Point2D center = s.getBoundingRectangle().getCenter();
                            for (Point2D p : s.getPoints()) {
                                p.copy(p.x(), center.y() - (p.y() - center.y()));
                            }
                            s.markBoundingRectangleDirty();
                        }
                        drawing.markBoundingRectangleDirty();
                    } else if (indexOfButton == palette.remove_buttonIndex) {
                        palette.buttons.get(indexOfButton).isPressed = true;
                        // Cause a new cursor to be created to keep track of
                        // this event id in the future
                        cursorIndex = cursorContainer.updateCursorById(id, x, y);
                        cursor = cursorContainer.getCursorByIndex(cursorIndex);
                        cursor.setType(MyCursor.TYPE_INTERACTING_WITH_WIDGET, indexOfButton);
                        ArrayList<Stroke> temp = new ArrayList<Stroke>();
                        for (Stroke s : drawing.strokes) {
                            temp.add(s);
                        }
                        for (Stroke s : selectedStrokes) {
                            for (Stroke ss : temp) {
                                if (s.egal(ss)) {
                                    drawing.removeStroke(ss, networkMode, server, client, null);
                                }
                            }
                        }
                        if (!selectedStrokes.isEmpty()) {
                            drawing.actionsMade.push(new Command(selectedStrokes, false));
                        }
                        selectedStrokes.clear();
                        temp.clear();
                        drawing.markBoundingRectangleDirty();
                    } else if (indexOfButton == palette.frameAll_buttonIndex) {
                        palette.buttons.get(indexOfButton).isPressed = true;

                        // Cause a new cursor to be created to keep track of
                        // this event id in the future
                        cursorIndex = cursorContainer.updateCursorById(id, x, y);
                        cursor = cursorContainer.getCursorByIndex(cursorIndex);
                        cursor.setType(MyCursor.TYPE_INTERACTING_WITH_WIDGET, indexOfButton);

                        // Frame the entire drawing
                        gw.frame(drawing.getBoundingRectangle(), true);
                    } else if (indexOfButton == palette.frameSelected_buttonIndex) {
                        palette.buttons.get(indexOfButton).isPressed = true;
                        // Cause a new cursor to be created to keep track of
                        // this event id in the future
                        cursorIndex = cursorContainer.updateCursorById(id, x, y);
                        cursor = cursorContainer.getCursorByIndex(cursorIndex);
                        cursor.setType(MyCursor.TYPE_INTERACTING_WITH_WIDGET, indexOfButton);

                        // Frame the selection
                        Drawing selected = new Drawing(); // TODO XXX
                        // inefficient
                        selected.strokes.clear();
                        for (Stroke s : selectedStrokes) {
                            selected.addStroke(s, networkMode, server, client, null);
                        }
                        gw.frame(selected.getBoundingRectangle(), true);
                    } else if (indexOfButton == palette.undo_buttonIndex) {
                        palette.buttons.get(indexOfButton).isPressed = true;

                        // Cause a new cursor to be created to keep track of
                        // this event id in the future
                        cursorIndex = cursorContainer.updateCursorById(id, x, y);
                        cursor = cursorContainer.getCursorByIndex(cursorIndex);
                        cursor.setType(MyCursor.TYPE_INTERACTING_WITH_WIDGET, indexOfButton);

                        if (!drawing.actionsMade.commands.isEmpty()) {
                            Command toUndo = drawing.actionsMade.pop();
                            if (toUndo.add) {
                                for (Stroke s : toUndo.strokes) {
                                    drawing.removeStroke(s, networkMode, server, client, null);
                                }
                            } else {
                                for (Stroke s : toUndo.strokes) {
                                    drawing.addStroke(s, networkMode, server, client, null);
                                }
                            }
                            drawing.actionsUndo.push(toUndo);
                        }
                    } else if (indexOfButton == palette.redo_buttonIndex) {
                        palette.buttons.get(indexOfButton).isPressed = true;

                        // Cause a new cursor to be created to keep track of
                        // this event id in the future
                        cursorIndex = cursorContainer.updateCursorById(id, x, y);
                        cursor = cursorContainer.getCursorByIndex(cursorIndex);
                        cursor.setType(MyCursor.TYPE_INTERACTING_WITH_WIDGET, indexOfButton);

                        if (!drawing.actionsUndo.commands.isEmpty()) {
                            Command toRedo = drawing.actionsUndo.pop();
                            if (toRedo.add) {
                                for (Stroke s : toRedo.strokes) {
                                    drawing.addStroke(s, networkMode, server, client, null);
                                }
                            } else {
                                for (Stroke s : toRedo.strokes) {
                                    drawing.removeStroke(s, networkMode, server, client, null);
                                }
                            }
                            drawing.actionsMade.push(toRedo);
                        }
                    } else if (indexOfButton == palette.savePNG_buttonIndex) {
                        palette.buttons.get(indexOfButton).isPressed = true;

                        // Cause a new cursor to be created to keep track of
                        // this event id in the future
                        cursorIndex = cursorContainer.updateCursorById(id, x, y);
                        cursor = cursorContainer.getCursorByIndex(cursorIndex);
                        cursor.setType(MyCursor.TYPE_INTERACTING_WITH_WIDGET, indexOfButton);

//                        String filename = getDateAndTimeAsString() + "." + Constant.PNG_STRING;
//                        mf.savePNG(drawing, filename);
                    } else if (indexOfButton == palette.saveSVG_buttonIndex) {
                        palette.buttons.get(indexOfButton).isPressed = true;

                        // Cause a new cursor to be created to keep track of
                        // this event id in the future
                        cursorIndex = cursorContainer.updateCursorById(id, x, y);
                        cursor = cursorContainer.getCursorByIndex(cursorIndex);
                        cursor.setType(MyCursor.TYPE_INTERACTING_WITH_WIDGET, indexOfButton);

//                        String filename = getDateAndTimeAsString() + "." + Constant.SVG_STRING;
//                        mf.generateSVG(drawing, filename);
                    } else if (indexOfButton == palette.sendMailPNG_buttonIndex) {
                        palette.buttons.get(indexOfButton).isPressed = true;

                        // Cause a new cursor to be created to keep track of
                        // this event id in the future
                        cursorIndex = cursorContainer.updateCursorById(id, x, y);
                        cursor = cursorContainer.getCursorByIndex(cursorIndex);
                        cursor.setType(MyCursor.TYPE_INTERACTING_WITH_WIDGET, indexOfButton);

                        String filename = getDateAndTimeAsString() + "." + Constant.PNG_STRING;
//                        mf.savePNG(drawing, filename);
//                        mf.sendMailWithAttachedFile(filename, Constant.PNG_STRING);
                    } else if (indexOfButton == palette.sendMailSVG_buttonIndex) {
                        palette.buttons.get(indexOfButton).isPressed = true;

                        // Cause a new cursor to be created to keep track of
                        // this event id in the future
                        cursorIndex = cursorContainer.updateCursorById(id, x, y);
                        cursor = cursorContainer.getCursorByIndex(cursorIndex);
                        cursor.setType(MyCursor.TYPE_INTERACTING_WITH_WIDGET, indexOfButton);

                        String filename = getDateAndTimeAsString() + "." + Constant.SVG_STRING;
//                        mf.generateSVG(drawing, filename);
//                        mf.sendMailWithAttachedFile(filename, Constant.SVG_STRING);
                    } else if (indexOfButton == palette.start_server && networkMode == Constant.NM_NONE) {
                        palette.buttons.get(indexOfButton).isPressed = true;

                        // Cause a new cursor to be created to keep track of
                        // this event id in the future
                        cursorIndex = cursorContainer.updateCursorById(id, x, y);
                        cursor = cursorContainer.getCursorByIndex(cursorIndex);
                        cursor.setType(MyCursor.TYPE_INTERACTING_WITH_WIDGET, indexOfButton);

                        networkMode = Constant.NM_SERVER; // TODO XXX should
                        // only be set if we
                        // are successful

                        server = new NetworkServer(drawing, mf, gw);
                        new Thread(server).start();
                    } else if (indexOfButton == palette.connect_IP && networkMode == Constant.NM_NONE) {
                        palette.buttons.get(indexOfButton).isPressed = true;

                        // Cause a new cursor to be created to keep track of
                        // this event id in the future
                        cursorIndex = cursorContainer.updateCursorById(id, x, y);
                        cursor = cursorContainer.getCursorByIndex(cursorIndex);
                        cursor.setType(MyCursor.TYPE_INTERACTING_WITH_WIDGET, indexOfButton);

                        networkMode = Constant.NM_CLIENT; // TODO XXX should
                        // only be set if we
                        // are successful

//                        IPAddressDialog dialogBox = new IPAddressDialog(mf);
//                        dialogBox.addObserver(new Observer() {
//                            public void update(Observable observable, Object data) {
//                                MultitouchFramework.log("Entered IP address: " + (String) data);
//                                client = new NetworkClient((String) data, drawing, mf, gw);
//                                new Thread(client).start();
//                            }
//                        });
                    } else if (indexOfButton == palette.connect_mul && networkMode == Constant.NM_NONE) {
                        palette.buttons.get(indexOfButton).isPressed = true;

                        // Cause a new cursor to be created to keep track of
                        // this event id in the future
                        cursorIndex = cursorContainer.updateCursorById(id, x, y);
                        cursor = cursorContainer.getCursorByIndex(cursorIndex);
                        cursor.setType(MyCursor.TYPE_INTERACTING_WITH_WIDGET, indexOfButton);

                        networkMode = Constant.NM_CLIENT; // TODO XXX should
                        // only be set if we
                        // are successful

                        client = new NetworkClient(drawing, mf, gw);
                        new Thread(client).start();
                    } else if (indexOfButton == palette.stop_connection && networkMode != Constant.NM_NONE) {
                        palette.buttons.get(indexOfButton).isPressed = true;

                        // Cause a new cursor to be created to keep track of
                        // this event id in the future
                        cursorIndex = cursorContainer.updateCursorById(id, x, y);
                        cursor = cursorContainer.getCursorByIndex(cursorIndex);
                        cursor.setType(MyCursor.TYPE_INTERACTING_WITH_WIDGET, indexOfButton);

                        if (networkMode == Constant.NM_SERVER) {
                            NetworkServerStopper stop = new NetworkServerStopper(server);
                            new Thread(stop).start();
                        } else if (networkMode == Constant.NM_CLIENT) {
                            NetworkClientStopper stop = new NetworkClientStopper(client);
                            new Thread(stop).start();
                        }
                        networkMode = Constant.NM_NONE;
                    } else {
                        // The event occurred on some part of the palette where
                        // there are no buttons.
                        // We cause a new cursor to be created to keep track of
                        // this event id in the future.
                        cursorIndex = cursorContainer.updateCursorById(id, x, y);
                        cursor = cursorContainer.getCursorByIndex(cursorIndex);

                        // Prevent the cursor from doing anything in the future.
                        cursor.setType(MyCursor.TYPE_NOTHING);
                    }
                } else {
                    // There is already at least one cursor.
                    // In other words, there is already one or more other
                    // fingers being tracked in this user context
                    // (possibly on a palette button, and/or over the drawing).
                    // To keep things simple, we prevent this new finger from
                    // doing anything.

                    // We create a new cursor ...
                    cursorIndex = cursorContainer.updateCursorById(id, x, y);
                    cursor = cursorContainer.getCursorByIndex(cursorIndex);

                    // ... and prevent the cursor from doing anything in the
                    // future.
                    cursor.setType(MyCursor.TYPE_NOTHING);
                }
            } else {
                // The event did not occur inside the palette.
                // This new finger may have been placed down to start
                // drawing a stroke, or start camera manipulation, etc.
                // We branch according to the current mode.
                //
                if (palette.currentlyActiveModalButton == palette.ink_buttonIndex) {
                    // start drawing a stroke
                    cursorIndex = cursorContainer.updateCursorById(id, x, y);
                    cursor = cursorContainer.getCursorByIndex(cursorIndex);
                    cursor.setType(MyCursor.TYPE_INKING);
                } else if (palette.currentlyActiveModalButton == palette.select_buttonIndex) {
                    // The new finger should only start selecting
                    // if there is not already another finger performing
                    // selection.
                    if (cursorContainer.getNumCursorsOfGivenType(MyCursor.TYPE_SELECTION) == 0) {
                        cursorIndex = cursorContainer.updateCursorById(id, x, y);
                        cursor = cursorContainer.getCursorByIndex(cursorIndex);
                        cursor.setType(MyCursor.TYPE_SELECTION);
                    } else {
                        cursorIndex = cursorContainer.updateCursorById(id, x, y);
                        cursor = cursorContainer.getCursorByIndex(cursorIndex);
                        cursor.setType(MyCursor.TYPE_NOTHING);
                    }
                } else if (palette.currentlyActiveModalButton == palette.manipulate_buttonIndex) {
                    // The new finger should only manipulate the selection
                    // if there are not already 2 fingers manipulating the
                    // selection.
                    if (cursorContainer.getNumCursorsOfGivenType(MyCursor.TYPE_DIRECT_MANIPULATION) < 2) {
                        cursorIndex = cursorContainer.updateCursorById(id, x, y);
                        cursor = cursorContainer.getCursorByIndex(cursorIndex);
                        cursor.setType(MyCursor.TYPE_DIRECT_MANIPULATION);
                    } else {
                        cursorIndex = cursorContainer.updateCursorById(id, x, y);
                        cursor = cursorContainer.getCursorByIndex(cursorIndex);
                        cursor.setType(MyCursor.TYPE_NOTHING);
                    }
                } else if (palette.currentlyActiveModalButton == palette.camera_buttonIndex) {
                    // The new finger should only manipulate the camera
                    // if there are not already 2 fingers manipulating the
                    // camera.
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

            }
        } else {
            // The event corresponds to an already existing cursor
            // (and the cursor was probably created during an earlier event of
            // type TOUCH_EVENT_DOWN).
            // The current event is probably of type TOUCH_EVENT_MOVE or
            // TOUCH_EVENT_UP.

            if (type == MultitouchFramework.TOUCH_EVENT_MOVE) {
                // The event is a move event, and corresponds to an existing
                // cursor.
                // Is the location of the event different from the last reported
                // location?
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

                if (type == MultitouchFramework.TOUCH_EVENT_UP)
                    cursorContainer.removeCursorByIndex(cursorIndex);
            } else if (cursor.type == MyCursor.TYPE_INTERACTING_WITH_WIDGET) {
                if (type == MultitouchFramework.TOUCH_EVENT_UP) {
                    // The user lifted their finger off of a palette button.
                    cursorContainer.removeCursorByIndex(cursorIndex);

                    if (!palette.buttons.get(cursor.indexOfButton).isSticky) {
                        palette.buttons.get(cursor.indexOfButton).isPressed = false;
                    }
                } else {
                    // Earlier, the user pressed down on a button in the
                    // palette,
                    // and now they are dragging their finger over the button
                    // (and possibly onto other buttons).
                    // If this is the "move palette" button, we move the
                    // palette.
                    if (cursor.indexOfButton == palette.movePalette_buttonIndex) {
                        movePalette(x - cursor.getCurrentPosition().x(), y - cursor.getCurrentPosition().y());
                    }
                    cursorIndex = cursorContainer.updateCursorById(id, x, y);
                }
            } else if (cursor.type == MyCursor.TYPE_INKING) {
                if (type == MultitouchFramework.TOUCH_EVENT_UP) {
                    // up event
                    cursorIndex = cursorContainer.updateCursorById(id, x, y);

                    // Add the newly drawn stroke to the drawing
                    Stroke newStroke = new Stroke();
                    newStroke.setColor(palette.current_red, palette.current_green, palette.current_blue);
                    for (Point2D p : cursor.getPositions()) {
                        newStroke.addPoint(gw.convertPixelsToWorldSpaceUnits(p));
                    }
                    drawing.addStroke(newStroke, networkMode, server, client, null);

                    cursorContainer.removeCursorByIndex(cursorIndex);
                } else {
                    // drag event; just update the cursor with the new position
                    cursorIndex = cursorContainer.updateCursorById(id, x, y);
                }
            } else if (cursor.type == MyCursor.TYPE_CAMERA_PAN_ZOOM) {
                if (type == MultitouchFramework.TOUCH_EVENT_UP) {
                    // up event
                    cursorContainer.removeCursorByIndex(cursorIndex);
                } else {
                    // drag event
                    cursorIndex = cursorContainer.updateCursorById(id, x, y);

                    if (cursorContainer.getNumCursorsOfGivenType(MyCursor.TYPE_CAMERA_PAN_ZOOM) == 2) {
                        MyCursor cursor0 = cursorContainer.getCursorByType(MyCursor.TYPE_CAMERA_PAN_ZOOM, 0);
                        MyCursor cursor1 = cursorContainer.getCursorByType(MyCursor.TYPE_CAMERA_PAN_ZOOM, 1);
                        gw.panAndZoomBasedOnDisplacementOfTwoPoints(id == cursor0.id ? cursor0.getPreviousPosition()
                                : cursor0.getCurrentPosition(), id == cursor1.id ? cursor1.getPreviousPosition()
                                : cursor1.getCurrentPosition(), cursor0.getCurrentPosition(), cursor1
                                .getCurrentPosition());
                    } else if (cursorContainer.getNumCursorsOfGivenType(MyCursor.TYPE_CAMERA_PAN_ZOOM) == 1) {
                        gw.pan(cursor.getCurrentPosition().x() - cursor.getPreviousPosition().x(), cursor
                                .getCurrentPosition().y() - cursor.getPreviousPosition().y());
                    }
                }
            } else if (cursor.type == MyCursor.TYPE_SELECTION) {
                if (type == MultitouchFramework.TOUCH_EVENT_UP) {
                    // up event
                    cursorIndex = cursorContainer.updateCursorById(id, x, y);

                    // Update the selection
                    if (cursor.doesDragLookLikeLassoGesture()) {
                        // complete a lasso selection

                        // Need to transform the positions of the cursor from
                        // pixels to world space coordinates.
                        // We will store the world space coordinates in the
                        // following data structure.
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
                                gw.convertPixelsToWorldSpaceUnits(cursor.getCurrentPosition()));

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
                if (type == MultitouchFramework.TOUCH_EVENT_UP) {
                    // up event
                    cursorContainer.removeCursorByIndex(cursorIndex);
                } else {
                    // drag event
                    cursorIndex = cursorContainer.updateCursorById(id, x, y);

                    if (cursorContainer.getNumCursorsOfGivenType(MyCursor.TYPE_DIRECT_MANIPULATION) == 2) {
                        MyCursor cursor0 = cursorContainer.getCursorByType(MyCursor.TYPE_DIRECT_MANIPULATION, 0);
                        MyCursor cursor1 = cursorContainer.getCursorByType(MyCursor.TYPE_DIRECT_MANIPULATION, 1);

                        // convert cursor positions to world space
                        Point2D cursor0_currentPosition_worldSpace = gw.convertPixelsToWorldSpaceUnits(cursor0
                                .getCurrentPosition());
                        Point2D cursor1_currentPosition_worldSpace = gw.convertPixelsToWorldSpaceUnits(cursor1
                                .getCurrentPosition());
                        Point2D cursor0_previousPosition_worldSpace = gw.convertPixelsToWorldSpaceUnits(cursor0
                                .getPreviousPosition());
                        Point2D cursor1_previousPosition_worldSpace = gw.convertPixelsToWorldSpaceUnits(cursor1
                                .getPreviousPosition());

                        for (Stroke s : selectedStrokes) {
                            Point2DUtil.transformPointsBasedOnDisplacementOfTwoPoints(s.getPoints(),
                                    id == cursor0.id ? cursor0_previousPosition_worldSpace
                                            : cursor0_currentPosition_worldSpace,
                                    id == cursor1.id ? cursor1_previousPosition_worldSpace
                                            : cursor1_currentPosition_worldSpace, cursor0_currentPosition_worldSpace,
                                    cursor1_currentPosition_worldSpace);
                            s.markBoundingRectangleDirty();
                        }
                        drawing.markBoundingRectangleDirty();
                    } else if (cursorContainer.getNumCursorsOfGivenType(MyCursor.TYPE_DIRECT_MANIPULATION) == 1) {
                        // convert cursor positions to world space
                        Point2D cursor_currentPosition_worldSpace = gw.convertPixelsToWorldSpaceUnits(cursor
                                .getCurrentPosition());
                        Point2D cursor_previousPosition_worldSpace = gw.convertPixelsToWorldSpaceUnits(cursor
                                .getPreviousPosition());

                        // compute translation vector
                        Vector2D translationVector = Point2D.diff(cursor_currentPosition_worldSpace,
                                cursor_previousPosition_worldSpace);

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

