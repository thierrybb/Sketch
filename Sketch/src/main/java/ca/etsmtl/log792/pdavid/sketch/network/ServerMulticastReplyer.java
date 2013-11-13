package ca.etsmtl.log792.pdavid.sketch.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import ca.etsmtl.log792.pdavid.sketch.graphic.MultitouchFramework;
import ca.etsmtl.log792.pdavid.sketch.graphic.util.Constant;

public class ServerMulticastReplyer implements Runnable {

    InetAddress multicastGroupAddress;
    boolean running;

    public ServerMulticastReplyer() {
        running = true;
        try {
            multicastGroupAddress = InetAddress.getByName(Constant.MULTICAST_ADDRESS);
        } catch (UnknownHostException e) {
            MultitouchFramework.log("Error creating group address");
            MultitouchFramework.log(" " + e.getMessage());
        }
    }

    public void run() {
        try {
            MulticastSocket multicastSocket = new MulticastSocket(Constant.MULTICAST_SOCKET_PORT);
            multicastSocket.setSoTimeout(1000);
            multicastSocket.joinGroup(this.multicastGroupAddress);
            while (running) {
                try {
                    DatagramPacket datagramPacket = new DatagramPacket(new byte[50], 50);
                    multicastSocket.receive(datagramPacket);
                    if (datagramPacket.getLength() == Constant.MULTICAST_INITIAL_REQUEST_FROM_CLIENT.length()) {
                        DatagramPacket response = new DatagramPacket(Constant.MULTICAST_REPLY_FROM_SERVER.getBytes(),
                                Constant.MULTICAST_REPLY_FROM_SERVER.length());
                        response.setAddress(datagramPacket.getAddress());
                        response.setPort(datagramPacket.getPort());
                        multicastSocket.send(response);
                    }
                } catch (SocketTimeoutException e) {
                    // This seems to happen normally, so don't bother logging an
                    // error about it
                    // MultitouchFramework.log( "Error with multicast: " +
                    // e.getMessage() );
                }
            }
            MultitouchFramework.log("Multicast stopped");
        } catch (IOException e) {
            MultitouchFramework.log("Error creating multicast socket");
            MultitouchFramework.log(" " + e.getMessage());
        }
    }
}
