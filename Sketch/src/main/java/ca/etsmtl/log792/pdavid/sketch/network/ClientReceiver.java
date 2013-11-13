package ca.etsmtl.log792.pdavid.sketch.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ca.etsmtl.log792.pdavid.sketch.graphic.Drawing;
import ca.etsmtl.log792.pdavid.sketch.graphic.GraphicsWrapper;
import ca.etsmtl.log792.pdavid.sketch.graphic.MultitouchFramework;
import ca.etsmtl.log792.pdavid.sketch.graphic.util.Constant;

public class ClientReceiver implements Runnable {

    NetworkClient start;
    BufferedReader in;
    Drawing drawing;
    MultitouchFramework mf;
    GraphicsWrapper gw;

    public ClientReceiver(NetworkClient sc, Drawing drawing, MultitouchFramework mf, GraphicsWrapper gw) {
        this.start = sc;
        this.drawing = drawing;
        this.mf = mf;
        this.gw = gw;
    }

    public void run() {
        if (!start.socket.isClosed()) {
            while (true) {
                try {
                    in = new BufferedReader(new InputStreamReader(start.socket.getInputStream()));
                    String message_distant = in.readLine();
                    if (message_distant != null) {
                        drawing.updateDrawing(message_distant, Constant.NM_CLIENT, null, start, null);
                        if (Constant.autoFrameWhenUpdatingOverNetwork)
                            gw.frame(drawing.getBoundingRectangle(), true);
                        mf.requestRedrawInUiThread();
                        MultitouchFramework.log("Received: " + message_distant.substring(0, 4));
                    }
                } catch (IOException e) {
                    MultitouchFramework.log("Problem receiving message for client");
                    MultitouchFramework.log(" " + e.getMessage());
                    // e.printStackTrace();
                }
            }
        }
    }
}
