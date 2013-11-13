package ca.etsmtl.log792.pdavid.sketch.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import ca.etsmtl.log792.pdavid.sketch.graphic.Drawing;
import ca.etsmtl.log792.pdavid.sketch.graphic.GraphicsWrapper;
import ca.etsmtl.log792.pdavid.sketch.graphic.MultitouchFramework;
import ca.etsmtl.log792.pdavid.sketch.graphic.util.Constant;

public class NetworkServer implements Runnable {

    ServerSocket serverSocket;
    Socket socketOfServer;
    ArrayList<Socket> clientSockets;
    Drawing drawing;
    MultitouchFramework mf;
    GraphicsWrapper gw;
    Thread threadMulti;
    ServerMulticastReplyer multi;

    public NetworkServer(Drawing drawing, MultitouchFramework mf, GraphicsWrapper gw) {
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

        MultitouchFramework.log("Creation of server");

        try {
            serverSocket = new ServerSocket(Constant.PORT);
            while (true) {
                socketOfServer = serverSocket.accept();
                MultitouchFramework.log("New connection");
                clientSockets.add(socketOfServer);
                ServerReceiver pc = new ServerReceiver(socketOfServer, this, drawing, mf, gw);
                Thread thread = new Thread(pc);
                thread.start();
            }
        } catch (IOException e) {
            MultitouchFramework.log("Error when trying to start server");
            MultitouchFramework.log(" " + e.getMessage());
        }
    }
}

