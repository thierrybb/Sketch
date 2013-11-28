package ca.etsmtl.log792.pdavid.sketch.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import ca.etsmtl.log792.pdavid.sketch.model.BaseModel;
import ca.etsmtl.log792.pdavid.sketch.ui.adapter.MyGridAdapter;

/**
 * Created by philippe on 27/11/13.
 */
public class SkercherGridFragment extends BaseGridFragment {

    public static final String TAG = SketchesGridFragment.class.getName();
    private List<BaseModel> list = new ArrayList<BaseModel>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SkercherGridFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gridview.setAdapter(new MyGridAdapter(getActivity(), list));
    }
}
