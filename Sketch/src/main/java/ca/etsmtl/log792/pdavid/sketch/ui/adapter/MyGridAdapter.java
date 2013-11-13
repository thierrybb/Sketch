package ca.etsmtl.log792.pdavid.sketch.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ca.etsmtl.log792.pdavid.sketch.R;
import ca.etsmtl.log792.pdavid.sketch.model.BaseModel;

/**
 * Created by Phil on 13/11/13.
 */
public class MyGridAdapter extends ArrayAdapter<BaseModel> {
    private Context mContext;

    public MyGridAdapter(Context c, List<?> list) {
        super(c, R.layout.sketch_grid_item);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            holder = new ViewHolder();

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.sketch_grid_item, parent, false);
            holder.img = (ImageView) convertView.findViewById(R.id.imageView);
            holder.txt = (TextView) convertView.findViewById(R.id.textView);
            convertView.setTag(holder);
//                imageView = new ImageView(mContext);
//                imageView.setLayoutParams(new GridView.LayoutParams(120, 120));
//                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                imageView.setPadding(10, 10, 10, 10);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final BaseModel item = getItem(position);

        holder.img.setImageBitmap(item.getImage());
        holder.txt.setText(item.getTitle());

        return convertView;
    }

    private class ViewHolder {

        public ImageView img;
        public TextView txt;
    }
}
