package ca.etsmtl.log792.pdavid.sketch;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ca.etsmtl.log792.pdavid.sketch.model.Book;
import ca.etsmtl.log792.pdavid.sketch.model.Sketch;

/**
 * Created by pdavid on 11/10/13.
 */
public class ApplicationManager extends Application {

    public static boolean isTablet;
    public static int densityDpi;
    public static List<Sketch> sketchList;
    private static Context context;
    public static List<Book> bookList;

    @Override
    public void onCreate() {

        super.onCreate();

        isTablet = getResources().getBoolean(R.bool.has_two_panes);

        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        densityDpi = metrics.densityDpi;

        sketchList = new ArrayList<Sketch>();
        sketchList.add(new Sketch("Sketch 1", "/public/Sketch1.png", null));
        sketchList.add(new Sketch("Sketch 2", "/public/Sketch2.png", null));

        bookList = new ArrayList<Book>();
        bookList.add(new Book("Public Sketches", null, 10));
        bookList.add(new Book("Private Sketches", null, 0));

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
