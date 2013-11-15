package ca.etsmtl.log792.pdavid.sketch.network;

import java.io.IOException;

public class NetworkClientStopper implements Runnable {
    NetworkClient start;

    public NetworkClientStopper(NetworkClient sc) {
        this.start = sc;
    }

    public void run() {
        try {
            start.socket.close();
//            MultitouchSurfaceView.log("Closing client");
        } catch (IOException e) {
//            MultitouchSurfaceView.log("Problem closing socket at client end");
//            MultitouchSurfaceView.log(" " + e.getMessage());
            e.printStackTrace();
        }
    }
}
