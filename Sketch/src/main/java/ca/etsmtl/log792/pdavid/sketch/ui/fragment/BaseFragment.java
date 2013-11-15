package ca.etsmtl.log792.pdavid.sketch.ui.fragment;

import android.app.Activity;
import android.app.Fragment;

/**
 * Created by Phil on 13/11/13.
 */
public class BaseFragment extends Fragment {

    protected BaseGridFragment.GridCallbacks mCallbacks;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof BaseGridFragment.GridCallbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (BaseGridFragment.GridCallbacks) activity;
    }

}
