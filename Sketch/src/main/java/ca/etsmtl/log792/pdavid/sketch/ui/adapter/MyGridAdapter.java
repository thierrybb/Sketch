package ca.etsmtl.log792.pdavid.sketch.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ca.etsmtl.log792.pdavid.sketch.R;
import ca.etsmtl.log792.pdavid.sketch.model.BaseModel;

/**
 * Created by Phil on 13/11/13.
 */
public class MyGridAdapter extends MyBaseAdapter<BaseModel> {

    public MyGridAdapter(Context c, List<BaseModel> list) {
        super(c, R.layout.sketch_grid_item, list);

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

//        ApplicationManager.getImage(holder.img, item.getImage());
        holder.txt.setText(item.getTitle());

        return convertView;
    }

    private class ViewHolder {

        public ImageView img;
        public TextView txt;
    }
}
