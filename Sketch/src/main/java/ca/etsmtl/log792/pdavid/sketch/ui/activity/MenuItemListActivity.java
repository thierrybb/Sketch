package ca.etsmtl.log792.pdavid.sketch.ui.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationClient;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import ca.etsmtl.log792.pdavid.sketch.ApplicationManager;
import ca.etsmtl.log792.pdavid.sketch.R;
import ca.etsmtl.log792.pdavid.sketch.network.NsdHelper;
import ca.etsmtl.log792.pdavid.sketch.network.Utils;
import ca.etsmtl.log792.pdavid.sketch.ui.activity.dummy.DummyContent;
import ca.etsmtl.log792.pdavid.sketch.ui.fragment.BaseGridFragment;
import ca.etsmtl.log792.pdavid.sketch.ui.fragment.MenuItemListFragment;
import ca.etsmtl.log792.pdavid.sketch.ui.fragment.SketchesGridFragment;


/**
 * An activity representing a list of MenuItems. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MenuItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ca.etsmtl.log792.pdavid.sketch.ui.fragment.MenuItemListFragment} and the item details
 * (if present) is a {@link ca.etsmtl.log792.pdavid.sketch.ui.fragment.MenuItemDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link ca.etsmtl.log792.pdavid.sketch.ui.fragment.MenuItemListFragment.Callbacks} interface
 * to listen for item selections.
 * http://developer.android.com/google/gcm/client.html
 */
@SuppressWarnings("unchecked")
public class MenuItemListActivity extends FragmentActivity
        implements MenuItemListFragment.Callbacks, BaseGridFragment.GridCallbacks, GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    public static final String TAG = MenuItemListActivity.class.getSimpleName();
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;
    private int mDrawerTitle = R.string.title_menuitem_list;
    private boolean backMustZoomOut = false;
    public static final String SENDER_ID = "733448047839";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private GoogleCloudMessaging gcm;
    private String regid;
    private Context context;
    private LocationClient mLocationClient;
    private NsdHelper mNsdHelper;

    /*
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
    }

    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main_layout);

        /**
         * Select between Two Pane Layout or 1 pane + Nav Drawer
         */
        if (findViewById(R.id.menuitem_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((MenuItemListFragment) getFragmentManager()
                    .findFragmentById(R.id.menuitem_list))
                    .setActivateOnItemClick(true);
        } else {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);

            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerList = (ListView) findViewById(R.id.left_drawer);

            // Set the adapter for the list view
            mDrawerList.setAdapter(new ArrayAdapter<DummyContent.DummyItem>(this,
                    R.layout.drawer_list_item, DummyContent.ITEMS));
            // Set the list's click listener
            mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
            mDrawerToggle = new ActionBarDrawerToggle(
                    this,                  /* host Activity */
                    mDrawerLayout,         /* DrawerLayout object */
                    R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                    R.string.drawer_open,  /* "open drawer" description */
                    R.string.drawer_close  /* "close drawer" description */
            ) {
                /** Called when a drawer has settled in a completely closed state. */
                public void onDrawerClosed(View view) {
                    getActionBar().setTitle(mTitle);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }

                /** Called when a drawer has settled in a completely open state. */
                public void onDrawerOpened(View drawerView) {
                    getActionBar().setTitle(mDrawerTitle);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }
            };

            // Set the drawer toggle as the DrawerListener
            mDrawerLayout.setDrawerListener(mDrawerToggle);


        }
        // Set Menu Item 0 as Default
        if (savedInstanceState == null) {
            onListItemSelected(0); //2 pane, handled in method
        }

        context = getApplicationContext();
        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = ApplicationManager.getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
        mLocationClient = new LocationClient(this, this, this);
