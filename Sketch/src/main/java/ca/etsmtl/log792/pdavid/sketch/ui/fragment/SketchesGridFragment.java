package ca.etsmtl.log792.pdavid.sketch.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ca.etsmtl.log792.pdavid.sketch.ApplicationManager;
import ca.etsmtl.log792.pdavid.sketch.R;
import ca.etsmtl.log792.pdavid.sketch.model.Sketch;

/**
 * A fragment representing a single MenuItem detail screen.
 * This fragment is either contained in a {@link ca.etsmtl.log792.pdavid.sketch.ui.activity.MenuItemListActivity}
 * in two-pane mode (on tablets) or a {@link ca.etsmtl.log792.pdavid.sketch.ui.activity.MenuItemDetailActivity}
 * on handsets.
 */
public class SketchesGridFragment extends BaseGridFragment {

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SketchesGridFragment() {
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gridview.setAdapter(new SketchAdapter(getActivity(), ApplicationManager.sketchList));

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    private class SketchAdapter extends ArrayAdapter<Sketch> {
        private Context mContext;
        private List<Sketch> list;

        public SketchAdapter(Context c, List<Sketch> list) {
            super(c, R.layout.sketch_grid_item);
            this.list = list;
        }

        public Sketch getItem(int position) {
            return list.get(position);
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

            final Sketch item = getItem(position);

            holder.img.setImageBitmap(item.thumbnail);
            holder.txt.setText(item.name);

            return convertView;
        }

        private class ViewHolder {

            public ImageView img;
            public TextView txt;
        }
    }
}
