package ca.etsmtl.log792.pdavid.sketch.graphic;

import java.net.InetAddress;
import java.util.ArrayList;

import ca.etsmtl.log792.pdavid.sketch.graphic.shape.AlignedRectangle2D;
import ca.etsmtl.log792.pdavid.sketch.graphic.shape.Point2D;
import ca.etsmtl.log792.pdavid.sketch.graphic.shape.Stroke;
import ca.etsmtl.log792.pdavid.sketch.graphic.shape.Text;
import ca.etsmtl.log792.pdavid.sketch.graphic.util.ActionTask;
import ca.etsmtl.log792.pdavid.sketch.graphic.util.Command;
import ca.etsmtl.log792.pdavid.sketch.graphic.util.Constant;
import ca.etsmtl.log792.pdavid.sketch.network.NetworkClient;
import ca.etsmtl.log792.pdavid.sketch.network.NetworkServer;

// This stores a set of strokes.
// Even if there are multiple users interacting with the window at the same
// time,
// they all interact with a single instance of this class.
public class Drawing {

    public ArrayList<Stroke> strokes = new ArrayList<Stroke>();
    public ArrayList<Text> texts = new ArrayList<Text>();
    public ActionTask actionsMade = new ActionTask();
    public ActionTask actionsUndo = new ActionTask();

    private AlignedRectangle2D boundingRectangle = new AlignedRectangle2D();
    private boolean isBoundingRectangleDirty = false;

    public void addStroke(Stroke s, int networkMode, NetworkServer server, NetworkClient client, InetAddress IP_sender) {
        strokes.add(s);
        isBoundingRectangleDirty = true;
        ArrayList<Stroke> ss = new ArrayList<Stroke>();
        ss.add(s);
        actionsMade.push(new Command(ss, true));
//        String message = this.toString(s, true);
//        if (networkMode == Constant.NM_SERVER) {
//            this.forwardAsServer(message, server, IP_sender);
//        } else if (networkMode == Constant.NM_CLIENT) {
//            this.forwardAsClient(message, client);
//        }
    }

    public void removeStroke(Stroke s, int networkMode, NetworkServer server, NetworkClient client,
                             InetAddress IP_sender) {
        strokes.remove(s);
//        String message = this.toString(s, false);
//        if (networkMode == Constant.NM_SERVER) {
//            this.forwardAsServer(message, server, IP_sender);
//        } else if (networkMode == Constant.NM_CLIENT) {
//            this.forwardAsClient(message, client);
//        }
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

    public void updateDrawing(String message, int networkMode, NetworkServer server, NetworkClient client,
                              InetAddress IP_sender) {
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
            this.addStroke(newStroke, networkMode, server, client, IP_sender);
        } else if (words[0].equals(Constant.MESSAGE_PREFIX_TO_REMOVE_STROKE)) {
            for (Stroke s : this.strokes) {
                if (s.egal(newStroke)) {
                    this.removeStroke(s, networkMode, server, client, IP_sender);
                    break;
                }
            }
        }
    }

//    public void forwardAsServer(String message, NetworkServer server, InetAddress IP_sender) {
//        ServerSender emission = new ServerSender(server, message, IP_sender);
//        new Thread(emission).start();
//    }
//
//    public void forwardAsClient(String message, NetworkClient client) {
//        ClientSender emission = new ClientSender(client, message);
//        new Thread(emission).start();
//    }
}
