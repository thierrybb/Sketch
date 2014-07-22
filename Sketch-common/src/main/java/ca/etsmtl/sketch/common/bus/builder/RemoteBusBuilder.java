package ca.etsmtl.sketch.common.bus.builder;

import java.io.IOException;
import java.net.Socket;

import ca.etsmtl.sketch.common.bus.eventbus.EventBus;
import ca.etsmtl.sketch.common.bus.eventbus.RemoteClientConnectorDecorator;
import ca.etsmtl.sketch.common.bus.io.DataOutputStream;
import ca.etsmtl.sketch.common.bus.io.event.EventFromDataInputStream;
import ca.etsmtl.sketch.common.bus.io.event.EventInputStream;
import ca.etsmtl.sketch.common.bus.io.event.EventOutputStream;
import ca.etsmtl.sketch.common.bus.io.event.EventToDataOutputStream;
import ca.etsmtl.sketch.common.bus.io.ois.DataInputStreamWrapper;
import ca.etsmtl.sketch.common.bus.io.ois.DataOutputStreamWrapper;

public class RemoteBusBuilder {

    private EventBus bus;
    private Socket socket;


    public RemoteBusBuilder setDecoratedBus(EventBus bus) {
        this.bus = bus;
        return this;
    }

    public RemoteBusBuilder setSocket(Socket socket) {
        this.socket = socket;
        return this;
    }

    public EventBus build(String eventBusID) throws IOException {
        DataOutputStream outputStream = new DataOutputStreamWrapper(socket.getOutputStream());
        outputStream.writeString(eventBusID);
        outputStream.flush();

        EventOutputStream eventOutputStream =
                new EventToDataOutputStream(outputStream);
        EventInputStream inputStream =
                new EventFromDataInputStream(new DataInputStreamWrapper(socket.getInputStream()));

        return new RemoteClientConnectorDecorator(bus, eventOutputStream, inputStream);
    }
}
