package ca.etsmtl.sketch.common.utils;

public class UniqueIDGenerator {
    private static int nextID = 0;

    public synchronized int generateUniqueID() {
        return nextID++;
    }

    public void setNextID(int value) {
        nextID = value;
    }
}
