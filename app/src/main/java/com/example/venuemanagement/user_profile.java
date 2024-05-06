package com.example.venuemanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class user_profile extends AppCompatActivity {

    TextView userUsernameFetch, userEmailFetch, userContactFetch;
    String username, sid;
    Button edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userUsernameFetch = findViewById(R.id.user_username_fetch);
        userEmailFetch = findViewById(R.id.user_email_fetch);
        userContactFetch = findViewById(R.id.user_contact_fetch);
        edit = findViewById(R.id.editProfile);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        sid = intent.getStringExtra("sid");

        // Execute AsyncTask to fetch user profile data
        new FetchUserProfileTask().execute(username);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(user_profile.this, user_profile_edit.class);
                intent.putExtra("username", userUsernameFetch.getText().toString());
                intent.putExtra("email", userEmailFetch.getText().toString());
                intent.putExtra("contact", userContactFetch.getText().toString());
                startActivity(intent);
            }
        });

    }

    // AsyncTask to fetch user profile data in the background
    private class FetchUserProfileTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            String username = params[0];
            try {
                URL url = new URL("http://10.0.2.2/venue_m/user_profile_fetch.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Create data to send
                String postData = "username=" + URLEncoder.encode(username, "UTF-8");
                Log.e("sd","sd"+postData);
                // Write data to the connection
                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                // Get the response from the server
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                // Convert the response JSON string to a JSONObject
                return new JSONObject(response.toString());

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonResponse) {
            if (jsonResponse != null) {
                try {
                    String status = jsonResponse.getString("status");

                    // Check if the response is successful
                    if (status.equals("success")) {
                        // Get the fetched user details
                        String usernamenew = jsonResponse.getString("username");
                        String email = jsonResponse.getString("email");
                        String contact = jsonResponse.getString("phone");

                        // Set the fetched user details to the TextViews
                        userUsernameFetch.setText(usernamenew);
                        userEmailFetch.setText(email);
                        userContactFetch.setText(contact);
                    } else {
                        // Show an error message if fetching fails
                        String message = jsonResponse.getString("message");
                        Toast.makeText(user_profile.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(user_profile.this, "Failed to fetch user profile data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(user_profile.this, user_home.class);
        intent.putExtra("username", userUsernameFetch.getText().toString());
        intent.putExtra("sid", sid);
        startActivity(intent);
    }
}
