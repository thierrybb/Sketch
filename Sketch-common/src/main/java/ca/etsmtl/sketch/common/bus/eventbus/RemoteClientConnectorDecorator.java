package ca.etsmtl.sketch.common.bus.eventbus;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import ca.etsmtl.sketch.common.bus.event.Event;
import ca.etsmtl.sketch.common.bus.event.OnRequestListenToEventType;
import ca.etsmtl.sketch.common.bus.io.event.EventInputStream;
import ca.etsmtl.sketch.common.bus.io.event.EventOutputStream;

public class RemoteClientConnectorDecorator implements EventBus {
    private final ReadEventFromStream readEventFromStream;
    private EventBus decoratedBus;

    private EventOutputStream outputStream;
    private boolean serverIsUp = true;

    private BlockingQueue<Event> outputEventQueue = new LinkedBlockingQueue<Event>();


    public RemoteClientConnectorDecorator(EventBus decoratedBus,
                                          EventOutputStream serverOutputStream,
                                          EventInputStream inputStream) {
        this.decoratedBus = decoratedBus;
        this.outputStream = serverOutputStream;

        readEventFromStream = new ReadEventFromStream(inputStream, decoratedBus);
        readEventFromStream.start();

        new WriteToOutputThread(outputStream, outputEventQueue).start();
    }

    @Override
    public void register(Object subscriber, Class<? extends Event> eventType) throws NoSuchMethodException {
        try {
            outputStream.writeEvent(new OnRequestListenToEventType(eventType));
        } catch (IOException e) {
            e.printStackTrace();
        }
        decoratedBus.register(subscriber, eventType);
    }

    @Override
    public void register(EventListener listener, Class<? extends Event> eventType) {
        try {
            outputStream.writeEvent(new OnRequestListenToEventType(eventType));
        } catch (IOException e) {
            e.printStackTrace();
        }
        decoratedBus.register(listener, eventType);
    }

    @Override
    public void unregister(Object subscriber, Class<? extends Event> eventType) {
        decoratedBus.unregister(subscriber, eventType);
    }

    @Override
    public void post(Event event) {
        decoratedBus.post(event);
        outputEventQueue.add(event);
    }

    @Override
    public void dispose() {
        serverIsUp = false;
    }

    private class WriteToOutputThread extends Thread {
        private BlockingQueue<Event> eventQueue;
        private EventOutputStream outputStream;

        public WriteToOutputThread(EventOutputStream outputStream, BlockingQueue<Event> eventQueue) {
            this.outputStream = outputStream;
            this.eventQueue = eventQueue;
        }

        @Override
        public void run() {
            while (serverIsUp) {
                try {
                    Event event = eventQueue.take();
                    outputStream.writeEvent(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ReadEventFromStream extends Thread {
        private final EventInputStream eis;
        private final EventBus eventBusToForwardEvent;

        public ReadEventFromStream(EventInputStream eis, EventBus eventBusToForwardEvent) {
            this.eis = eis;
            this.eventBusToForwardEvent = eventBusToForwardEvent;
        }

        public void run() {
            while (serverIsUp) {
                try {
                    Event event = eis.readEvent();
                    eventBusToForwardEvent.post(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
