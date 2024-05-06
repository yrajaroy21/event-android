package com.example.venuemanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class org_login extends AppCompatActivity {
    EditText id, upassword;
    Button loginUser;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_login);

        id = findViewById(R.id.orgusername);
        upassword = findViewById(R.id.orgpassword);
        loginUser = findViewById(R.id.loginOrg);


        loginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = id.getText().toString().trim();
                String password = upassword.getText().toString().trim();

                // Make HTTP POST request to the PHP script
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL("http://10.0.2.2/venue_m/login.php");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("POST");
                            conn.setDoOutput(true);

                            // Create data to send
                            String postData = "email=" + URLEncoder.encode(email, "UTF-8") +
                                    "&password=" + URLEncoder.encode(password, "UTF-8");

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

                                        // Handle the response
                                        if (status.equals("success")) {
                                            String id = jsonResponse.getString("id");
                                            String username = jsonResponse.getString("username");
                                            Toast.makeText(org_login.this, message, Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(org_login.this, view_event_registration.class);
                                            intent.putExtra("sid", id);
                                            intent.putExtra("username", username);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(org_login.this, message, Toast.LENGTH_SHORT).show();
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
        });

    }
}
