package com.example.venuemanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class user_profile_edit extends AppCompatActivity {
    TextView username, email;
    EditText contact;
    String sid;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_edit);

        username = findViewById(R.id.user_username);
        email = findViewById(R.id.user_email);
        contact = findViewById(R.id.user_contact);

        Intent intent = getIntent();
        username.setText(intent.getStringExtra("username"));
        email.setText(intent.getStringExtra("email"));
        contact.setText(intent.getStringExtra("contact"));
        sid = getIntent().getStringExtra("sid");
        save = findViewById(R.id.saveEdit);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDetails(contact.getText().toString());
            }
        });
    }

    private void updateDetails(String newContact) {
        String username = getIntent().getStringExtra("username");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.2.2/venue_m/update_userProfile.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    // Create data to send
                    String postData = "username=" + URLEncoder.encode(username, "UTF-8") +
                            "&new_contact=" + URLEncoder.encode(newContact, "UTF-8");

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
                    JSONObject jsonResponse = new JSONObject(response.toString());

                    // Process the response
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String status = jsonResponse.getString("status");
                                String message = jsonResponse.getString("message");

                                // Show a toast message with the response
                                Toast.makeText(user_profile_edit.this, message, Toast.LENGTH_SHORT).show();
                                if (status.equals("success")) {
                                    Intent intent = new Intent(user_profile_edit.this, user_profile.class);
                                    intent.putExtra("username", username.toString());
                                    intent.putExtra("sid", sid);
                                    Toast.makeText(user_profile_edit.this, sid, Toast.LENGTH_SHORT).show();// Replace PreviousActivity with your actual previous activity
                                    startActivity(intent);// Reload previous activity
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    // Disconnect the connection
                    conn.disconnect();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Reload previous activity here
        Intent intent = new Intent(user_profile_edit.this, user_profile.class);
        intent.putExtra("username", username.toString());
        intent.putExtra("sid", sid);// Replace PreviousActivity with your actual previous activity
        startActivity(intent);
    }
}
