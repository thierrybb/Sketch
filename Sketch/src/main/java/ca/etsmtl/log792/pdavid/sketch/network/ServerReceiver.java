package ca.etsmtl.log792.pdavid.sketch.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

import ca.etsmtl.log792.pdavid.sketch.graphic.Drawing;
import ca.etsmtl.log792.pdavid.sketch.graphic.GraphicsWrapper;
import ca.etsmtl.log792.pdavid.sketch.graphic.MultitouchSurfaceView;
import ca.etsmtl.log792.pdavid.sketch.graphic.util.Constant;

public class ServerReceiver implements Runnable {

    Socket socketVersClient;
    BufferedReader in;
    Drawing drawing;
    MultitouchSurfaceView mf;
    GraphicsWrapper gw;
    NetworkServer server;

    public ServerReceiver(Socket s, NetworkServer server, Drawing drawing, MultitouchSurfaceView mf, GraphicsWrapper gw) {
        this.socketVersClient = s;
        this.server = server;
        this.drawing = drawing;
        this.mf = mf;
        this.gw = gw;
    }

    public void run() {
//        MultitouchSurfaceView.log("A client has connected itself");

        while (true) {
            if (socketVersClient != null && !socketVersClient.isClosed()) {
                try {
                    in = new BufferedReader(new InputStreamReader(socketVersClient.getInputStream()));
                    String message_distant = in.readLine();
                    if (message_distant != null) {
                        InetAddress IP_sender = socketVersClient.getInetAddress();
                        drawing.updateDrawing(message_distant, Constant.NM_SERVER, server, null, IP_sender);
                        if (Constant.autoFrameWhenUpdatingOverNetwork)
                            gw.frame(drawing.getBoundingRectangle(), true);
//                        mf.requestRedrawInUiThread();
//                        MultitouchSurfaceView.log("Updating drawing");
                    } else {
//                        MultitouchSurfaceView.log("Empty message");
                    }
                } catch (IOException e) {
//                    MultitouchSurfaceView.log("Error when receiving message for server");
//                    MultitouchSurfaceView.log(" " + e.getMessage());
                    // e.printStackTrace();
                }
            } else {
//                MultitouchSurfaceView.log("Thread Reciever closed");
                break;
            }
        }
    }
}
