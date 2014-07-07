package ca.etsmtl.sketch.model;

import android.graphics.Bitmap;

/**
 * Created by Phil on 13/11/13.
 */
public class Sketcher extends BaseModel {

    String uuid;
    String image;
    String latitude;
    String longitude;
    String ip;
    String _id;

    public Sketcher() {

    }

    /**
     * @param uuid
     * @param image
     * @param latitude
     * @param longitude
     * @param ip
     */
    public Sketcher(String uuid,
                    String image,
                    String latitude,
                    String longitude,
                    String ip
    ) {

        this.uuid = uuid;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ip = ip;
    }

    @Override
    public String getTitle() {
        return String.format("Sketcher #%s", uuid);
    }

    @Override
    public Bitmap getImage() {
        return null;
    }

    public String getImageUrl() {
        return image;
    }

    public String getIp() {
        return ip;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getUuid() {
        return uuid;
    }

    public String getId() {
        return _id;
    }

}
