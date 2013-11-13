package ca.etsmtl.log792.pdavid.sketch.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import ca.etsmtl.log792.pdavid.sketch.graphic.Drawing;
import ca.etsmtl.log792.pdavid.sketch.graphic.GraphicsWrapper;
import ca.etsmtl.log792.pdavid.sketch.graphic.MultitouchFramework;
import ca.etsmtl.log792.pdavid.sketch.graphic.util.Constant;

public class NetworkClient implements Runnable {

    Socket socket;
    InetAddress multiCastGroupAddress;
    ArrayList<InetAddress> buffer = new ArrayList<InetAddress>();
    Drawing drawing;
    MultitouchFramework mf;
    GraphicsWrapper gw;

    private static final int CONNECTION_WITH_USER_ENTERED_IP_ADDRESS = 1;
    private static final int CONNECTION_VIA_MULTICAST_DISCOVERY = 2;
    int typeConnection;

    InetAddress inetAddress;

    public NetworkClient(Drawing drawing, MultitouchFramework mf, GraphicsWrapper gw) {
        this.drawing = drawing;
        this.mf = mf;
        this.gw = gw;
        typeConnection = CONNECTION_VIA_MULTICAST_DISCOVERY;
        try {
            multiCastGroupAddress = InetAddress.getByName(Constant.MULTICAST_ADDRESS);
        } catch (UnknownHostException e) {
            MultitouchFramework.log("Error creating group address");
            MultitouchFramework.log(" " + e.getMessage());
        }
    }

    public NetworkClient(String IPAddressOfServer, Drawing drawing, MultitouchFramework mf, GraphicsWrapper gw) {
        this.drawing = drawing;
        this.mf = mf;
        this.gw = gw;
        typeConnection = CONNECTION_WITH_USER_ENTERED_IP_ADDRESS;
        try {
            inetAddress = InetAddress.getByName(IPAddressOfServer);
        } catch (UnknownHostException e) {
            MultitouchFramework.log("Error starting client");
            MultitouchFramework.log(" " + e.getMessage());
        }
    }

    public void run() {
        if (typeConnection == CONNECTION_VIA_MULTICAST_DISCOVERY) {
            try {
                MulticastSocket multicastsocket = new MulticastSocket(Constant.MULTICAST_SOCKET_PORT);
                multicastsocket.joinGroup(multiCastGroupAddress);

                DatagramPacket datagrampacket = new DatagramPacket(
                        Constant.MULTICAST_INITIAL_REQUEST_FROM_CLIENT.getBytes(),
                        Constant.MULTICAST_INITIAL_REQUEST_FROM_CLIENT.length(), this.multiCastGroupAddress,
                        Constant.MULTICAST_SOCKET_PORT);
                multicastsocket.send(datagrampacket);

                int timeLeft = 5000; // in milliseconds
                int endtime = (int) System.currentTimeMillis() + timeLeft;

                boolean wasSuccessful = false; // TODO XXX problem: this works
                // fine if there's only one
                // server, but if there are
                // many, we'll only connect to
                // the first one
                do {
                    try {
                        multicastsocket.setSoTimeout(timeLeft);
                        DatagramPacket reponse = new DatagramPacket(new byte[50], 50); // TODO
                        // XXX
                        // what
                        // if
                        // this
                        // overflows
                        // ?
                        multicastsocket.receive(reponse);
                        if (reponse.getLength() == Constant.MULTICAST_REPLY_FROM_SERVER.length()) {
                            this.buffer.add(reponse.getAddress());
                            wasSuccessful = true;
                        }
                    } catch (SocketTimeoutException e) {
                        MultitouchFramework.log("Error during server discovery via multicast: SocketTimeoutException");
                        MultitouchFramework.log(" " + e.getMessage());
                    }
                    timeLeft = endtime - (int) System.currentTimeMillis();
                } while (!wasSuccessful && 0 < timeLeft);
                multicastsocket.leaveGroup(this.multiCastGroupAddress);
            } catch (IOException e) {
                MultitouchFramework.log("Error during server discovery via multicast: IOException");
                MultitouchFramework.log(" " + e.getMessage());
            }
            if (this.buffer.size() > 0) {
                try {
                    String adr = this.buffer.get(0).toString().substring(1);
                    socket = new Socket(adr, Constant.PORT);
                    ClientReceiver reception = new ClientReceiver(this, drawing, mf, gw);
                    new Thread(reception).start();
                } catch (UnknownHostException e) {
                    MultitouchFramework.log("Error: unknown host");
                    MultitouchFramework.log(" " + e.getMessage());
                    // e.printStackTrace();
                } catch (IOException e) {
                    MultitouchFramework.log("Error: IOException");
                    MultitouchFramework.log(" " + e.getMessage());
                    // e.printStackTrace();
                }
            } else {
                MultitouchFramework.log("No server available");
            }
        } else {
            try {
                socket = new Socket(inetAddress, Constant.PORT);
                ClientReceiver reception = new ClientReceiver(this, drawing, mf, gw);
                new Thread(reception).start();
            } catch (UnknownHostException e) {
                MultitouchFramework.log("Error: unknown host");
                MultitouchFramework.log(" " + e.getMessage());
                // e.printStackTrace();
            } catch (IOException e) {
                MultitouchFramework.log("Error: IOException");
                MultitouchFramework.log(" " + e.getMessage());
                // e.printStackTrace();
            }
        }
    }
}

