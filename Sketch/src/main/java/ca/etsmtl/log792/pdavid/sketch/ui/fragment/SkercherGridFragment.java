package ca.etsmtl.log792.pdavid.sketch.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import ca.etsmtl.log792.pdavid.sketch.ApplicationManager;
import ca.etsmtl.log792.pdavid.sketch.R;
import ca.etsmtl.log792.pdavid.sketch.model.Sketcher;
import ca.etsmtl.log792.pdavid.sketch.network.AbsDataRequest;
import ca.etsmtl.log792.pdavid.sketch.network.DataManager;
import ca.etsmtl.log792.pdavid.sketch.network.GetRequest;
import ca.etsmtl.log792.pdavid.sketch.ui.activity.FullscreenActivity;
import ca.etsmtl.log792.pdavid.sketch.ui.activity.HandleInviteActivity;
import ca.etsmtl.log792.pdavid.sketch.ui.adapter.MyBaseAdapter;

/**
 * Created by philippe on 27/11/13.
 */
public class SkercherGridFragment extends BaseGridFragment {

    public static final String TAG = SketchesGridFragment.class.getName();
    private List<Sketcher> list = new ArrayList<Sketcher>();
    private SpiceManager spiceManager;
    private SketcherAdapter myGridAdapter;
    private RequestListener getAllSketchersListener = new RequestListener() {
        @Override
        public void onRequestFailure(SpiceException e) {
            e.printStackTrace();
            getActivity().setProgressBarIndeterminateVisibility(false);
        }

        @Override
        public void onRequestSuccess(Object o) {
            if (o != null) {
                if (o instanceof ArrayList) {
                    ArrayList<LinkedHashMap<String, String>> mList = (ArrayList<LinkedHashMap<String, String>>) o;
                    myGridAdapter.clear();
                    for (LinkedHashMap<String, String> obj : mList) {
                        myGridAdapter.add(new Sketcher(obj.get("udid"), obj.get("image"), obj.get("lat"), obj.get("lng"), obj.get("ip")));
                    }
                    myGridAdapter.notifyDataSetChanged();
                }
            }
            getActivity().setProgressBarIndeterminateVisibility(false);
        }
    };
    private RequestListener inviteAndCreateLobbyListener = new RequestListener() {
        @Override
        public void onRequestFailure(SpiceException e) {
            e.printStackTrace();
            getActivity().setProgressBarIndeterminateVisibility(false);
        }

        @Override
        public void onRequestSuccess(Object o) {

            if (o != null) {
                if (o instanceof String) {
                    final Intent intent = new Intent(getActivity(), FullscreenActivity.class);
                    intent.putExtra(FullscreenActivity.START_IO, true);
                    intent.putExtra(FullscreenActivity.INVITATION_TYPE, FullscreenActivity.HOST);

                    getActivity().startActivity(intent);
                }
            }

        }
    };
    private String invitedUUID;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SkercherGridFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spiceManager = DataManager.prepareSpiceManager(getActivity());

        final List<Sketcher> fakeList = new ArrayList<Sketcher>();
        myGridAdapter = new SketcherAdapter(getActivity(), list);

        getActivity().setProgressBarIndeterminateVisibility(true);
        AbsDataRequest request = new GetRequest(fakeList.getClass(), ApplicationManager.SERVER_URL);
        DataManager.performRequest(spiceManager, getActivity(), request, getAllSketchersListener, DurationInMillis.ONE_SECOND);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        final Sketcher itemAtPosition = (Sketcher) adapterView.getItemAtPosition(i);
        inviteAndStartNewRoom(itemAtPosition.getUuid());
    }

    private void inviteAndStartNewRoom(String id) {

        invitedUUID = id;
        final String from = ApplicationManager.getRegistrationId(getActivity());

        final AbsDataRequest request = new GetRequest(String.class, String.format(getString(R.string.backend_url_invite, from, id)));
        DataManager.performRequest(spiceManager, getActivity(), request, inviteAndCreateLobbyListener, DurationInMillis.ONE_SECOND);

        Toast.makeText(getActivity(), getString(R.string.toast_invite_sent),Toast.LENGTH_LONG).show();

        getActivity().setProgressBarIndeterminateVisibility(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        gridview.setAdapter(myGridAdapter);
        gridview.setOnItemClickListener(this);
    }

    public class SketcherAdapter extends MyBaseAdapter<Sketcher> {

        public SketcherAdapter(Context c, List<Sketcher> list) {
            super(c, R.layout.sketches_grid_view, list);
        }

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

            Sketcher item = getItem(position);

            //Picasso
            ApplicationManager.getImage(holder.img, item.getImageUrl());
            holder.txt.setText(item.getTitle());

            return convertView;
        }

        private class ViewHolder {

            public ImageView img;
            public TextView txt;
        }
    }

}
