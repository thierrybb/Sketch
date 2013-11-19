package ca.etsmtl.log792.pdavid.sketch.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;

import ca.etsmtl.log792.pdavid.sketch.R;
import ca.etsmtl.log792.pdavid.sketch.graphic.MultitouchSurfaceView;
import ca.etsmtl.log792.pdavid.sketch.ui.activity.provider.SaveActionProvider;
import ca.etsmtl.log792.pdavid.sketch.ui.activity.util.SystemUiHider;
import ca.etsmtl.log792.pdavid.sketch.ui.dialog.SaveDialog;
import ca.etsmtl.log792.pdavid.sketch.ui.view.TextCreatorView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity implements ActionBar.TabListener, ColorPicker.OnColorChangedListener, SaveActionProvider.OnClickListener {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = false;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current tab position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private boolean userDrawSomething = true;
    private MultitouchSurfaceView multiTouchFramework;
    private ColorPicker picker;
    private SaveDialog saveDialog;
    private TextCreatorView textCreationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        multiTouchFramework = (MultitouchSurfaceView) findViewById(R.id.canvas);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
//        actionBar.hide();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        actionBar.addTab(actionBar.newTab().setText(R.string.default_drawing_title).setTabListener(this), true);
//        actionBar.addTab(actionBar.newTab().setText(R.string.default_new_drawing).setTabListener(this), false);

        picker = (ColorPicker) findViewById(R.id.picker);
        picker.setOnColorChangedListener(this);

        textCreationView = (TextCreatorView) findViewById(R.id.text_creation);
        textCreationView.setOnInsertListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multiTouchFramework.insertText(((EditText) v.findViewById(R.id.text_creation_text)).getText().toString());
            }
        });

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
//        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
//        mSystemUiHider.setup();
//        mSystemUiHider
//                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
//                    // Cached values.
//                    int mControlsHeight;
//                    int mShortAnimTime;
//
//                    @Override
//                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
//                    public void onVisibilityChange(boolean visible) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
//                            // If the ViewPropertyAnimator API is available
//                            // (Honeycomb MR2 and later), use it to animate the
//                            // in-layout UI controls at the bottom of the
//                            // screen.
//                            if (mControlsHeight == 0) {
//                                mControlsHeight = controlsView.getHeight();
//                            }
//                            if (mShortAnimTime == 0) {
//                                mShortAnimTime = getResources().getInteger(
//                                        android.R.integer.config_shortAnimTime);
//                            }
//                            controlsView.animate()
//                                    .translationY(visible ? 0 : mControlsHeight)
//                                    .setDuration(mShortAnimTime);
//                        } else {
//                            // If the ViewPropertyAnimator APIs aren't
//                            // available, simply show or hide the in-layout UI
//                            // controls.
//                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
//                        }
//
//                        if (visible && AUTO_HIDE) {
//                            // Schedule a hide().
//                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
//                        }
//                    }
//                });

        // Set up the user interaction to manually show or hide the system UI.
