package ca.etsmtl.log792.pdavid.sketch.model;

import android.graphics.Bitmap;

/**
 * Created by pdavid on 11/12/13.
 */
public class Sketch extends BaseModel {
    public String name;
    public String filePath;
    public Bitmap thumbnail;

    public Sketch(String name, String filePath, Bitmap thumbnail) {
        this.name = name;
        this.filePath = filePath;
        this.thumbnail = thumbnail;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public Bitmap getImage() {
        return thumbnail;
    }
}
