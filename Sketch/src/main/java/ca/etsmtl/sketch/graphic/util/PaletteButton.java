package ca.etsmtl.sketch.graphic.util;

import ca.etsmtl.sketch.graphic.GraphicsWrapper;
import ca.etsmtl.sketch.graphic.shape.AlignedRectangle2D;
import ca.etsmtl.sketch.graphic.shape.Point2D;

public class PaletteButton {
    public static final int width = Constant.BUTTON_WIDTH; // in pixels
    public static final int height = Constant.BUTTON_HEIGHT; // in pixels
    public int x0, y0; // coordinates of upper left corner of button, in pixels,
    public boolean isPressed = false; // if true, the button is drawn
    // differently
    public boolean isSticky = false; // if true, the button remains pressed
    // with respect to the upper left corner of the palette
    // that contains us
    String label = "";
    String tooltip = "";
    // after the finger has lifted off
    // (useful for modal or radio buttons)

    public PaletteButton(int x0, int y0, String label, String tooltip, boolean isSticky) {
        this.x0 = x0;
        this.y0 = y0;
        this.label = label;
        this.tooltip = tooltip;
        this.isSticky = isSticky;
    }

    // returns bounding box in the local space of the palette
    public AlignedRectangle2D getBoundingRectangle() {
        return new AlignedRectangle2D(new Point2D(x0, y0), new Point2D(x0 + width, y0 + height));
    }

    public boolean contains(float x, float y // pixel coordinates in the local
                            // space of the palette
    ) {
        return getBoundingRectangle().contains(new Point2D(x, y));
    }

    public void draw(int palette_x, int palette_y, // upper left corner of the
                     // palette that contains us,
                     // in pixels
                     GraphicsWrapper gw) {
        // draw background
        if (isPressed) {
            gw.setColor(0, 0, 0, Palette.alpha);
            gw.fillRect(palette_x + x0, palette_y + y0, width, height);
            // set the foreground color in preparation for drawing the label
            gw.setColor(1, 1, 1);
        } else {
            gw.setColor(1, 1, 1, Palette.alpha);
            gw.fillRect(palette_x + x0, palette_y + y0, width, height);
            // draw border
            gw.setColor(0, 0, 0);
            gw.drawRect(palette_x + x0, palette_y + y0, width, height);
        }
        // draw text label
        int stringWidth = Math.round(gw.stringWidth(label));
        gw.drawString(palette_x + x0 + (width - stringWidth) / 2, palette_y + y0 + height / 2 + Constant.TEXT_HEIGHT
                / 2, label);
    }
}
