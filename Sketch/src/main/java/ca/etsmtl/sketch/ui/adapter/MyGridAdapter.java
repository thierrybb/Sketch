package ca.etsmtl.sketch.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import ca.etsmtl.sketch.ApplicationManager;
import ca.etsmtl.sketch.R;
import ca.etsmtl.sketch.model.BaseModel;
import ca.etsmtl.sketch.model.Sketcher;

/**
 * Created by Phil on 13/11/13.
 */
public class MyGridAdapter extends MyBaseAdapter<BaseModel> {

    public MyGridAdapter(Context c, List<BaseModel> list) {
        super(c, R.layout.sketches_grid_view, list);

    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.sketch_grid_item, parent, false);

            holder.img = (ImageView) convertView.findViewById(R.id.imageView);
            holder.txt = (TextView) convertView.findViewById(R.id.textView);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final BaseModel item = getItem(position);

        File itemFile = item.getFile();
        if (itemFile == null) {
            ApplicationManager.getImage(holder.img, ((Sketcher) item).getImageUrl());
        } else {

            ApplicationManager.getImage(holder.img, itemFile);
        }
        holder.txt.setText(item.getTitle());

        return convertView;
    }

    private class ViewHolder {

        public ImageView img;
        public TextView txt;
    }
}
