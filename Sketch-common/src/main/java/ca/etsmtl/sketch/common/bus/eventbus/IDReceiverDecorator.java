package ca.etsmtl.sketch.common.bus.eventbus;

import java.util.concurrent.Semaphore;

import ca.etsmtl.sketch.common.bus.event.Event;
import ca.etsmtl.sketch.common.bus.event.OnNewIDAssigned;

public class IDReceiverDecorator implements EventBus {
    private EventBus decoratedObject;
    private int id = -1;

    private Semaphore idReceived = new Semaphore(0);

    public IDReceiverDecorator(EventBus decoratedObject) {
        this.decoratedObject = decoratedObject;
    }

    public int getId() {
        if (id == -1) {
            try {
                idReceived.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return id;
    }

    @Override
    public void register(Object subscriber, Class<? extends Event> eventType) throws NoSuchMethodException {
        decoratedObject.register(subscriber, eventType);
    }

    @Override
    public void register(EventListener listener, Class<? extends Event> eventType) {
        decoratedObject.register(listener, eventType);
    }

    @Override
    public void unregister(Object subscriber, Class<? extends Event> eventType) {
        decoratedObject.unregister(subscriber, eventType);
    }

    @Override
    public void post(final Event event) {
        if (event instanceof OnNewIDAssigned) {
            this.id = ((OnNewIDAssigned)event).getNewID();
            idReceived.release();
        }

        decoratedObject.post(event);
    }
}
