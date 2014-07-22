package ca.etsmtl.sketch.utils;

public class Identifier {
    private int localID;
    private int userID;

    public Identifier(int localID, int userID) {
        this.localID = localID;
        this.userID = userID;
    }

    public int getLocalID() {
        return localID;
    }

    public int getUserID() {
        return userID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Identifier that = (Identifier) o;

        if (localID != that.localID) return false;
        if (userID != that.userID) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = localID;
        result = 31 * result + userID;
        return result;
    }

    public static Identifier create(int localID, int userID) {
        return new Identifier(localID, userID);
    }
}
