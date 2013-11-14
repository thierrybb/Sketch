package ca.etsmtl.log792.pdavid.sketch.ui.fragment;

import android.app.Activity;
import android.app.Fragment;

/**
 * Created by Phil on 13/11/13.
 */
public class BaseFragment extends Fragment {
    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    protected GridCallbacks mCallbacks = sDummyCallbacks;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface GridCallbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(int id);
    }

    /**
     * A dummy implementation of the {@link ca.etsmtl.log792.pdavid.sketch.ui.fragment.BaseFragment.GridCallbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    protected static GridCallbacks sDummyCallbacks = new GridCallbacks() {
        @Override
        public void onItemSelected(int id) {
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof GridCallbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (GridCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }
}
