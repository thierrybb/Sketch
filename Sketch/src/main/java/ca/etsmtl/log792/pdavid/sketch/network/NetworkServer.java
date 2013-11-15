package ca.etsmtl.log792.pdavid.sketch.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import ca.etsmtl.log792.pdavid.sketch.graphic.Drawing;
import ca.etsmtl.log792.pdavid.sketch.graphic.GraphicsWrapper;
import ca.etsmtl.log792.pdavid.sketch.graphic.MultitouchSurfaceView;
import ca.etsmtl.log792.pdavid.sketch.graphic.util.Constant;

public class NetworkServer implements Runnable {

    ServerSocket serverSocket;
    Socket socketOfServer;
    ArrayList<Socket> clientSockets;
    Drawing drawing;
    MultitouchSurfaceView mf;
    GraphicsWrapper gw;
    Thread threadMulti;
    ServerMulticastReplyer multi;

    public NetworkServer(Drawing drawing, MultitouchSurfaceView mf, GraphicsWrapper gw) {
        this.drawing = drawing;
        this.mf = mf;
        this.gw = gw;
        clientSockets = new ArrayList<Socket>();
        multi = new ServerMulticastReplyer();
    }

    public void run() {
        // ServerMulticastReplyer multi = new ServerMulticastReplyer();
        threadMulti = new Thread(multi);
        threadMulti.start();

//        MultitouchSurfaceView.log("Creation of server");

        try {
            serverSocket = new ServerSocket(Constant.PORT);
            while (true) {
                socketOfServer = serverSocket.accept();
//                MultitouchSurfaceView.log("New connection");
                clientSockets.add(socketOfServer);
                ServerReceiver pc = new ServerReceiver(socketOfServer, this, drawing, mf, gw);
                Thread thread = new Thread(pc);
                thread.start();
            }
        } catch (IOException e) {
//            MultitouchSurfaceView.log("Error when trying to start server");
//            MultitouchSurfaceView.log(" " + e.getMessage());
        }
    }
}

