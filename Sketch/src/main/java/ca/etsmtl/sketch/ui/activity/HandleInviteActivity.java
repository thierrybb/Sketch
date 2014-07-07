package ca.etsmtl.sketch.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import ca.etsmtl.sketch.R;

/**
 * Created by philippe on 28/11/13.
 */
public class HandleInviteActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // exposing deep links into your app, handle intents here.

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        setContentView(R.layout.invite_activity);

        //TODO: create service for data exchange

    }

}
