package ca.etsmtl.log792.pdavid.sketch.network;

import java.io.IOException;

import ca.etsmtl.log792.pdavid.sketch.graphic.MultitouchFramework;

public class NetworkClientStopper implements Runnable {
    NetworkClient start;

    public NetworkClientStopper(NetworkClient sc) {
        this.start = sc;
    }

    public void run() {
        try {
            start.socket.close();
            MultitouchFramework.log("Closing client");
        } catch (IOException e) {
            MultitouchFramework.log("Problem closing socket at client end");
            MultitouchFramework.log(" " + e.getMessage());
            // e.printStackTrace();
        }
    }
}
