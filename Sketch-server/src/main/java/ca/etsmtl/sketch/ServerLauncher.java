package ca.etsmtl.sketch;

import ca.etsmtl.sketch.common.bus.builder.ServerBus;

public class ServerLauncher {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage : Sketch-server.jar [port] [auth server url]");
        }

        ServerBus serverBus = new ServerBus(args[1], Integer.parseInt(args[0]));
        serverBus.start();
    }
}
