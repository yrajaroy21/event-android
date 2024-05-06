package com.example.venuemanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.Arrays;

public class org_manage_events extends AppCompatActivity {
    TextView title,category,date;
    Spinner live;
    Button update;
    String username,sid;
    private static final int REQUEST_CODE_NEXT_PAGE = 1;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_manage_events);
        title = findViewById(R.id.titleEvent);
        username=getIntent().getStringExtra("username");
        sid=getIntent().getStringExtra("sid");
        category = findViewById(R.id.categoryEvent);
        date = findViewById(R.id.dateEvent);
        live = findViewById(R.id.statusEvent);
        update = findViewById(R.id.updButton);

        Intent intent = getIntent();
        title.setText(intent.getStringExtra("title"));
        category.setText(intent.getStringExtra("category"));
        date.setText(intent.getStringExtra("date"));

        // Get the value of "live" from the intent
        String liveValue = intent.getStringExtra("live");

        // Set up the Spinner's adapter
        ArrayList<String> spinnerEntries = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.status_event)));

        // Add "TBD" to the Spinner's entries if it's not already present
        if (!spinnerEntries.contains("TBD")) {
            spinnerEntries.add("TBD");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerEntries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        live.setAdapter(adapter);

        // Set the Spinner's selection to "TBD"
        int position = adapter.getPosition(liveValue);
        live.setSelection(position != -1 ? position : spinnerEntries.indexOf("TBD"));
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected value from the Spinner
                String selectedLive = live.getSelectedItem().toString();

                // Make an HTTP POST request to the PHP script
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL("http://10.0.2.2/venue_m/update_event_org.php");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("POST");
                            conn.setDoOutput(true);

                            // Create the data to send
                            String postData = "title=" + URLEncoder.encode(title.getText().toString(), "UTF-8") +
                                    "&live=" + URLEncoder.encode(selectedLive, "UTF-8");

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

                                        // Handle the response based on the status
                                        if (status.equals("success")) {
                                            Toast.makeText(org_manage_events.this, ""+message, Toast.LENGTH_SHORT).show();
                                            Intent nextPageIntent = new Intent(org_manage_events.this, org_view_events.class);
                                            startActivityForResult(nextPageIntent, REQUEST_CODE_NEXT_PAGE);
                                        } else {
                                            Toast.makeText(org_manage_events.this, ""+message, Toast.LENGTH_SHORT).show();
                                            Intent nextPageIntent = new Intent(org_manage_events.this, org_view_events.class);
                                            startActivityForResult(nextPageIntent, REQUEST_CODE_NEXT_PAGE);
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