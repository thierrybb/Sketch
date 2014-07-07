package ca.etsmtl.sketch.graphic.util;

import java.util.ArrayList;

import ca.etsmtl.sketch.graphic.GraphicsWrapper;
import ca.etsmtl.sketch.graphic.shape.AlignedRectangle2D;
import ca.etsmtl.sketch.graphic.shape.Point2D;

public class Palette {
    public static final float alpha = 0.3f; // if between 0 and 1, the palette
    public int width, height; // in pixels
    public int x0, y0; // in pixels
    // is drawn semi-transparent
    public ArrayList<PaletteButton> buttons = null;

    // These variables are initialized in the contructor,
    // to save the index of each button,
    // but after that they should never change.
    public int movePalette_buttonIndex;
    public int ink_buttonIndex;
    public int select_buttonIndex;
    public int manipulate_buttonIndex;
    public int camera_buttonIndex;
    public int black_buttonIndex;
    public int red_buttonIndex;
    public int green_buttonIndex;
    public int horizFlip_buttonIndex;
    public int vertFlip_buttonIndex;
    public int remove_buttonIndex;
    public int frameAll_buttonIndex;
    public int frameSelected_buttonIndex;
    public int undo_buttonIndex;
    public int redo_buttonIndex;
    public int savePNG_buttonIndex;
    public int saveSVG_buttonIndex;
    public int sendMailPNG_buttonIndex;
    public int sendMailSVG_buttonIndex;
    public int start_server;
    public int connect_IP; // for clients to connect to a server, using a
    // user-entered IP address for the server
    public int connect_mul; // for clients to connect to a server, automatically
    // discovering the server that is multicasting its
    // identity
    public int stop_connection;

    public int currentlyActiveModalButton; // could be equal to any of
    // ink_buttonIndex,
    // select_buttonIndex,
    // manipulate_buttonIndex,
    // camera_buttonIndex

    public int currentlyActiveColorButton; // could be equal to any of
    // black_buttonIndex,
    // red_buttonIndex,
    // green_buttonIndex
    public float current_red = 0;
    public float current_green = 0;
    public float current_blue = 0;

