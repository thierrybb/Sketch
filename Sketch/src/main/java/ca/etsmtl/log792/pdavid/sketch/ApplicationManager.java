package ca.etsmtl.log792.pdavid.sketch;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.bugsense.trace.BugSenseHandler;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.etsmtl.log792.pdavid.sketch.model.BaseModel;
import ca.etsmtl.log792.pdavid.sketch.model.Book;

/**
 * Created by pdavid on 11/10/13.
 */
public class ApplicationManager extends Application {

    private static final String TAG = "Sketch::ApplicationManager";
    public static final String SERVER_URL = "http://young-lowlands-1317.herokuapp.com/";
    public static boolean isTablet;
    public static int densityDpi;
    private static Context context;
    public static List<BaseModel> sketchList = new ArrayList<BaseModel>();
    public static List<BaseModel> bookList = new ArrayList<BaseModel>();
    public static List<BaseModel> onlineSketchersList = new ArrayList<BaseModel>();
    private String APIKEY = "b01157e6";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    private static ApplicationManager instance;


    public static ApplicationManager getInstance() {
        return instance;
    }

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

        context = getApplicationContext();
        BugSenseHandler.initAndStartSession(context, APIKEY);

        instance = this;
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

    public static void getImage(ImageView img, String imageUrl) {
        Picasso.with(context)
                .load(imageUrl)
                .placeholder(android.R.color.white)
                .error(android.R.color.holo_red_light)
                .into(img);
    }

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    public boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    public String generateRoomName() {
        return new NameGenerator().getName();
    }

    public class NameGenerator {

        private List vocals = new ArrayList();
        private List startConsonants = new ArrayList();
        private List endConsonants = new ArrayList();
        private List nameInstructions = new ArrayList();

        public NameGenerator() {
            String demoVocals[] = {"a", "e", "i", "o", "u", "ei", "ai", "ou", "j",
                    "ji", "y", "oi", "au", "oo"};

            String demoStartConsonants[] = {"b", "c", "d", "f", "g", "h", "k",
                    "l", "m", "n", "p", "q", "r", "s", "t", "v", "w", "x", "z",
                    "ch", "bl", "br", "fl", "gl", "gr", "kl", "pr", "st", "sh",
                    "th"};

            String demoEndConsonants[] = {"b", "d", "f", "g", "h", "k", "l", "m",
                    "n", "p", "r", "s", "t", "v", "w", "z", "ch", "gh", "nn", "st",
                    "sh", "th", "tt", "ss", "pf", "nt"};

            String nameInstructions[] = {"vd", "cvdvd", "cvd", "vdvd"};

            this.vocals.addAll(Arrays.asList(demoVocals));
            this.startConsonants.addAll(Arrays.asList(demoStartConsonants));
            this.endConsonants.addAll(Arrays.asList(demoEndConsonants));
            this.nameInstructions.addAll(Arrays.asList(nameInstructions));
        }

        /**
         * The names will look like this
         * (v=vocal,c=startConsonsonant,d=endConsonants): vd, cvdvd, cvd, vdvd
         *
         * @param vocals          pass something like {"a","e","ou",..}
         * @param startConsonants pass something like {"s","f","kl",..}
         * @param endConsonants   pass something like {"th","sh","f",..}
         */
        public NameGenerator(String[] vocals, String[] startConsonants,
                             String[] endConsonants) {
            this.vocals.addAll(Arrays.asList(vocals));
            this.startConsonants.addAll(Arrays.asList(startConsonants));
            this.endConsonants.addAll(Arrays.asList(endConsonants));
        }

        /**
         * see {@link NameGenerator#NameGenerator(String[], String[], String[])}
         *
         * @param vocals
         * @param startConsonants
         * @param endConsonants
         * @param nameInstructions Use only the following letters:
         *                         (v=vocal,c=startConsonsonant,d=endConsonants)! Pass something
         *                         like {"vd", "cvdvd", "cvd", "vdvd"}
         */
        public NameGenerator(String[] vocals, String[] startConsonants,
                             String[] endConsonants, String[] nameInstructions) {
            this(vocals, startConsonants, endConsonants);
            this.nameInstructions.addAll(Arrays.asList(nameInstructions));
        }

        public String getName() {
            return firstCharUppercase(getNameByInstructions(getRandomElementFrom(nameInstructions)));
        }

        private int randomInt(int min, int max) {
            return (int) (min + (Math.random() * (max + 1 - min)));
        }

        private String getNameByInstructions(String nameInstructions) {
            String name = "";
            int l = nameInstructions.length();

            for (int i = 0; i < l; i++) {
                char x = nameInstructions.charAt(0);
                switch (x) {
                    case 'v':
                        name += getRandomElementFrom(vocals);
                        break;
                    case 'c':
                        name += getRandomElementFrom(startConsonants);
                        break;
                    case 'd':
                        name += getRandomElementFrom(endConsonants);
                        break;
                }
                nameInstructions = nameInstructions.substring(1);
            }
            return name;
        }

        private String firstCharUppercase(String name) {
            return Character.toString(name.charAt(0)).toUpperCase() + name.substring(1);
        }

        private String getRandomElementFrom(List v) {
            return v.get(randomInt(0, v.size() - 1)).toString();
        }
    }
}
