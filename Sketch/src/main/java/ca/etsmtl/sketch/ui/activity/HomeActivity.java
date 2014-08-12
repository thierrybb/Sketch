package ca.etsmtl.sketch.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ca.etsmtl.sketch.R;
import ca.etsmtl.sketch.request.RequestFactory;
import ca.etsmtl.sketch.utils.UserUtils;

public class HomeActivity extends Activity {
    private List<String> sketches = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getActionBar().setTitle("My sketches");

        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AsyncTask<Void, Void, JSONObject>() {

                            @Override
                            protected JSONObject doInBackground(Void... params) {
                                return RequestFactory.newGet(HomeActivity.this)
                                        .action("api/create_drawing")
                                        .executeForAnswer();
                            }

                            @Override
                            protected void onPostExecute(JSONObject response) {
                                if (response.has("bus")) {
                                    try {
                                        String bus = response.getString("bus");
                                        Integer port = response.getInt("bus_port");
                                        String drawingID = response.getString("drawing_id");

                                        Intent i = new Intent(HomeActivity.this, DrawingActivity.class);
                                        i.putExtra(DrawingActivity.DRAWING_ID_INTENT_KEY, drawingID);
                                        i.putExtra(DrawingActivity.DRAWING_BUS_SERVER_IP, bus);
                                        i.putExtra(DrawingActivity.DRAWING_BUS_PORT, port);
                                        startActivity(i);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        }.execute();
                    }
                }
        );


        Button logout = (Button) findViewById(R.id.button2);

        logout.setOnClickListener(new View.OnClickListener()

                                  {
                                      @Override
                                      public void onClick(View v) {
                                          UserUtils.logout(HomeActivity.this);
                                          startLoginActivity();
                                      }
                                  }
        );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        if (!UserUtils.isLogged(this)) {
            startLoginActivity();
        } else {
            new LoginTask(this, UserUtils.getUsername(this),
                    UserUtils.getPassword(this), new LoginTask.OnLoginListener() {
                @Override
                public void onFail() {
                    startLoginActivity();
                }

                @Override
                public void onSuccess() {
                    refreshDrawingList();
                }

                @Override
                public void onCancel() {

                }
            }
            ).execute();
        }

        super.onResume();
    }

    private void refreshDrawingList() {
        new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... params) {
                return RequestFactory.newGet(HomeActivity.this)
                        .action("api/list_drawing")
                        .executeForAnswer();
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                ListView listView = (ListView) findViewById(R.id.listView);
                sketches.clear();

                JSONArray drawings = null;
                try {
                    drawings = result.getJSONArray("drawings");

                    for (int i = 0; i < drawings.length(); i++) {
                        sketches.add(drawings.get(i).toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                ArrayAdapter<String> adapter = new ArrayAdapter<String>(HomeActivity.this,
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1, sketches.toArray(new String[0]));

                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        openSketch(sketches.get(position));
                    }
                });
            }
        }.execute();
    }

    private void openSketch(final String sketchID) {
        new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... params) {
                return RequestFactory.newGet(HomeActivity.this)
                        .action("api/open_drawing")
                        .addParameter("drawing", sketchID)
                        .executeForAnswer();
            }

            @Override
            protected void onPostExecute(JSONObject response) {
                if (response.has("bus")) {
                    try {
                        String bus = response.getString("bus");
                        Integer port = response.getInt("bus_port");
                        String drawingID = response.getString("drawing_id");

                        Intent i = new Intent(HomeActivity.this, DrawingActivity.class);
                        i.putExtra(DrawingActivity.DRAWING_ID_INTENT_KEY, drawingID);
                        i.putExtra(DrawingActivity.DRAWING_BUS_SERVER_IP, bus);
                        i.putExtra(DrawingActivity.DRAWING_BUS_PORT, port);
                        startActivity(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