    public Palette() {
        final int W = PaletteButton.width;
        final int H = PaletteButton.height;
        PaletteButton b = null;
        buttons = new ArrayList<PaletteButton>();

        // Create first row of buttons

        b = new PaletteButton(0, 0, "Move", "Drag on this button to move the palette.", false);
        movePalette_buttonIndex = buttons.size();
        buttons.add(b);

        b = new PaletteButton(W, 0, "Ink", "When active, use other fingers to draw ink strokes.", true);
        ink_buttonIndex = buttons.size();
        buttons.add(b);

        b = new PaletteButton(2 * W, 0, "Select", "When active, use another finger to select strokes.", true);
        select_buttonIndex = buttons.size();
        buttons.add(b);

        b = new PaletteButton(3 * W, 0, "Manip.",
                "When active, use one or two other fingers to directly manipulate the selection.", true);
        manipulate_buttonIndex = buttons.size();
        buttons.add(b);

        b = new PaletteButton(4 * W, 0, "Camera",
                "When active, use one or two other fingers to directly manipulate the camera.", true);
        camera_buttonIndex = buttons.size();
        buttons.add(b);

        // Create second row of buttons

        b = new PaletteButton(0, H, "Black", "Changes ink color.", true);
        black_buttonIndex = buttons.size();
        buttons.add(b);

        b = new PaletteButton(W, H, "Red", "Changes ink color.", true);
        red_buttonIndex = buttons.size();
        buttons.add(b);

        b = new PaletteButton(2 * W, H, "Green", "Changes ink color.", true);
        green_buttonIndex = buttons.size();
        buttons.add(b);

        b = new PaletteButton(3 * W, H, "Hor. Flip", "Flip the selection horizontally (around a vertical axis).", false);
        horizFlip_buttonIndex = buttons.size();
        buttons.add(b);

        b = new PaletteButton(4 * W, H, "Ver. Flip", "Flip the selection vertically (around an horizontal axis).",
                false);
        vertFlip_buttonIndex = buttons.size();
        buttons.add(b);

        // Create third row of buttons

        b = new PaletteButton(0, 2 * H, "Remove", "Remove the selection", false);
        remove_buttonIndex = buttons.size();
        buttons.add(b);

        b = new PaletteButton(W, 2 * H, "Frame all", "Frames the entire drawing.", false);
        frameAll_buttonIndex = buttons.size();
        buttons.add(b);

        b = new PaletteButton(2 * W, 2 * H, "Frame sel", "Frames the selection.", false);
        frameSelected_buttonIndex = buttons.size();
        buttons.add(b);

        b = new PaletteButton(3 * W, 2 * H, "Undo", "Undo the last action", false);
        undo_buttonIndex = buttons.size();
        buttons.add(b);

        b = new PaletteButton(4 * W, 2 * H, "Redo", "Redo the action which was undo", false);
        redo_buttonIndex = buttons.size();
        buttons.add(b);

        // Create fourth row of buttons

        b = new PaletteButton(0, 3 * H, "Save PNG", "Save in PNG", false);
        savePNG_buttonIndex = buttons.size();
        buttons.add(b);

        b = new PaletteButton(W, 3 * H, "Save SVG", "Save in SVG", false);
        saveSVG_buttonIndex = buttons.size();
        buttons.add(b);

        b = new PaletteButton(2 * W, 3 * H, "Email PNG", "Send the PNG by mail", false);
        sendMailPNG_buttonIndex = buttons.size();
        buttons.add(b);

        b = new PaletteButton(3 * W, 3 * H, "Email SVG", "Send the SVG by mail", false);
        sendMailSVG_buttonIndex = buttons.size();
        buttons.add(b);

        // Create fifth row of buttons

        b = new PaletteButton(0, 4 * H, "Start Server", "Start the connection as the server", false);
        start_server = buttons.size();
        buttons.add(b);

        b = new PaletteButton(W, 4 * H, "IP", "Start a client with server's IP address", false);
        connect_IP = buttons.size();
        buttons.add(b);

        b = new PaletteButton(2 * W, 4 * H, "Multicast", "Start a client with multicast", false);
        connect_mul = buttons.size();
        buttons.add(b);

        b = new PaletteButton(3 * W, 4 * H, "Stop", "Stop the connection", false);
        stop_connection = buttons.size();
        buttons.add(b);

        // Initialize remaining state

        buttons.get(ink_buttonIndex).isPressed = true;
        currentlyActiveModalButton = ink_buttonIndex;
        buttons.get(black_buttonIndex).isPressed = true;
        currentlyActiveColorButton = black_buttonIndex;
        current_red = current_green = current_blue = 0;

        // Figure out the width and height of the palette.
        // To do this, compute a bounding rectangle.
        AlignedRectangle2D boundingRectangle = new AlignedRectangle2D();
        for (int j = 0; j < buttons.size(); ++j) {
            boundingRectangle.bound(buttons.get(j).getBoundingRectangle());
        }
        // Note that the bounding rectangle contains coordinates in the
        // palette's local space.
        // We only store the width and height of the bounding rectangle.
        width = Math.round(boundingRectangle.getDiagonal().x());
        height = Math.round(boundingRectangle.getDiagonal().y());
    }

    public AlignedRectangle2D getBoundingRectangle() {
        return new AlignedRectangle2D(new Point2D(x0, y0), new Point2D(x0 + width, y0 + height));
    }

    public Point2D getCenter() {
        return getBoundingRectangle().getCenter();
    }

    public boolean contains(float x, float y) {
        return getBoundingRectangle().contains(new Point2D(x, y));
    }

    // returns -1 if no button contains the given point
    public int indexOfButtonContainingTheGivenPoint(float x, float y) {
        for (int j = 0; j < buttons.size(); ++j) {
            PaletteButton b = buttons.get(j);
            if (b.contains(x - x0, y - y0)) // the subtraction converts the
                // coordinates to the palette's
                // local coordinate system
                return j;
        }
        return -1;
    }

    public void draw(GraphicsWrapper gw) {
        // draw border
        gw.setColor(0, 0, 0);
        gw.drawRect(x0, y0, width, height);

        for (PaletteButton b : buttons) {
            b.draw(x0, y0, gw);
        }
    }
}
