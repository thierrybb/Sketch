package ca.etsmtl.sketch.common.bus.component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import ca.etsmtl.sketch.common.bus.event.Event;
import ca.etsmtl.sketch.common.bus.event.OnNewIDAssigned;
import ca.etsmtl.sketch.common.bus.event.OnRequestListenToEventType;
import ca.etsmtl.sketch.common.bus.eventbus.EventBus;
import ca.etsmtl.sketch.common.bus.io.event.EventInputStream;
import ca.etsmtl.sketch.common.bus.io.event.EventOutputStream;

public class ServerConnectorComponent {

    private EventInputStream inputStream;
    private EventOutputStream outputStream;

    private BlockingQueue<Event> outputEventQueue = new LinkedBlockingQueue<Event>();

    private EventBus.EventListener mainBusEventListener;

    public ServerConnectorComponent(EventInputStream inputStream, EventOutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public void startOn(EventBus bus, int userID) {
        mainBusEventListener = new QueueDelegatorListener(outputEventQueue);
        new ReadEventServerThread(inputStream, bus).start();
        new OutputServerThread(outputStream, outputEventQueue).start();

        outputEventQueue.offer(new OnNewIDAssigned(userID));
    }

    private class QueueDelegatorListener implements EventBus.EventListener {
        private BlockingQueue<Event> outputEventQueue;

        public QueueDelegatorListener(BlockingQueue<Event> outputEventQueue) {
            this.outputEventQueue = outputEventQueue;
        }

        @Override
        public void onEventReceived(Event event) {
            outputEventQueue.offer(event);
//            System.out.println("Event offer to queue " + event.getClass());

        }
    }

    private class OutputServerThread extends Thread {
        private BlockingQueue<Event> eventQueue;
        private EventOutputStream outputStream;

        public OutputServerThread(EventOutputStream outputStream, BlockingQueue<Event> eventQueue) {
            this.outputStream = outputStream;
            this.eventQueue = eventQueue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Event event = eventQueue.take();
//                    System.out.println("Sending back event to client " + event.getClass());

                    if (!events.contains(event)) {
                        outputStream.writeEvent(event);
                    }

                    events.remove(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<Event> events = new ArrayList<Event>();

    private class ReadEventServerThread extends Thread {
        private EventInputStream inputStream;
        private EventBus eventBus;

        public ReadEventServerThread(EventInputStream inputStream, EventBus bus) {
            this.inputStream = inputStream;
            eventBus = bus;
        }

        @Override
        public void run() {
            List<Class<? extends Event>> registeredEvents = new ArrayList<Class<? extends Event>>();
            try {
                while (true) {
                    Event event = inputStream.readEvent();

//                    System.out.println("Event received " + event.getClass());

                    if (event instanceof OnRequestListenToEventType) {
                        OnRequestListenToEventType requestEvent = (OnRequestListenToEventType) event;
                        eventBus.register(mainBusEventListener, requestEvent.getEventClassToListen());
                        registeredEvents.add(requestEvent.getEventClassToListen());
                    } else {
                        events.add(event);
                        eventBus.post(event);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Client disconnected.");
            } finally {
                for (Class<? extends Event> registeredEvent : registeredEvents) {
                    eventBus.unregister(mainBusEventListener, registeredEvent);
                }
            }
        }
    }
}
