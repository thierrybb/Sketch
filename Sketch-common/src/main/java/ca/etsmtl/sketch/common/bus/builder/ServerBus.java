package ca.etsmtl.sketch.common.bus.builder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import ca.etsmtl.sketch.common.bus.component.PersistentDrawingComponent;
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
import ca.etsmtl.sketch.common.bus.shapeserialization.MemorySerializer;

public class ServerBus {
    private ServerSocket serverSocket;
    private int port;

    private Map<String, EventBus> availableBus = new HashMap<String, EventBus>();

    public ServerBus(int port) {
        this.port = port;
    }

    private EventBus createForID(String id) {
        if (availableBus.containsKey(id)) {
            return availableBus.get(id);
        }

        EventBus bus = new SimpleEventBus();

        PersistentDrawingComponent persistentDrawingComponent = new PersistentDrawingComponent(new MemorySerializer());
        persistentDrawingComponent.plug(bus);

        availableBus.put(id, bus);
        return bus;
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

                DataOutputStreamWrapper dataOutputStream = new DataOutputStreamWrapper(clientSocket.getOutputStream());
                EventOutputStream outputStream =
                        new EventToDataOutputStream(dataOutputStream);

                DataInputStreamWrapper inputStream = new DataInputStreamWrapper(clientSocket.getInputStream());
                EventInputStream eventInputStream = new EventFromDataInputStream(inputStream);

                String busID = inputStream.readString();

                ServerConnectorComponent serverConnectorComponent = new ServerConnectorComponent(eventInputStream, outputStream);
                int userID = idGenerator.generateUniqueID();
                serverConnectorComponent.startOn(createForID(busID), userID);

                System.out.println("New user registered on bus ID [" + busID + "] with user ID [" + userID + "]");
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Accept de " + port + " a échoué.");
                System.exit(1);
            }
        }
    }
}
