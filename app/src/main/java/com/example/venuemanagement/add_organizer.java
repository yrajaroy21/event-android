package com.example.venuemanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class add_organizer extends AppCompatActivity {
    EditText Bioid, Name, email;
    Spinner status;
    Button add;
    TextView topadd;
    String username;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_organizer);
        topadd = findViewById(R.id.topadd);
        username=getIntent().getStringExtra("username");
        topadd.setText(username);
        Bioid = findViewById(R.id.bioid);
        Name = findViewById(R.id.org_name_add);
        email=findViewById(R.id.org_email_add);
        status=findViewById(R.id.statusSpinner);
        add = findViewById(R.id.add_organizer);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve values from the EditText fields and Spinner
                String bioidValue = Bioid.getText().toString();
                String nameValue = Name.getText().toString();
                String emailValue = email.getText().toString();
                String statusValue = status.getSelectedItem().toString();

                // Execute AsyncTask to send data to server
                new AddOrganizerTask().execute(bioidValue, nameValue, emailValue, statusValue);
            }
        });
    }

    private class AddOrganizerTask extends AsyncTask<String, Void, String> {
        // Replace "http://your_domain.com/insert.php" with the actual URL of your PHP script
        private static final String INSERT_URL = "http://10.0.2.2/venue_m/add_organizer.php";

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String responseJsonString = null;

            try {
                // URL of your PHP script
                URL url = new URL(INSERT_URL);

                // Create connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                // Encode the data as form data
                String formData = "Bioid=" + URLEncoder.encode(params[0], "UTF-8")
                        + "&Name=" + URLEncoder.encode(params[1], "UTF-8")
                        + "&email=" + URLEncoder.encode(params[2], "UTF-8")
                        + "&status=" + URLEncoder.encode(params[3], "UTF-8");

                // Write form data to the connection
                OutputStream outputStream = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(formData);
                writer.flush();
                writer.close();
                outputStream.close();

                // Get the response
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // No response
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    // Empty response
                    return null;
                }
                responseJsonString = buffer.toString();
            } catch (IOException e) {
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                    }
                }
            }
            return responseJsonString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Handle the response here
            if (result != null) {
                try {
                    // Parse the JSON response
                    JSONObject jsonResponse = new JSONObject(result);
                    boolean success = jsonResponse.getBoolean("success");
                    String message = jsonResponse.getString("message");
                    if (success) {
                        // Handle successful insertion
                        Toast.makeText(add_organizer.this, message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(add_organizer.this,view_organizer.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Handle insertion failure
                        Toast.makeText(add_organizer.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // Handle JSON parsing error
                    e.printStackTrace();
                }
            } else {
                // Handle null response
                Toast.makeText(add_organizer.this, "Null response", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
