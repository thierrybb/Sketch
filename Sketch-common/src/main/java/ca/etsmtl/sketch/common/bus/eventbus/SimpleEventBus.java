package ca.etsmtl.sketch.common.bus.eventbus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.etsmtl.sketch.common.bus.event.Event;
import ca.etsmtl.sketch.common.bus.eventbus.subscriber.SubscriberEventInvokerBuilder;

public class SimpleEventBus implements EventBus {
    private Map<Class, List<EventListener>> subscribers = new HashMap<Class, List<EventListener>>();

    @Override
    public void register(Object subscriber, Class<? extends Event> eventType) throws NoSuchMethodException {
        register(SubscriberEventInvokerBuilder.newFromEvent(subscriber, eventType), eventType);
    }

    @Override
    public void register(EventListener listener, Class<? extends Event> eventType) {
        if (!subscribers.containsKey(eventType)) {
            subscribers.put(eventType, new ArrayList<EventListener>());
        }

        subscribers.get(eventType).add(listener);
    }

    @Override
    public void unregister(Object subscriber, Class<? extends Event> eventType) {
        List<EventListener> subscriberEventInvokers = subscribers.get(eventType);

        List<EventListener> removeList = new ArrayList<EventListener>();

        for (EventListener listener : subscriberEventInvokers) {
            if (listener.equals(subscriber))
                removeList.add(listener);
        }

        subscriberEventInvokers.removeAll(removeList);
    }

    @Override
    public void post(Event event) {
        if (subscribers.containsKey(event.getClass())) {
            List<EventListener> subscriberEventInvokers = this.subscribers.get(event.getClass());

            for (EventListener listener : subscriberEventInvokers) {
                listener.onEventReceived(event);
            }
        }
    }

    @Override
    public void dispose() {
        subscribers.clear();
    }
}
