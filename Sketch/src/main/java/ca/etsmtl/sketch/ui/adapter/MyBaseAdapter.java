package ca.etsmtl.sketch.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Phil on 13/11/13.
 */
public abstract class MyBaseAdapter<T> extends ArrayAdapter<T> {
    protected final LayoutInflater inflater;
    protected List<T> items;

    public MyBaseAdapter(Context context, int resource, List<T> items) {
        super(context, resource, items);
        this.items = items;
        inflater = LayoutInflater.from(getContext());
    }

    @Override
    public T getItem(int position) {
        return items.get(position);
    }

}
