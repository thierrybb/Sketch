package ca.etsmtl.sketch.common.bus.eventbus;

import ca.etsmtl.sketch.common.bus.event.Event;

public interface EventBus {
    void register(Object subscriber, Class<? extends Event> eventType) throws NoSuchMethodException;

    void register(EventListener listener, Class<? extends Event> eventType);

    void unregister(Object subscriber, Class<? extends Event> eventType);

    public void post(Event event);

    public interface EventListener {
        void onEventReceived(Event event);
    }

    void dispose();
}
