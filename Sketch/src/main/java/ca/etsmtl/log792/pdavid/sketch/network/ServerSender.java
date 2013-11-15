package ca.etsmtl.log792.pdavid.sketch.network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ServerSender implements Runnable {
    NetworkServer start;
    PrintWriter out;
    String mess;
    InetAddress IP_sender;

    public ServerSender(NetworkServer ss, String message, InetAddress IP_sender) {
        this.start = ss;
        this.mess = message;
        this.IP_sender = IP_sender;
    }

    public void run() {
        for (Socket socket : start.clientSockets) {
            if (socket != null && !socket.isClosed()) {
                if (!socket.getInetAddress().equals(this.IP_sender)) {
                    try {
                        out = new PrintWriter(socket.getOutputStream());
                    } catch (IOException e) {
//                        MultitouchSurfaceView.log("Error when sending message from server");
//                        MultitouchSurfaceView.log(" " + e.getMessage());
                    }
                    out.println(this.mess);
                    out.flush();
//                    MultitouchSurfaceView.log(this.mess);
                }
            }
        }
    }
}
