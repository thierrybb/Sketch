package ca.etsmtl.sketch.ui.activity;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.larswerkman.holocolorpicker.ColorPicker;

import ca.etsmtl.sketch.R;
import ca.etsmtl.sketch.surface.DrawableGLSurfaceView;
import ca.etsmtl.sketch.ui.dialog.AddCollaboratorDialog;

public class DrawingActivity extends FragmentActivity {
    public static final String DRAWING_ID_INTENT_KEY = "drawingID";
    public static final String DRAWING_BUS_PORT = "DRAWING_BUS_PORT";
    public static final String DRAWING_BUS_SERVER_IP = "DRAWING_BUS_SERVER_IP";
    private ColorPicker picker;
    private DrawableGLSurfaceView canvas;
    private String currentDrawingID;

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

        currentDrawingID = getIntent().getStringExtra(DRAWING_ID_INTENT_KEY);
        canvas.loadDrawing(currentDrawingID,
                getIntent().getStringExtra(DRAWING_BUS_SERVER_IP),
                getIntent().getIntExtra(DRAWING_BUS_PORT, 11112));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menu_info:
                return true;
            case R.id.menu_draw_tools:
                canvas.setToPenDrawingMode();
                return true;
            case R.id.menu_erase:
                canvas.setToEraseMode();
                return true;
            case R.id.menu_add_collaborators:
                addCollaborator();
                return true;
            case R.id.menu_pan_mode:
                canvas.setToPanMode();
                return true;
            case R.id.menu_undo:
                canvas.undo();
                return true;
            case R.id.menu_view_collaborators:
                canvas.showCollaborators();
                return true;
            case R.id.menu_redo:
                canvas.redo();
                return true;
            case R.id.menu_color_wheel:
                toggleColorWheel();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addCollaborator() {
        AddCollaboratorDialog dialog = new AddCollaboratorDialog(this, currentDrawingID);
        dialog.show();
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

}
