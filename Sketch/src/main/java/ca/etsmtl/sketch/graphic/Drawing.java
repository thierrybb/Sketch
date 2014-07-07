package ca.etsmtl.sketch.graphic;

import java.util.ArrayList;

import ca.etsmtl.sketch.graphic.shape.AlignedRectangle2D;
import ca.etsmtl.sketch.graphic.shape.Point2D;
import ca.etsmtl.sketch.graphic.shape.Stroke;
import ca.etsmtl.sketch.graphic.shape.Text;
import ca.etsmtl.sketch.graphic.util.Constant;

public class Drawing {

    public ArrayList<Stroke> strokes = new ArrayList<Stroke>();
    public ArrayList<Text> texts = new ArrayList<Text>();

    private AlignedRectangle2D boundingRectangle = new AlignedRectangle2D();
    private boolean isBoundingRectangleDirty = false;

    public void addStroke(Stroke s) {
        strokes.add(s);
        isBoundingRectangleDirty = true;
        ArrayList<Stroke> ss = new ArrayList<Stroke>();
        ss.add(s);
    }

    public void removeStroke(Stroke s) {
        strokes.remove(s);
    }

    public void addText(Text t) {
        texts.add(t);
    }

    public synchronized String toString(Stroke s, boolean added) {
        String res = "";
        if (added) {
            res += Constant.MESSAGE_PREFIX_TO_ADD_STROKE + " ";
        } else {
            res += Constant.MESSAGE_PREFIX_TO_REMOVE_STROKE + " ";
        }
        if (s.getColor_green() == 1) {
            res += "g ";
        } else if (s.getColor_red() == 1) {
            res += "r ";
        } else {
            res += "b ";
        }

        for (Point2D p : s.getPoints()) {
            res += p.x() + "_" + p.y() + " ";
        }
        return res;
    }

    public AlignedRectangle2D getBoundingRectangle() {
        if (isBoundingRectangleDirty) {
            boundingRectangle.clear();
            for (Stroke s : strokes) {
                boundingRectangle.bound(s.getBoundingRectangle());
            }
            isBoundingRectangleDirty = false;
        }
        return boundingRectangle;
    }

    public void markBoundingRectangleDirty() {
        isBoundingRectangleDirty = true;
    }

    public void draw(GraphicsWrapper gw) {
        gw.setLineWidth(Constant.INK_THICKNESS_IN_WORLD_SPACE_UNITS);
        for (Stroke s : strokes) {
            s.draw(gw);
        }

        for (Text t : texts) {
            gw.drawString(t.x, t.y, t.text);
        }
        gw.setLineWidth(1);
    }

    public void updateDrawing(String message, int networkMode) {
//        , NetworkServer server, NetworkClient client,InetAddress IP_sender
        String[] words = message.split(" ");
        Stroke newStroke = new Stroke();
        if (words[1].equals("b")) {
            newStroke.setColor(0, 0, 0);
        } else if (words[1].equals("r")) {
            newStroke.setColor(1, 0, 0);
        } else if (words[1].equals("g")) {
            newStroke.setColor(0, 1, 0);
        }
        for (int i = 2; i < words.length; i++) {
            String[] coordinates = words[i].split("_");
            Point2D point = new Point2D(Float.parseFloat(coordinates[0]), Float.parseFloat(coordinates[1]));
            newStroke.addPoint(point);
        }

        if (words[0].equals(Constant.MESSAGE_PREFIX_TO_ADD_STROKE)) {
            this.addStroke(newStroke);
        } else if (words[0].equals(Constant.MESSAGE_PREFIX_TO_REMOVE_STROKE)) {
            for (Stroke s : this.strokes) {
                if (s.egal(newStroke)) {
                    this.removeStroke(s);
                    break;
                }
            }
        }
    }
}
