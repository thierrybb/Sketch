package ca.etsmtl.log792.pdavid.sketch.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import ca.etsmtl.log792.pdavid.sketch.ApplicationManager;
import ca.etsmtl.log792.pdavid.sketch.ui.adapter.MyGridAdapter;

/**
 * Created by pdavid on 11/10/13.
 */
public class BookListFragment extends BaseGridFragment {

    /**
     * Mandatory constructor
     */
    public BookListFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gridview.setAdapter(new MyGridAdapter(getActivity(), ApplicationManager.bookList));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
