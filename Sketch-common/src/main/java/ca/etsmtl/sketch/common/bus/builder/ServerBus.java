package ca.etsmtl.sketch.common.bus.builder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ca.etsmtl.sketch.common.bus.component.ServerConnectorComponent;
import ca.etsmtl.sketch.common.bus.component.UniqueIDGenerator;
import ca.etsmtl.sketch.common.bus.eventbus.EventBus;
import ca.etsmtl.sketch.common.bus.eventbus.SimpleEventBus;
import ca.etsmtl.sketch.common.bus.io.event.EventFromDataInputStream;
import ca.etsmtl.sketch.common.bus.io.event.EventInputStream;
import ca.etsmtl.sketch.common.bus.io.event.EventOutputStream;
import ca.etsmtl.sketch.common.bus.io.event.EventToDataOutputStream;
import ca.etsmtl.sketch.common.bus.io.ois.DataInputStreamWrapper;
import ca.etsmtl.sketch.common.bus.io.ois.DataOutputStreamWrapper;

public class ServerBus {
    private ServerSocket serverSocket;
    private EventBus bus = new SimpleEventBus();
    private int port;

    public ServerBus(int port) {
        this.port = port;
    }

    public void start() {
        UniqueIDGenerator idGenerator = new UniqueIDGenerator();
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();

                EventOutputStream outputStream =
                        new EventToDataOutputStream(new DataOutputStreamWrapper(clientSocket.getOutputStream()));
                EventInputStream inputStream =
                        new EventFromDataInputStream(new DataInputStreamWrapper(clientSocket.getInputStream()));
                ServerConnectorComponent serverConnectorComponent = new ServerConnectorComponent(inputStream, outputStream);
                serverConnectorComponent.startOn(bus, idGenerator.generateUniqueID());
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Accept de " + port + " a échoué.");
                System.exit(1);
            }
        }
    }
}
