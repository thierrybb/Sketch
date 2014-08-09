package ca.etsmtl.sketch.common.provider;

public interface DrawingProvider {
    public class AccessToken {
        public int ID = -1;

        public AccessToken(int id) {
            ID = id;
        }
    }

    public static final AccessToken NULL_TOKEN = new AccessToken(-1);

    AccessToken getToken(String drawingID, String account, String password);
}
