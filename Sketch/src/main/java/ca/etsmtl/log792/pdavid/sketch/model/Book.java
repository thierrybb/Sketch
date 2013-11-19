package ca.etsmtl.log792.pdavid.sketch.model;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by Phil on 13/11/13.
 */
public class Book extends BaseModel{
    public String name;
    public Bitmap thumbnail;
    public int sketchesCount;

    public Book(String name, Bitmap thumbnail, int sketchesCount) {
        this.name = name;
        this.thumbnail = thumbnail;
        this.sketchesCount = sketchesCount;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public Bitmap getImage() {
        return thumbnail;
    }

    @Override
    public File getFile() {
        return null;
    }
}
