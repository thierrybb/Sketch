package ca.etsmtl.sketch.common.bus.eventbus;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import ca.etsmtl.sketch.common.bus.event.Event;
import ca.etsmtl.sketch.common.bus.io.DataInputStream;
import ca.etsmtl.sketch.common.bus.io.DataOutputStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SimpleEventBusTest {
    private boolean eventReceived = false;

    @Test
    public void plugObjectTest() throws NoSuchMethodException {
        EventBus eventBus = new SimpleEventBus();

        eventBus.register(new EventTestListener(), EventTest.class);

        eventBus.post(new EventTest());

        assertTrue(eventReceived);
    }

    @Test
    public void unplugObjectTest() throws NoSuchMethodException {
        EventBus eventBus = new SimpleEventBus();

        EventTestListener testObject = new EventTestListener();
        eventBus.register(testObject, EventTest.class);

        eventBus.post(new EventTest());

        assertTrue(eventReceived);

        eventReceived = false;

        eventBus.unregister(testObject, EventTest.class);

        eventBus.post(new EventTest());

        assertFalse(eventReceived);
    }

    @Before
    public void testInit() {
        eventReceived = false;
    }

    @Test
    public void testTemplate() throws NoSuchMethodException {
        EventBus eventBus = new SimpleEventBus();

        EventListener<EventTest> testEventListener = new EventListener<EventTest>() {
            @Subscribe
            public void onEventReceived(EventTest e) {
                eventReceived = true;
            }
        };

        eventBus.register(testEventListener, EventTest.class);

        eventBus.post(new EventTest());

        assertTrue(eventReceived);
    }

    public class EventTestListener {

        @Subscribe
        public void onEventTest(EventTest test) {
            eventReceived = true;
        }
    }

    private class EventTest implements Event {
        @Override
        public void writeInto(DataOutputStream stream) throws IOException {

        }

        @Override
        public void readFrom(DataInputStream stream) throws IOException {

        }
    }
}
