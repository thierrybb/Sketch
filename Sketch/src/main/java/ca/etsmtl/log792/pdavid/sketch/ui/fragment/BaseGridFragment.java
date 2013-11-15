package ca.etsmtl.log792.pdavid.sketch.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;

import ca.etsmtl.log792.pdavid.sketch.R;

/**
 * A fragment representing a single MenuItem detail screen.
 * This fragment is either contained in a {@link ca.etsmtl.log792.pdavid.sketch.ui.activity.MenuItemListActivity}
 * in two-pane mode (on tablets) or a {@link ca.etsmtl.log792.pdavid.sketch.ui.activity.MenuItemDetailActivity}
 * on handsets.
 * <p/>
 * Created by Phil on 13/11/13.
 */
public abstract class BaseGridFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    protected GridView gridview;
    protected FrameLayout root;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface GridCallbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onGridItemClick(int id);
    }

    /**
     * A dummy implementation of the {@link ca.etsmtl.log792.pdavid.sketch.ui.fragment.BaseGridFragment} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    protected static GridCallbacks sDummyCallbacks = new GridCallbacks() {
        @Override
        public void onGridItemClick(int id) {
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = (FrameLayout) inflater.inflate(R.layout.fragment_sketches_grid_view, container, false);
        assert root != null;
        gridview = (GridView) root.findViewById(R.id.gridview);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gridview.setOnItemClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

}
