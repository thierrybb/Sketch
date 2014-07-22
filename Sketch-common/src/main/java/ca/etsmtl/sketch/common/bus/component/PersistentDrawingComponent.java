package ca.etsmtl.sketch.common.bus.component;

import ca.etsmtl.sketch.common.event.OnAllStrokeRestored;
import ca.etsmtl.sketch.common.event.OnInkStrokeErased;
import ca.etsmtl.sketch.common.event.OnInkStrokeReAdded;
import ca.etsmtl.sketch.common.event.OnInkStrokeRemoved;
import ca.etsmtl.sketch.common.event.OnNewClientConnected;
import ca.etsmtl.sketch.common.bus.eventbus.EventBus;
import ca.etsmtl.sketch.common.bus.eventbus.Subscribe;
import ca.etsmtl.sketch.common.bus.shapeserialization.ShapeSerializer;
import ca.etsmtl.sketch.common.event.OnInkStrokeAdded;
import ca.etsmtl.sketch.common.event.OnStrokeRestored;

public class PersistentDrawingComponent {
    private EventBus bus;
    private ShapeSerializer serializer;

    public PersistentDrawingComponent(ShapeSerializer serializer) {
        this.serializer = serializer;
    }

    public void plug(EventBus bus) {
        this.bus = bus;
        try {
            bus.register(this, OnNewClientConnected.class);
            bus.register(this, OnInkStrokeAdded.class);
            bus.register(this, OnInkStrokeRemoved.class);
            bus.register(this, OnInkStrokeReAdded.class);
            bus.register(this, OnInkStrokeErased.class);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onInkDrawingAdded(OnInkStrokeAdded event) {
        serializer.serializeInkStroke(event.getPoints(), event.getStrokeColor(),
                event.getUniqueID(), event.getUserID());
    }

    @Subscribe
    public void onInkStrokeReAdded(OnInkStrokeReAdded event) {
        serializer.serializeInkStroke(event.getPoints(), event.getStrokeColor(),
                event.getUniqueID(), event.getUserID());
    }

    @Subscribe
    public void onInkStrokeRemoved(OnInkStrokeRemoved event) {
        serializer.removeStroke(event.getUniqueID(), event.getUserID());
    }

    @Subscribe
    public void onInkStrokeErased(OnInkStrokeErased event) {
        serializer.removeStroke(event.getUniqueID(), event.getUserID());
    }

    @Subscribe
    public void onUserAdded(final OnNewClientConnected event) {
        serializer.pullAllInkStroke(new ShapeSerializer.InkStoreReaderStrategy() {
            @Override
            public void readStroke(float[] strokes, int color, int id, int userID) {
                bus.post(new OnStrokeRestored(strokes, color, userID, id, event.getUserId()));
            }
        });
        bus.post(new OnAllStrokeRestored(event.getUserId()));
    }
}
