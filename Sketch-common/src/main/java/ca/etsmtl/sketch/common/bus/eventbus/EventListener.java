package ca.etsmtl.sketch.common.bus.eventbus;

import ca.etsmtl.sketch.common.bus.event.Event;

public interface EventListener<T extends Event> {
    @Subscribe
    void onEventReceived(T e);
}
