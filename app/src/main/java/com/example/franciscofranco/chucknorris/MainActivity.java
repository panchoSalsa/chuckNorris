package com.example.franciscofranco.chucknorris;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private SeekBar seekBar;
    private TextView textView;
    private LinkedList<String> jokes;
    private ArrayAdapter<String> itemsAdapter;
    private int jokeCount = 1;
    private static final String PREFS = "prefs";
    private static final String PREF_NAME = "name";
    SharedPreferences mSharedPreferences;
    private String customName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        textView = (TextView) findViewById(R.id.textView);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                jokeCount = progress + 1;
                textView.setText(String.valueOf(jokeCount));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        jokes = new LinkedList<String>();

        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, jokes);
        listView.setAdapter(itemsAdapter);
        displayWelcome();

    }

    public void getJokes(View view) {
        makeAsyncRequest();
    }

    public void makeAsyncRequest() {
        String url = "http://api.icndb.com/jokes/random/" + Integer.toString(jokeCount);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        String[] name = customName.split(" ");
        params.put("firstName", name[0]);
        params.put("lastName", name[1]);
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                jokes.clear();
                retrieveJokes(response);
                itemsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Throwable t, JSONObject error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d("FRANCO_DEBUG", "failed");
            }
        });
    }

    public void retrieveJokes(JSONObject obj) {
        try {
            JSONArray valueArray = obj.getJSONArray("value");
            for (int i = 0; i < valueArray.length(); ++i) {
                JSONObject joke = valueArray.getJSONObject(i);
                jokes.add(joke.getString("joke"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void displayWelcome() {

        // Access the device's key-value storage
        mSharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);

        // Read the user's name,
        // or an empty string if nothing found
        String name = mSharedPreferences.getString(PREF_NAME, "");
        customName = name;
        if (name.length() > 0) {

            // If the name is valid, display a Toast welcoming them
            Toast.makeText(this, "Welcome back, " + name + "!", Toast.LENGTH_LONG).show();
        }  else {

            // otherwise, show a dialog to ask for their name
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Hello!");
            alert.setMessage("What is your name?");

            // Create EditText for entry
            final EditText input = new EditText(this);
            alert.setView(input);

            // Make an "OK" button to save the name
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {

                    // Grab the EditText's input
                    String inputName = input.getText().toString();

                    // Put it into memory (don't forget to commit!)
                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putString(PREF_NAME, inputName);
                    e.commit();

                    customName = inputName;

                    // Welcome the new user
                    Toast.makeText(getApplicationContext(), "Welcome, " + inputName + "!", Toast.LENGTH_LONG).show();
                }
            });

            // Make a "Cancel" button
            // that simply dismisses the alert
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {}
            });

            alert.show();
        }
    }

}
