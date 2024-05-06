package com.example.venuemanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class signUpUser extends AppCompatActivity {
    EditText createusername, newEmail, phoneNumber, newPassword, RePassword;
    Button signupUser;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_user);

        createusername = findViewById(R.id.createusername);
        newEmail = findViewById(R.id.newEmail);
        phoneNumber = findViewById(R.id.phoneNumber);
        newPassword = findViewById(R.id.newPassword);
        RePassword = findViewById(R.id.RePassword);
        signupUser = findViewById(R.id.signupUser);

        signupUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = createusername.getText().toString().trim();
                String email = newEmail.getText().toString().trim();
                String phone = phoneNumber.getText().toString().trim();
                String password = newPassword.getText().toString().trim();
                String confirmPassword = RePassword.getText().toString().trim();

                // Password and confirm password validation
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(signUpUser.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Make HTTP POST request to the PHP script
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL("http://10.0.2.2/venue_m/signUpuser.php");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("POST");
                            conn.setDoOutput(true);

                            // Create data to send
                            String postData = "username=" + URLEncoder.encode(username, "UTF-8") +
                                    "&email=" + URLEncoder.encode(email, "UTF-8") +
                                    "&phone=" + URLEncoder.encode(phone, "UTF-8") +
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
                                        String sid = jsonResponse.getString("id");
                                        if (status.equals("success")) {
                                            Toast.makeText(signUpUser.this, message, Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(signUpUser.this, user_home.class);
                                            intent.putExtra("sid", sid);
                                            intent.putExtra("username",username);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(signUpUser.this, message, Toast.LENGTH_SHORT).show();
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
