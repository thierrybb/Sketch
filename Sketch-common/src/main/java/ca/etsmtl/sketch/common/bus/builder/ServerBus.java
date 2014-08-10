package ca.etsmtl.sketch.common.bus.builder;

import com.mongodb.DB;
import com.mongodb.MongoClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import ca.etsmtl.sketch.common.bus.component.PersistentDrawingComponent;
import ca.etsmtl.sketch.common.bus.component.ServerConnectorComponent;
import ca.etsmtl.sketch.common.bus.eventbus.EventBus;
import ca.etsmtl.sketch.common.bus.eventbus.SimpleEventBus;
import ca.etsmtl.sketch.common.bus.io.event.EventFromDataInputStream;
import ca.etsmtl.sketch.common.bus.io.event.EventInputStream;
import ca.etsmtl.sketch.common.bus.io.event.EventOutputStream;
import ca.etsmtl.sketch.common.bus.io.event.EventToDataOutputStream;
import ca.etsmtl.sketch.common.bus.io.ois.DataInputStreamWrapper;
import ca.etsmtl.sketch.common.bus.io.ois.DataOutputStreamWrapper;
import ca.etsmtl.sketch.common.provider.BackendUserProvider;
import ca.etsmtl.sketch.common.provider.DrawingProvider;
import ca.etsmtl.sketch.common.provider.shapeserialization.CachedSerializerDecorator;
import ca.etsmtl.sketch.common.provider.shapeserialization.MongoDBShapeSerializer;
import ca.etsmtl.sketch.common.provider.shapeserialization.ShapeSerializer;
import ca.etsmtl.sketch.common.utils.UniqueIDGenerator;

public class ServerBus {
    private ServerSocket serverSocket;
    private int port;

    private Map<String, EventBus> availableBus = new HashMap<String, EventBus>();
    private final DB db;
    private DrawingProvider drawingProvider;

    public ServerBus(String authServerAddress, int port) {
        this.port = port;

        // TODO : Externalize database init
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        db = mongoClient.getDB("Sketch");

        drawingProvider = new BackendUserProvider(authServerAddress);
    }

    private EventBus createForID(String id) {
        if (availableBus.containsKey(id)) {
            return availableBus.get(id);
        }

        EventBus bus = new SimpleEventBus();

        ShapeSerializer serializer = new CachedSerializerDecorator(new MongoDBShapeSerializer(db, id));

        PersistentDrawingComponent persistentDrawingComponent = new PersistentDrawingComponent(serializer);
        persistentDrawingComponent.plug(bus);

        availableBus.put(id, bus);
        return bus;
    }

    public void start() {

        UniqueIDGenerator generator = new UniqueIDGenerator();

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

                String account = inputStream.readString();
                String password = inputStream.readString();

                DrawingProvider.AccessToken token = drawingProvider.getToken(busID, account, password);

                if (token.equals(DrawingProvider.NULL_TOKEN)) {
                    clientSocket.close();
                }
                else {
                    ServerConnectorComponent serverConnectorComponent = new ServerConnectorComponent(eventInputStream, outputStream);
                    serverConnectorComponent.startOn(createForID(busID), token.ID);

                    System.out.println("New user registered on bus ID [" + busID + "] with user ID [" + token.ID + "]");
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Accept de " + port + " a échoué.");
                System.exit(1);
            }
        }
    }
}
