package ca.etsmtl.sketch.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.etsmtl.sketch.R;
import ca.etsmtl.sketch.surface.Collaborator;
import ca.etsmtl.sketch.surface.CollaboratorsCollection;

public class CollaboratorsDialog {
    private Context context;
    private CollaboratorsCollection collaborators;

    public CollaboratorsDialog(Context context, CollaboratorsCollection collaborators) {
        this.context = context;
        this.collaborators = collaborators;
    }

    public void show() {
        LayoutInflater inflater = LayoutInflater.from(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = inflater.inflate(R.layout.view_collaborators_dialog, null);

        ListView listview = (ListView) view.findViewById(R.id.listView);

        listview.setAdapter(new ListAdapter(context, collaborators));

        builder.setTitle(R.string.collaborators_dialog_caption)
                .setView(view)
                .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private class ListAdapter extends ArrayAdapter<Collaborator> {

        public ListAdapter(Context context, CollaboratorsCollection collaboratorsCollection) {
            super(context, R.layout.list_view_collaborators_item, new ArrayList<Collaborator>(collaboratorsCollection.values()));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.list_view_collaborators_item, null);
            }

            TextView viewById = (TextView) convertView.findViewById(R.id.collaborators_name);
            Collaborator currentCollaborator = getItem(position);
//            viewById.setBackgroundColor(currentCollaborator.getColors());
            viewById.setText(currentCollaborator.getName());

            return convertView;
        }
    }
}
