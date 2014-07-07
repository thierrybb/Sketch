package ca.etsmtl.sketch;

import ca.etsmtl.sketch.common.bus.builder.ServerBus;

public class ServerLauncher {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage : Sketch-server.jar [port]");
        }

        ServerBus serverBus = new ServerBus(11112);
        serverBus.start();
    }
}