//        mNsdHelper = new NsdHelper(this);
//        mNsdHelper.initializeNsd();
    }

    @Override
    protected void onDestroy() {
//        mNsdHelper.tearDown();
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (!mTwoPane)
            mDrawerToggle.syncState();
    }

    // Play Services APK check here too.
    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
        if (mNsdHelper != null) {
            mNsdHelper.discoverServices();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!mTwoPane)
            mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (!mTwoPane)
            if (mDrawerToggle.onOptionsItemSelected(item)) {
                return true;
            }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback method from {@link MenuItemListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onListItemSelected(int id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.

            Fragment fragment = null;
            try {
                DummyContent.DummyItem dummyItem = DummyContent.ITEMS.get(id);
                Object instance = dummyItem.getFragment().newInstance();
                if (instance instanceof Fragment) {
                    fragment = (Fragment) instance;
                    getFragmentManager().beginTransaction()
                            .replace(R.id.menuitem_detail_container, fragment, dummyItem.getTag()).addToBackStack(null)
                            .commit();

                } else {
                    selectMenuItem(id);
                }
            } catch (InstantiationException e) {

            } catch (IllegalAccessException e) {
            }
        } else {
            selectMenuItem(0);
        }
    }

    @Override
    public void onGridItemClick(int id) {
        backMustZoomOut = true;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectMenuItem(position);
        }
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectMenuItem(int position) {
        // Creates a new instance of a fragment, if the item doesn't contain
        // a fragment, start the activity
        final DummyContent.DummyItem item = DummyContent.ITEMS.get(position);
        final Class aClass = item.getFragment();

        if (aClass.toString().contains("Fragment")) {

            Fragment fragment = null;
            try {
                fragment = (Fragment) aClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            Bundle args = new Bundle();
            args.putInt("id", position);
            fragment.setArguments(args);

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment, item.getTag()).addToBackStack(null)
                    .commit();


            // Highlight the selected item, update the title, and close the drawer
            mDrawerList.setItemChecked(position, true);
            setTitle(item.title);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            startActivityForResult(new Intent(getApplicationContext(), FullscreenActivity.class), 0);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                final FragmentManager fragmentManager = getFragmentManager();
                final Fragment fragmentByTag = fragmentManager.findFragmentByTag(SketchesGridFragment.TAG);
                SketchesGridFragment sketchesGridFragment = (SketchesGridFragment) fragmentByTag;

                assert sketchesGridFragment != null;
                if (sketchesGridFragment.isZoomedIn()) {
                    sketchesGridFragment.zoomOut();
                } else {
                    //leave App if we don't have to zoomOut
                    finish();
                }

                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // GCM

    /*
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
//                finish();
            }
            return false;
        }
        return true;
    }

    /*
     * Registers the application with GCM servers asynchronously.
     *
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);

                    final String msg = "Device registered, registration ID=" + regid;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    });


                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    HttpClient client = new DefaultHttpClient();
                    URI website = null;
                    try {
                        final String ip = Utils.getIPAddress(true);
                        if (mLocationClient.isConnected()) {

                            Location lastLocation = mLocationClient.getLastLocation();
                            assert lastLocation != null;


                            website = new URI(String.format(context.getString(R.string.backend_url_register), regid,
                                    lastLocation.getLatitude(), lastLocation.getLongitude(), ip));
                        } else {
                            website = new URI(String.format(context.getString(R.string.backend_url_register), regid,
                                    0F, 0F, ip));
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    HttpGet request = new HttpGet();
                    request.setURI(website);
                    HttpResponse response = client.execute(request);
                    int r = response.getStatusLine().getStatusCode();

                    if (r == HttpStatus.SC_OK)

                        // For this demo: we don't need to send it because the device
                        // will send upstream messages to a server that echo back the
                        // message using the 'from' address in the message.

                        // Persist the regID - no need to register again.
                        storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return "";
            }
        }.execute(null, null, null);
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = ApplicationManager.getGCMPreferences(context);
        int appVersion = ApplicationManager.getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ApplicationManager.PROPERTY_REG_ID, regId);
        editor.putInt(ApplicationManager.PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    //
    //location service
    //

    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Toast.makeText(this, "Location Services Connected", Toast.LENGTH_SHORT).show();

    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
//            showErrorDialog(connectionResult.getErrorCode());
        }
    }
}
