package ca.etsmtl.sketch.common.bus.component;

public class UniqueIDGenerator {
    private static int nextID = 0;

    public synchronized int generateUniqueID() {
        return nextID++;
    }
}
