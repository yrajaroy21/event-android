package com.example.venuemanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class manage_organiser extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Button update;
    String bioid;
    TextView id, name, email, ad;
    private Spinner status;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_organiser);
        ad = findViewById(R.id.ad);
        ad.setText(getIntent().getStringExtra("username"));
                // Initialize UI components
                id = findViewById(R.id.bioid);
        name = findViewById(R.id.org_name);
        email = findViewById(R.id.org_mail);
        status = findViewById(R.id.statusSpinner);

        Intent intent = getIntent();
        bioid = intent.getStringExtra("bioid");

        // Call AsyncTask to retrieve organizer details
        new GetOrganizerDetailsTask().execute(bioid);

        update = findViewById(R.id.save_venue);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedStatus = status.getSelectedItem().toString();

                // Call AsyncTask to update organizer status
                new UpdateOrganizerStatusTask().execute(bioid, updatedStatus);
            }
        });
    }

    private class UpdateOrganizerStatusTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String bioid = params[0];
            String updatedStatus = params[1];
            String urlString = "http://10.0.2.2/venue_m/update_organizers.php";
            String result = null;

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                // Set the request method to POST
                urlConnection.setRequestMethod("POST");

                // Enable input and output streams
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                // Build the POST parameters
                String postData = "bioid=" + URLEncoder.encode(bioid, "UTF-8") +
                        "&status=" + URLEncoder.encode(updatedStatus, "UTF-8");

                // Write the POST data
                OutputStream outputStream = urlConnection.getOutputStream();
                outputStream.write(postData.getBytes());
                outputStream.flush();
                outputStream.close();

                // Get the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                reader.close();
                result = stringBuilder.toString();

            } catch (Exception e) {
                Log.e("HTTP", "Error in doInBackground: " + e.toString());
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonResult = new JSONObject(result);
                    // Check if the response contains a "message" field
                    if (jsonResult.has("message")) {
                        String message = jsonResult.getString("message");
                        // Display a success message
                        Toast.makeText(manage_organiser.this, message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(manage_organiser.this,adminHome.class);
                        intent.putExtra("username",ad.getText().toString());
                        startActivity(intent);
                    } else {
                        // Handle other scenarios if needed
                        Log.e("UpdateResult", "Unexpected response: " + result);
                    }
                } catch (JSONException e) {
                    Log.e("JSON", "Error parsing JSON: " + e.toString());
                    Toast.makeText(manage_organiser.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(manage_organiser.this, "Failed to update status", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private class GetOrganizerDetailsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String bioid = params[0];
            String urlString = "http://10.0.2.2/venue_m/get_organizer_details.php";
            String result = null;

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                // Set the request method to POST
                urlConnection.setRequestMethod("POST");

                // Enable input and output streams
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                // Build the POST parameters
                String postData = "bioid=" + bioid;

                // Write the POST data
                OutputStream outputStream = urlConnection.getOutputStream();
                outputStream.write(postData.getBytes());
                outputStream.flush();
                outputStream.close();

                // Get the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                reader.close();
                result = stringBuilder.toString();

            } catch (Exception e) {
                Log.e("HTTP", "Error in doInBackground: " + e.toString());
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // Parse the JSON response and update UI
                try {
                    JSONObject jsonResult = new JSONObject(result);
                    Log.e("result", "response" + result);

                    // Extract values directly without checking for "bioid" existence
                    String organizerId = jsonResult.getString("bioid");
                    String organizerName = jsonResult.getString("username");
                    String organizerEmail = jsonResult.getString("email");
                    String organizerStatus = jsonResult.getString("status");

                    // Update UI with organizer details
                    id.setText(organizerId);
                    name.setText(organizerName);
                    email.setText(organizerEmail);

                    ArrayAdapter<String> spinnerAdapter = (ArrayAdapter<String>) status.getAdapter();
                    int position = spinnerAdapter.getPosition(organizerStatus);
                    status.setSelection(position);
                } catch (JSONException e) {
                    Log.e("JSON", "Error parsing JSON: " + e.toString());
                    Toast.makeText(manage_organiser.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(manage_organiser.this, "Failed to retrieve organizer details", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
