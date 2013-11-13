package ca.etsmtl.log792.pdavid.sketch;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ca.etsmtl.log792.pdavid.sketch.model.Sketch;

/**
 * Created by pdavid on 11/10/13.
 */
public class ApplicationManager extends Application {

    public static boolean isTablet;
    public static int densityDpi;
    public static List<Sketch> sketchList;
    private static Context context;

    @Override
    public void onCreate() {

        super.onCreate();

        isTablet = getResources().getBoolean(R.bool.has_two_panes);

        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        densityDpi = metrics.densityDpi;

        sketchList = new ArrayList<Sketch>();
        sketchList.add(new Sketch("Public Sketches", "/public", null));
        sketchList.add(new Sketch("Private Sketches", "/private", null));

        context = getApplicationContext();
    }


    public static void getImage(ImageView imageView, String url) {
        Picasso.with(context)
                .load(url)
                .placeholder(android.R.color.white)
                .error(android.R.color.holo_red_light)
                .into(imageView);
    }
}
