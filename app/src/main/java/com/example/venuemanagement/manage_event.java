package com.example.venuemanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class manage_event extends AppCompatActivity {
    String title;
    TextView category, name, guest, designation, venue, start_date, end_date, start_time, end_time, organizer;
    Spinner status;
    Button save;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_event);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");

        category = findViewById(R.id.category_venue_f);
        name = findViewById(R.id.venue_event_name);
        guest = findViewById(R.id.guest_name);
        designation = findViewById(R.id.designation_event);
        venue = findViewById(R.id.venue_event_f);
        start_date = findViewById(R.id.start_date_event);
        end_date = findViewById(R.id.end_date_event);
        start_time = findViewById(R.id.start_time_event);
        end_time = findViewById(R.id.end_time_event);
        organizer = findViewById(R.id.organized_by);
        status = findViewById(R.id.statusVenueSpinner);
        save = findViewById(R.id.save_event);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newStatus = status.getSelectedItem().toString();

                // Execute AsyncTask to update status
                new UpdateStatusTask().execute(title, newStatus);
            }
        });
        // Execute AsyncTask to fetch event details
        new FetchEventDetailsTask().execute(title);
    }
    private class UpdateStatusTask extends AsyncTask<String, Void, String> {

        private static final String PHP_UPDATE_URL = "http://10.0.2.2/venue_m/events.php";

        @Override
        protected String doInBackground(String... params) {
            String title = params[0];
            String newStatus = params[1];
            String action = "updateStatus";

            try {
                URL url = new URL(PHP_UPDATE_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                // Set up the connection properties
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                // Build the POST data
                String postData = "action=" + action + "&title=" + title + "&newStatus=" + newStatus;

                // Get the output stream to send the POST data
                OutputStream os = urlConnection.getOutputStream();
                os.write(postData.getBytes("UTF-8"));
                os.flush();
                os.close();

                // Get the response from the server
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the response
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }

                    in.close();
                    return response.toString();
                } else {
                    return "Error: " + responseCode;
                }
            } catch (IOException e) {
                return "Error: " + e.getMessage();
            }
        }
        protected void onPostExecute(String result) {
            // Handle the result of the status update if needed
            // You can show a Toast or log a message
            Log.d("UpdateStatusTask", "Status update result: " + result);
        }
    }



    // AsyncTask to fetch event details from PHP backend
    private class FetchEventDetailsTask extends AsyncTask<String, Void, String> {

        private static final String PHP_URL = "http://10.0.2.2/venue_m/events.php";

        @Override
        protected String doInBackground(String... params) {
            String title = params[0];
            String action = "getEventDetails";

            try {
                URL url = new URL(PHP_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                // Set up the connection properties
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                // Build the POST data
                String postData = "action=" + action + "&title=" + title;

                // Get the output stream to send the POST data
                OutputStream os = urlConnection.getOutputStream();
                os.write(postData.getBytes("UTF-8"));
                os.flush();
                os.close();

                // Get the response from the server
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the response
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }

                    in.close();
                    return response.toString();
                } else {
                    return "Error: " + responseCode;
                }
            } catch (IOException e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override


        protected void onPostExecute(String result) {
            try {
                // Parse the JSON response as an array
                JSONArray jsonArray = new JSONArray(result);

                // Check if the array is not empty
                if (jsonArray.length() > 0) {
                    // Assuming the first element in the array is the event details
                    JSONObject eventObject = jsonArray.getJSONObject(0);

                    // Update UI with fetched data
                    category.setText(eventObject.optString("category"));
                    name.setText(eventObject.optString("name"));
                    guest.setText(eventObject.optString("guest"));
                    designation.setText(eventObject.optString("designation"));
                    venue.setText(eventObject.optString("venue"));
                    start_date.setText(eventObject.optString("start_date"));
                    end_date.setText(eventObject.optString("end_date"));
                    start_time.setText(eventObject.optString("start_time"));
                    end_time.setText(eventObject.optString("end_time"));
                    organizer.setText(eventObject.optString("organizer"));

                    // You can also update the status spinner here
                    // For example, assuming you have an ArrayAdapter for the spinner:
                    String statusValue = eventObject.optString("status");
                    int position = getStatusPosition(statusValue);
                    status.setSelection(position);
                } else {
                    // Handle case where no event details are found
                    Log.e("FetchEventDetailsTask", "No event details found in the response.");
                }
            } catch (JSONException e) {
                Log.e("FetchEventDetailsTask", "Error parsing JSON response: " + e.getMessage());
            }
        }


        // Helper method to get the position of the status in the spinner
        private int getStatusPosition(String statusValue) {
            // Assuming you have an array of status values for the spinner
            String[] statusArray = getResources().getStringArray(R.array.event_status);

            for (int i = 0; i < statusArray.length; i++) {
                if (statusArray[i].equalsIgnoreCase(statusValue)) {
                    return i;
                }
            }

            return 0; // Default position if not found
        }

    }
}
