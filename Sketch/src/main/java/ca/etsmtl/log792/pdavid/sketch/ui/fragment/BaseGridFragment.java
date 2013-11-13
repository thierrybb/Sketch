package ca.etsmtl.log792.pdavid.sketch.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
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
public abstract class BaseGridFragment extends Fragment implements AdapterView.OnItemClickListener {
    protected GridView gridview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        gridview = (GridView) inflater.inflate(R.layout.fragment_sketches_grid_view, container, false);

        gridview.setOnItemClickListener(this);

        return gridview;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Set the Required Animation to GridView and start the Animation
        // use fly_in_from_center to have 2nd type of animation effect (snapshot 2)
        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.fly_in_from_top_corner);
        gridview.setAnimation(anim);
        anim.start();
    }
}
