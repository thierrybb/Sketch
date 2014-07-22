package ca.etsmtl.sketch.common.bus.component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import ca.etsmtl.sketch.common.bus.event.Event;
import ca.etsmtl.sketch.common.bus.event.OnClientDisconnected;
import ca.etsmtl.sketch.common.bus.event.OnNewIDAssigned;
import ca.etsmtl.sketch.common.bus.event.OnRequestListenToEventType;
import ca.etsmtl.sketch.common.bus.event.OnRequestUnregisterToEventType;
import ca.etsmtl.sketch.common.bus.eventbus.EventBus;
import ca.etsmtl.sketch.common.bus.io.event.EventInputStream;
import ca.etsmtl.sketch.common.bus.io.event.EventOutputStream;

public class ServerConnectorComponent {

    private EventInputStream inputStream;
    private EventOutputStream outputStream;

    private BlockingQueue<Event> outputEventQueue = new LinkedBlockingQueue<Event>();

    private EventBus.EventListener mainBusEventListener;

    private List<Event> events = new ArrayList<Event>();

    private int userID;

    private boolean isRunning = true;

    public ServerConnectorComponent(EventInputStream inputStream, EventOutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public void startOn(EventBus bus, int userID) {
        mainBusEventListener = new QueueDelegatorListener(outputEventQueue);
        new ReadEventServerThread(inputStream, bus).start();
        new OutputServerThread(outputStream, outputEventQueue).start();

        outputEventQueue.offer(new OnNewIDAssigned(userID));

        this.userID = userID;
    }
    private class QueueDelegatorListener implements EventBus.EventListener {

        private BlockingQueue<Event> outputEventQueue;

        public QueueDelegatorListener(BlockingQueue<Event> outputEventQueue) {
            this.outputEventQueue = outputEventQueue;
        }
        @Override
        public void onEventReceived(Event event) {
            outputEventQueue.offer(event);
        }

    }
    private class OutputServerThread extends Thread {
        private BlockingQueue<Event> eventQueue;
        private EventBus bus;

        private EventOutputStream outputStream;

        public OutputServerThread(EventOutputStream outputStream, BlockingQueue<Event> eventQueue) {
            this.outputStream = outputStream;
            this.eventQueue = eventQueue;
        }
        @Override
        public void run() {
            while (isRunning) {
                try {
                    Event event = eventQueue.take();

                    if (!events.contains(event)) {
                        outputStream.writeEvent(event);
                    }

                    events.remove(event);
                } catch (Exception e) {
                    System.out.println("OutputServerThread exception from : [" + userID + "].");
                    isRunning = false;
                }
            }
        }

    }

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
            registeredEvents.add(OnClientDisconnected.class);
            eventBus.register(mainBusEventListener, OnClientDisconnected.class);
            try {
                while (isRunning) {
                    Event event = inputStream.readEvent();

                    if (event instanceof OnRequestListenToEventType) {
                        OnRequestListenToEventType requestEvent = (OnRequestListenToEventType) event;
                        eventBus.register(mainBusEventListener, requestEvent.getEventClassToListen());
                        registeredEvents.add(requestEvent.getEventClassToListen());
                    } else if (event instanceof OnRequestUnregisterToEventType) {
                        OnRequestUnregisterToEventType requestEvent = (OnRequestUnregisterToEventType) event;
                        eventBus.unregister(mainBusEventListener, requestEvent.getEventClassToUnregister());
                        registeredEvents.remove(requestEvent.getEventClassToUnregister());
                    } else {
                        events.add(event);
                        eventBus.post(event);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Client [" + userID + "] disconnected.");
                eventBus.post(new OnClientDisconnected(userID));
            } finally {
                for (Class<? extends Event> registeredEvent : registeredEvents) {
                    eventBus.unregister(mainBusEventListener, registeredEvent);
                }

                isRunning = false;
            }
        }
    }
}
