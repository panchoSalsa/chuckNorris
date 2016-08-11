package com.example.franciscofranco.chucknorris;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

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

    }

    public void getJokes(View view) {
        makeAsyncRequest();
    }

    public void makeAsyncRequest() {
        String url = "http://api.icndb.com/jokes/random/" + Integer.toString(jokeCount);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
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

}
