package ca.etsmtl.sketch.common.bus.eventbus.subscriber;

import java.lang.reflect.Method;

import ca.etsmtl.sketch.common.bus.event.Event;
import ca.etsmtl.sketch.common.bus.eventbus.EventBus;

public class SubscriberEventInvoker implements EventBus.EventListener {
    private Object subscriber;
    private Method eventMethod;

    SubscriberEventInvoker(Object subscriber, Method eventMethod) {
        this.subscriber = subscriber;
        this.eventMethod = eventMethod;
    }

    public boolean equals(Object subscriber) {
        return this.subscriber == subscriber || subscriber.equals(this.subscriber);
    }

    @Override
    public void onEventReceived(Event event) {
        try {
            eventMethod.invoke(subscriber, event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
