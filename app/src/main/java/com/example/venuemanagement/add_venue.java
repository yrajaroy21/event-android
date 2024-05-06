package com.example.venuemanagement;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class add_venue extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private EditText venueNameEditText, venueLocationEditText, venueCapacityEditText,
            venueFloorEditText, venueAreaEditText;
    private Button saveVenueButton;
    ImageButton image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_venue);

        String username = getIntent().getStringExtra("username");
        venueNameEditText = findViewById(R.id.venue_name);
        venueLocationEditText = findViewById(R.id.venue_location);
        venueCapacityEditText = findViewById(R.id.venue_capacity);
        venueFloorEditText = findViewById(R.id.venue_floor);
        venueAreaEditText = findViewById(R.id.venue_area);

        image = findViewById(R.id.addImage);
        saveVenueButton = findViewById(R.id.save_venue);
        saveVenueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                JSONObject venueData = new JSONObject();
                if(venueNameEditText.getText().toString().isEmpty() ||venueLocationEditText.getText().toString().isEmpty() ||venueCapacityEditText.getText().toString().isEmpty() ||venueFloorEditText.getText().toString().isEmpty()||venueAreaEditText.getText().toString().isEmpty())
                {

//                    Toast.makeText(add_venue.this, "All the fields except the Image are mandatory", Toast.LENGTH_LONG).show();
                    AlertDialog alertDialog = new AlertDialog.Builder(add_venue.this)
//set icon
                            .setIcon(android.R.drawable.ic_dialog_alert)
//set title
                            .setTitle("Mandatory")
//set message
                            .setMessage("All fields are mandatory")
//set positive button

//set negative button
                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //set what should happen when negative button is clicked
                                    setContentView(R.layout.activity_add_venue);
                                }
                            })
                            .show();



                }
                else {


                    try {

                        venueData.put("venueName", venueNameEditText.getText().toString());
                        venueData.put("venueLocation", venueLocationEditText.getText().toString());
                        venueData.put("venueCapacity", venueCapacityEditText.getText().toString());
                        venueData.put("venueFloor", venueFloorEditText.getText().toString());
                        venueData.put("venueArea", venueAreaEditText.getText().toString());

                        // Add more fields as needed
                        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                        String encodedImage = convertBitmapToBase64(bitmap);

                        // Add image data to JSON
                        venueData.put("image", encodedImage);

                        // Convert JSON data to string

                        // Convert JSON data to string
                        if (venueData.length()==0) {
                            Toast.makeText(add_venue.this, "All the fields except the Image are mandatory", Toast.LENGTH_SHORT).show();

                            setContentView(R.layout.activity_add_venue);

                        }
                        else{
                            String jsonData = venueData.toString();
                            new SendDataToServerTask().execute(jsonData);
                        }

                        // Send data to PHP server

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        image = findViewById(R.id.addImage);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_home) {
                    Intent intent = new Intent(getApplicationContext(), adminHome.class);
                    intent.putExtra("username",username);
                    startActivity(intent);
                }
                if (id == R.id.add_events) {
                    Intent intent = new Intent(getApplicationContext(), add_venue.class);
                    intent.putExtra("username",username);
                    startActivity(intent);
                }
                if (id == R.id.navigation_notifications) {
                    Intent intent = new Intent(getApplicationContext(), view_organizer.class);
                    intent.putExtra("username",username);
                    startActivity(intent);
                }

            }
        });
    }

    private void pickImageFromGallery() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imageGalleryLauncher.launch(pickIntent);
    }

    ActivityResultLauncher<Intent> imageGalleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (result.getData() != null) {
                            try {
                                Uri selectedImageUri = result.getData().getData();
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                        add_venue.this.getContentResolver(),
                                        selectedImageUri
                                );
                                image.setImageBitmap(bitmap);
                            } catch (Exception e) {

                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
    );

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private class SendDataToServerTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String jsonData = params[0];
            boolean success = false;

            try {
                // Replace YOUR_PHP_ENDPOINT with the actual PHP script URL
                URL url = new URL("http://10.0.2.2/venue_m/add_venue.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                // Set up the connection
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                // Write data to the server
                OutputStream outputStream = urlConnection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                outputStreamWriter.write(jsonData);
                outputStreamWriter.flush();

                // Get the response from the server
                int responseCode = urlConnection.getResponseCode();
                success = (responseCode == HttpURLConnection.HTTP_OK);

                // Close connections
                outputStreamWriter.close();
                outputStream.close();
                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(add_venue.this, "Data sent successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(add_venue.this, adminHome.class);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(add_venue.this, "Failed to send data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


