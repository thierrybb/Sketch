package ca.etsmtl.sketch.eventbus;

import android.view.View;

import ca.etsmtl.sketch.common.bus.event.Event;
import ca.etsmtl.sketch.common.bus.eventbus.EventBus;

public class UIThreadEventBusDecorator implements EventBus {
    private EventBus decoratedObject;
    private View view;

    public UIThreadEventBusDecorator(EventBus decoratedObject, View view) {
        this.decoratedObject = decoratedObject;
        this.view = view;
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
        view.post(new Runnable() {
            @Override
            public void run() {
                decoratedObject.post(event);
            }
        });
    }

    @Override
    public void dispose() {
        decoratedObject.dispose();
    }
}
