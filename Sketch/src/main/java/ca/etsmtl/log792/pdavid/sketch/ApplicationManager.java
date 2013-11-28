package ca.etsmtl.log792.pdavid.sketch;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.bugsense.trace.BugSenseHandler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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

    private static final String TAG = "Sketch::ApplicationManager";
    public static boolean isTablet;
    public static int densityDpi;
    private static Context context;
    public static List<BaseModel> sketchList = new ArrayList<BaseModel>();
    public static List<BaseModel> bookList = new ArrayList<BaseModel>();
    public static List<BaseModel> onlineSketchersList = new ArrayList<BaseModel>();
    private String APIKEY = "b01157e6";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";


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

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = ApplicationManager.getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    public static SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return PreferenceManager.getDefaultSharedPreferences(context);
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