//        contentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (TOGGLE_ON_CLICK) {
//                    mSystemUiHider.hide();
//                } else {
//                    mSystemUiHider.show();
//                }
//            }
//        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
//        findViewById(R.id.fullscreen_content).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_full_screen, menu);
        ((SaveActionProvider) menu.getItem(1).getActionProvider()).setOnClickListener(this);
        return true;
    }

    //  Menu Selection

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menu_info:
                return true;
            case R.id.menu_draw_tools:
                return true;
            case R.id.menu_save:
                /**
                 * saveDialog is handled by {@link ca.etsmtl.log792.pdavid.sketch.ui.activity.provider.SaveActionProvider}
                 * a callback is added at menu see On Create Options Menu.
                 * True is returned here so the submenu will open
                 */
                return true;
            case R.id.menu_draw_text_creation:
                toggleTextCreation();
                return true;
            case R.id.menu_color_wheel:
                toggleColorWheel();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleTextCreation() {
        if (textCreationView.getVisibility() == View.GONE || textCreationView.getVisibility() == View.INVISIBLE) {
//            int[] location = new int[2];
//            getActionItem(R.id.menu_draw_text_creation).getLocationInWindow(location);
//            int x = location[0] + textCreationView.getWidth() / 2;
//            int y = location[1] + textCreationView.getHeight() / 2;
//            textCreationView.setX(x);
//            textCreationView.setY(y);
            textCreationView.setVisibility(View.VISIBLE);
        } else {
            textCreationView.setVisibility(View.GONE);
        }
    }

    /**
     * Show / hide {@link com.larswerkman.holocolorpicker.ColorPicker}
     */
    private void toggleColorWheel() {
        if (picker.getVisibility() == View.GONE || picker.getVisibility() == View.INVISIBLE)
            picker.setVisibility(View.VISIBLE);
        else
            picker.setVisibility(View.GONE);
    }

    // Activity State

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current tab position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
                .getSelectedNavigationIndex());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    //  Tabs Events

    @Override
    public void onTabSelected(ActionBar.Tab tab,
                              FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, check if it's the active tab,
        // show NameChangeDialog

        if (getActionBar().getSelectedTab().equals(tab)) {


        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab,
                                FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab,
                                FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onColorChanged(int i) {
        multiTouchFramework.setColor(i);
//        Log.d("COLOR", "" + i);
    }

    //  Save Action Provider Callbacks

    @Override
    public void onSavePng() {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        saveDialog = SaveDialog.newInstance(SaveDialog.TYPE_PNG);
        saveDialog.show(ft, "dialog");
    }

    public void savePNG(String name) {
        File file = getAlbumStorageDir(name + ".png");
        Bitmap bitmap = multiTouchFramework.generatePNG();

        boolean result = saveImage(file, bitmap);
        if (result) {
            sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
            Toast.makeText(getApplicationContext(), "File saved to :" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            if (saveDialog != null && saveDialog.isCancelable()) {
                saveDialog.dismiss();
            }
        }

    }

    @Override
    public void onSaveSvg() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        saveDialog = SaveDialog.newInstance(SaveDialog.TYPE_SVG);
        saveDialog.show(ft, "dialog");
    }

    public void saveSVG(String name) {
        File file = getAlbumStorageDir(name + ".svg");
        final String svg = multiTouchFramework.generateSVG();

        boolean result = saveImage(file, svg);
        if (result) {
            sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
            Toast.makeText(getApplicationContext(), "File saved to :" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            if (saveDialog != null && saveDialog.isCancelable()) {
                saveDialog.dismiss();
            }
        }

    }

    /**
     * Get the directory for the app's private pictures directory.
     *
     * @param context
     * @param fileName Name of the album
     * @return a file to write to
     */
    public File getAlbumStorageDir(String fileName) {
        File file = new File(Environment.getExternalStorageDirectory(), "/MySketches/" + fileName);
        if (!file.mkdirs()) {
            Log.e("ERROR", "Directory not created");
        }
        return file;
    }

    private boolean saveImage(File file, Object thingToSave) {

        if (file.exists()) file.delete();

        try {

            FileOutputStream out = new FileOutputStream(file);
            if (thingToSave instanceof Bitmap) {
                ((Bitmap) thingToSave).compress(Bitmap.CompressFormat.PNG, 90, out);
            } else {
                out.write(thingToSave.toString().getBytes());
            }
            out.flush();
            out.close();
            if (thingToSave instanceof Bitmap)
                MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            return false;
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
//    private void delayedHide(int delayMillis) {
//        mHideHandler.removeCallbacks(mHideRunnable);
//        mHideHandler.postDelayed(mHideRunnable, delayMillis);
//    }
    public View getActionItem(int actionItemId) {
        try {
            ViewParent actionBarView = getHomeButton();
            if (!actionBarView.getClass().getName().contains("ActionBarView")) {
                String previousP = actionBarView.getClass().getName();
                actionBarView = actionBarView.getParent();
                String throwP = actionBarView.getClass().getName();
                if (!actionBarView.getClass().getName().contains("ActionBarView")) {
                    throw new IllegalStateException("Cannot find ActionBarView for " +
                            "Activity, instead found " + previousP + " and " + throwP);
                }
            }
            Field actionMenuPresenterField = actionBarView.getClass().getSuperclass().getDeclaredField("mActionMenuPresenter");
            actionMenuPresenterField.setAccessible(true);
            Object actionMenuPresenter = actionMenuPresenterField.get(actionBarView);

            Field menuViewField = actionMenuPresenter.getClass().getSuperclass().getDeclaredField("mMenuView");
            menuViewField.setAccessible(true);
            Object menuView = menuViewField.get(actionMenuPresenter);

            Field mChField;
            if (menuView.getClass().toString().contains("com.actionbarsherlock")) {
                // There are thousands of superclasses to traverse up
                // Have to get superclasses because mChildren is private
                mChField = menuView.getClass().getSuperclass().getSuperclass()
                        .getSuperclass().getSuperclass().getDeclaredField("mChildren");
            } else if (menuView.getClass().toString().contains("android.support.v7")) {
                mChField = menuView.getClass().getSuperclass().getSuperclass()
                        .getSuperclass().getDeclaredField("mChildren");
            } else {
                mChField = menuView.getClass().getSuperclass().getSuperclass()
                        .getDeclaredField("mChildren");
            }
            mChField.setAccessible(true);
            Object[] mChs = (Object[]) mChField.get(menuView);
            for (Object mCh : mChs) {
                if (mCh != null) {
                    View v = (View) mCh;
                    if (v.getId() == actionItemId) {
                        return v;
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ViewParent getHomeButton() {
        View homeButton = findViewById(android.R.id.home);
        if (homeButton == null) {
            throw new RuntimeException(
                    "insertShowcaseViewWithType cannot be used when the theme " +
                            "has no ActionBar");
        }
        return homeButton.getParent().getParent();
    }

}
