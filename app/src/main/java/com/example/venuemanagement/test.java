package com.example.venuemanagement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class test extends Activity {

    private Spinner venueSpinner;
    private List<Venue> venueList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        venueSpinner = findViewById(R.id.fetch);
        venueList = new ArrayList<>();

        // Fetch venue data and populate the spinner
        new FetchVenuesTask().execute("http://10.0.2.2/venue_m/test.php");
    }

    private class FetchVenuesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String apiUrl = urls[0];
            String result = null;

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    InputStream in = urlConnection.getInputStream();
                    result = convertStreamToString(in);
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                parseVenueData(result);
                populateSpinner();
            }
        }

        private void parseVenueData(String result) {
            try {
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {
                    // Assuming the array contains strings directly
                    String venueName = jsonArray.getString(i);
                    venueList.add(new Venue(venueName));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        private void populateSpinner() {
            ArrayAdapter<Venue> adapter = new ArrayAdapter<>(test.this, android.R.layout.simple_spinner_item, venueList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            venueSpinner.setAdapter(adapter);
        }

        private String convertStreamToString(InputStream inputStream) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();

            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return stringBuilder.toString();
        }
    }

    // Venue class
    public class Venue {
        private String name;

        public Venue(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
