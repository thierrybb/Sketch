package ca.etsmtl.sketch.model;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by pdavid on 11/12/13.
 */
public class Sketch extends BaseModel {
    public String name;
    public File file;
    public Bitmap thumbnail;

    public Sketch(String name, File filePath, Bitmap thumbnail) {
        this.name = name;
        this.file = filePath;
        this.thumbnail = thumbnail;
    }

    public Sketch(String name, File path) {
        this(name, path, null);
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
        return file;
    }
}
