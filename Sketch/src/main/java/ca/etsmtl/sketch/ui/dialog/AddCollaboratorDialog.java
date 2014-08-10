package ca.etsmtl.sketch.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import ca.etsmtl.sketch.R;
import ca.etsmtl.sketch.request.RequestFactory;

public class AddCollaboratorDialog {
    private final Context context;
    private String sketchID;

    public AddCollaboratorDialog(Context context, String sketchID) {
        this.context = context;
        this.sketchID = sketchID;
    }

    public void show() {
        LayoutInflater inflater = LayoutInflater.from(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = inflater.inflate(R.layout.add_collaborators_dialog, null);

        final TextView email = (TextView) view.findViewById(R.id.email);

        builder.setTitle(R.string.add_collaborator_dialog_caption)
                .setView(view)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        addCollaborator(email.getText().toString(), dialog);
                    }
                })
                .setCancelable(true);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void addCollaborator(final String collaboratorMail, final DialogInterface dialogInterface) {
        new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... params) {
                return RequestFactory.newPost(context)
                        .action("api/add_collaborator")
                        .addParameter("drawingID", sketchID)
                        .addParameter("email", collaboratorMail)
                        .executeForAnswer();
            }

            @Override
            protected void onPostExecute(JSONObject response) {
                Boolean result = false;

                try {
                    if (response.has("result"))
                        result = response.getBoolean("result");
                } catch (Exception e) {

                }

                if (!result) {
                    Toast.makeText(context, "Unable to add collaborator", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Collaborator added", Toast.LENGTH_LONG).show();
                    dialogInterface.dismiss();
                }
            }
        }.execute();
    }
}
