package ca.etsmtl.sketch.ui.activity;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import ca.etsmtl.sketch.request.RequestFactory;

public class LoginTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private final String mEmail;
    private final String mPassword;
    private OnLoginListener listener;

    public interface OnLoginListener {
        void onFail();
        void onSuccess();
        void onCancel();
    }

    public LoginTask(Context context, String email, String password, OnLoginListener listener) {
        this.context = context;
        mEmail = email;
        mPassword = password;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        JSONObject response = RequestFactory.newPost(context)
                .action("api/login")
                .addParameter("email", mEmail)
                .addParameter("password", mPassword)
                .executeForAnswer();

        try {
            return Boolean.parseBoolean(response.get("login").toString());
        } catch (JSONException e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            listener.onSuccess();
        } else {
            listener.onFail();

        }
    }

    @Override
    protected void onCancelled() {
        listener.onCancel();
    }
}