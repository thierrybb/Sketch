package ca.etsmtl.log792.pdavid.sketch.model;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by Phil on 13/11/13.
 */
public abstract class BaseModel {

    public abstract String getTitle();

    public abstract Bitmap getImage();

    public abstract File getFile();
}
