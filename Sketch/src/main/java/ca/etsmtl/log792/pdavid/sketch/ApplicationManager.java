package ca.etsmtl.log792.pdavid.sketch;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.bugsense.trace.BugSenseHandler;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ca.etsmtl.log792.pdavid.sketch.model.BaseModel;
import ca.etsmtl.log792.pdavid.sketch.model.Book;
import ca.etsmtl.log792.pdavid.sketch.model.Sketcher;

/**
 * Created by pdavid on 11/10/13.
 */
public class ApplicationManager extends Application {

    public static boolean isTablet;
    public static int densityDpi;
    private static Context context;
    public static List<BaseModel> sketchList = new ArrayList<BaseModel>();
    public static List<BaseModel> bookList = new ArrayList<BaseModel>();
    public static List<BaseModel> onlineSketchersList = new ArrayList<BaseModel>();
    private String APIKEY = "b01157e6";

    @Override
    public void onCreate() {

        super.onCreate();

        isTablet = getResources().getBoolean(R.bool.has_two_panes);

        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        densityDpi = metrics.densityDpi;

        //books
        bookList.add(new Book("Public Sketches", null, 10));
        bookList.add(new Book("Private Sketches", null, 0));
        bookList.add(new Book("Private Sketches", null, 0));
        bookList.add(new Book("Private Sketches", null, 0));
        bookList.add(new Book("Private Sketches", null, 0));
        bookList.add(new Book("Private Sketches", null, 0));
        bookList.add(new Book("Private Sketches", null, 0));
        bookList.add(new Book("Private Sketches", null, 0));
        bookList.add(new Book("Private Sketches", null, 0));
        bookList.add(new Book("Private Sketches", null, 0));
        bookList.add(new Book("Private Sketches", null, 0));
        bookList.add(new Book("Private Sketches", null, 0));
        bookList.add(new Book("Private Sketches", null, 0));
        bookList.add(new Book("Private Sketches", null, 0));
        bookList.add(new Book("Private Sketches", null, 0));
        bookList.add(new Book("Private Sketches", null, 0));
        bookList.add(new Book("Private Sketches", null, 0));
        bookList.add(new Book("Private Sketches", null, 0));
        bookList.add(new Book("Private Sketches", null, 0));
        //Online
        onlineSketchersList.add(new Sketcher());
        onlineSketchersList.add(new Sketcher());
        onlineSketchersList.add(new Sketcher());
        onlineSketchersList.add(new Sketcher());
        onlineSketchersList.add(new Sketcher());
        onlineSketchersList.add(new Sketcher());
        onlineSketchersList.add(new Sketcher());
        onlineSketchersList.add(new Sketcher());
        onlineSketchersList.add(new Sketcher());
        onlineSketchersList.add(new Sketcher());
        onlineSketchersList.add(new Sketcher());
        onlineSketchersList.add(new Sketcher());
        onlineSketchersList.add(new Sketcher());
        onlineSketchersList.add(new Sketcher());
        onlineSketchersList.add(new Sketcher());
        onlineSketchersList.add(new Sketcher());
        onlineSketchersList.add(new Sketcher());
        onlineSketchersList.add(new Sketcher());

        context = getApplicationContext();
        BugSenseHandler.initAndStartSession(context, APIKEY);
    }


    public static void getImage(ImageView imageView, File f) {
        Picasso.with(context)
                .load(f)
                .placeholder(android.R.color.white)
                .error(android.R.color.holo_red_light)
                .fit()
                .into(imageView);
    }

    public static void getImage(ImageView imageView, int resId) {
        Picasso.with(context)
                .load(resId)
                .placeholder(android.R.color.white)
                .error(android.R.color.holo_red_light)
                .into(imageView);
    }
}
