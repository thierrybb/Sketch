package ca.etsmtl.log792.pdavid.sketch.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import ca.etsmtl.log792.pdavid.sketch.ApplicationManager;
import ca.etsmtl.log792.pdavid.sketch.ui.adapter.MyGridAdapter;

/**
 * A fragment representing a single MenuItem detail screen.
 * This fragment is either contained in a {@link ca.etsmtl.log792.pdavid.sketch.ui.activity.MenuItemListActivity}
 * in two-pane mode (on tablets) or a {@link ca.etsmtl.log792.pdavid.sketch.ui.activity.MenuItemDetailActivity}
 * on handsets.
 */
public class SketchesGridFragment extends BaseGridFragment {

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SketchesGridFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gridview.setAdapter(new MyGridAdapter(getActivity(), ApplicationManager.sketchList));

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }


}
