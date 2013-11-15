package ca.etsmtl.log792.pdavid.sketch.network;

import java.io.IOException;
import java.io.PrintWriter;

public class ClientSender implements Runnable {

    NetworkClient start;
    PrintWriter out;
    String mess;

    public ClientSender(NetworkClient sc, String message) {
        this.start = sc;
        this.mess = message;
    }

    public void run() {
        if (start != null && start.socket != null && !start.socket.isClosed()) {
            try {
                out = new PrintWriter(start.socket.getOutputStream());
            } catch (IOException e) {
//                MultitouchSurfaceView.log("Problem creating message");
//                MultitouchSurfaceView.log(" " + e.getMessage());
            }
            out.println(this.mess);
            out.flush();
//            MultitouchSurfaceView.log(this.mess);
        }
    }
}
