package ca.etsmtl.log792.pdavid.sketch.network;

import java.io.IOException;
import java.net.Socket;

public class NetworkServerStopper implements Runnable {

    NetworkServer start;

    public NetworkServerStopper(NetworkServer ss) {
        this.start = ss;
    }

    public void run() {
        try {
            // We close each socket connected to a client
            for (Socket s : start.clientSockets) {
                if (s != null) {
                    s.close();
//                    MultitouchSurfaceView.log("Closing of socket");
                }
            }

            // if ( start.socketOfServer!=null ) start.socketOfServer.close();
            // // TODO XXX TODO-Celine doesn't this simply close the most recent
            // client ? - Fait, le serveur ferme tous les sockets qu'il a cr��
            // (lignes d'au dessus)
            // TODO XXX TODO-Celine what about stopping the multicast thread ? -
            // C'est bon le thread multicast se ferme bien (ligne en dessous)

            // On arr�te le thread qui g�re le multicast
            start.multi.running = false;
            if (start.serverSocket != null)
                start.serverSocket.close();
        } catch (IOException e) {
//            MultitouchSurfaceView.log("Error when trying to stop server");
//            MultitouchSurfaceView.log(" " + e.getMessage());
            // e.printStackTrace();
        }
    }
}
