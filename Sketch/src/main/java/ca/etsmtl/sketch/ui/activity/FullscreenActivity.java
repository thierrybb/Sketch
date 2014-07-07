package ca.etsmtl.sketch.ui.activity;

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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;

import ca.etsmtl.sketch.R;
import ca.etsmtl.sketch.surface.DrawableGLSurfaceView;
import ca.etsmtl.sketch.ui.activity.provider.SaveActionProvider;
import ca.etsmtl.sketch.ui.dialog.SaveDialog;
import ca.etsmtl.sketch.ui.view.TextCreatorView;

public class FullscreenActivity extends FragmentActivity {
    private ColorPicker picker;
    private SaveDialog saveDialog;
    private TextCreatorView textCreationView;
    private DrawableGLSurfaceView canvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        canvas = (DrawableGLSurfaceView) findViewById(R.id.canvas);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        picker = (ColorPicker) findViewById(R.id.picker);
        picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int i) {
                canvas.setStrokeColor(i);
            }
        });

        textCreationView = (TextCreatorView) findViewById(R.id.text_creation);
        textCreationView.setOnInsertListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                multiTouchFramework.insertText(((EditText) v.findViewById(R.id.text_creation_text)).getText().toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_full_screen, menu);
        return true;
    }

    //
    //  Menu Selection
    //
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
            case R.id.menu_undo:
                canvas.undo();
                return true;
            case R.id.menu_redo:
                canvas.redo();
                return true;
//            case R.id.menu_save:
//                /**
//                 * saveDialog is handled by {@link ca.etsmtl.sketch.ui.activity.provider.SaveActionProvider}
//                 * a callback is added at menu see On Create Options Menu.
//                 * True is returned here so the submenu will open
//                 */
//                return true;
//            case R.id.menu_draw_text_creation:
//                toggleTextCreation();
//                return true;
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
//        Bitmap bitmap = multiTouchFramework.generatePNG();

//        boolean result = saveImage(file, bitmap);
//        if (result) {
////            sendBroadcast(new Intent(
////                    Intent.ACTION_MEDIA_MOUNTED,
////                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
////            Toast.makeText(getApplicationContext(), "File saved to :" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
//            if (saveDialog != null && saveDialog.isCancelable()) {
//                saveDialog.dismiss();
//            }
//        }

    }

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
//        final String svg = multiTouchFramework.generateSVG();

//        boolean result = saveImage(file, svg);
//        if (result) {
//            sendBroadcast(new Intent(
//                    Intent.ACTION_MEDIA_MOUNTED,
//                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
//            Toast.makeText(getApplicationContext(), "File saved to :" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
//            if (saveDialog != null && saveDialog.isCancelable()) {
//                saveDialog.dismiss();
//            }
//        }

    }

    /**
     * Get the directory for the app's private pictures directory.
     *
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
                            "has no ActionBar"
            );
        }
        return homeButton.getParent().getParent();
    }

}
