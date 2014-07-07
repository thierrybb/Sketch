package ca.etsmtl.sketch.common.bus.component;

import ca.etsmtl.sketch.common.bus.eventbus.EventBus;
import ca.etsmtl.sketch.common.bus.eventbus.Subscribe;
import ca.etsmtl.sketch.common.bus.shapeserialization.ShapeSerializer;
import ca.etsmtl.sketch.common.event.OnInkStrokeAdded;
import ca.etsmtl.sketch.common.event.OnNewUserAdded;import ca.etsmtl.sketch.common.event.OnSyncDrawingEvent;

public class PersistentDrawingComponent {
    private EventBus bus;
    private ShapeSerializer serializer;

    public void plug(EventBus bus) {
        this.bus = bus;
        try {
            bus.register(this, OnInkStrokeAdded.class);
            bus.register(this, OnNewUserAdded.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onInkDrawingAdded(OnInkStrokeAdded event) {
        serializer.serializeInkStroke(event.getPoints(), event.getStrokeColor());
    }

    @Subscribe
    public void onUserAdded(OnNewUserAdded onNewUserAdded) {
        serializer.pullAllInkStroke(new ShapeSerializer.InkStoreReaderStrategy() {
            @Override
            public void readStroke(float[] strokes, int color, int id, int userID) {
                bus.post(new OnInkStrokeAdded(strokes, color, id, userID));

            }
        });
    }
}
