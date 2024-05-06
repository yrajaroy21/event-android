package com.example.venuemanagement;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class add_event extends AppCompatActivity {
    private Spinner venueSpinner, categorySpinner;
    private EditText event_name, resource_person_name, resource_person_designation, start_date, start_time, end_date, end_time;
    private ImageButton rpImage, invitation;
    private List<String> venueList;
    private List<String> categoryList;
    String username,sid;
    BottomNavigationView bottomNavigationView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        sid=intent.getStringExtra("sid");
        event_name = findViewById(R.id.event_name);
        resource_person_name = findViewById(R.id.resource_person_name);
        resource_person_designation = findViewById(R.id.resource_person_designation);
        start_date = findViewById(R.id.start_date);
        start_time = findViewById(R.id.start_time);
        end_date = findViewById(R.id.end_date);
        end_time = findViewById(R.id.end_time);
        rpImage = findViewById(R.id.rp_Image);
        invitation = findViewById(R.id.addInvite);
        venueSpinner = findViewById(R.id.statusSpinner);
        categorySpinner = findViewById(R.id.spinnerF);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_home) {
                    Intent intent = new Intent(getApplicationContext(), view_event_registration.class);
                    intent.putExtra("username",username);
                    intent.putExtra("sid",sid);
                    startActivity(intent);
                }
                if (id == R.id.add_events) {
                    Intent intent = new Intent(getApplicationContext(), add_event.class);
                    intent.putExtra("username",username);
                    intent.putExtra("sid",sid);
                    startActivity(intent);
                }
                if (id == R.id.navigation_notifications) {
                    Intent intent = new Intent(getApplicationContext(), org_view_events.class);
                    intent.putExtra("username",username);
                    intent.putExtra("sid",sid);
                    startActivity(intent);
                }
            }
        });
        venueList = new ArrayList<>();
        categoryList = new ArrayList<>();

        // Fetch data
        new FetchData().execute("http://10.0.2.2/venue_m/test.php", "fetchData");

        rpImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });

        invitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickInvitationImageFromGallery();
            }
        });

        // Insert data (assuming there is a button with id 'submitButton')
        findViewById(R.id.save_venue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertDetails();
            }
        });
    }

    private void insertDetails() {
        String selectedVenue = venueSpinner.getSelectedItem().toString();
        String selectedCategory = categorySpinner.getSelectedItem().toString();
        // Get other details from your UI components

        // Now, you can perform the insert operation or send the data to your server/database.
        // Implement the code to insert the data into your server here.
        new InsertDetails().execute("http://10.0.2.2/venue_m/test.php", "insertDetails", selectedVenue, selectedCategory, event_name.getText().toString(), resource_person_name.getText().toString(), resource_person_designation.getText().toString(),
                start_date.getText().toString(), start_time.getText().toString(), end_date.getText().toString(), end_time.getText().toString());

    }

    private void pickInvitationImageFromGallery() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        invitationImageGalleryLauncher.launch(pickIntent);
    }

    private void pickImageFromGallery() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imageGalleryLauncher.launch(pickIntent);
    }

    ActivityResultLauncher<Intent> invitationImageGalleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            try {
                                Uri selectedImageUri = result.getData().getData();
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                        add_event.this.getContentResolver(),
                                        selectedImageUri
                                );
                                invitation.setImageBitmap(bitmap);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
    );

    ActivityResultLauncher<Intent> imageGalleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            try {
                                Uri selectedImageUri = result.getData().getData();
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                        add_event.this.getContentResolver(),
                                        selectedImageUri
                                );
                                rpImage.setImageBitmap(bitmap);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
    );

    private class FetchData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String apiUrl = params[0];
            String action = params[1];

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoOutput(true);

                    // Write the action parameter to the output stream
                    urlConnection.getOutputStream().write(("action=" + action).getBytes());

                    InputStream in = urlConnection.getInputStream();
                    return convertStreamToString(in);
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                parseData(result);
                populateSpinners();
            }
        }

        private void parseData(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);

                JSONArray venuesArray = jsonObject.getJSONArray("venues");
                JSONArray categoriesArray = jsonObject.getJSONArray("eventCategories");

                for (int i = 0; i < venuesArray.length(); i++) {
                    String venue = venuesArray.getString(i);
                    venueList.add(venue);
                }

                for (int i = 0; i < categoriesArray.length(); i++) {
                    String category = categoriesArray.getString(i);
                    categoryList.add(category);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        private void populateSpinners() {
            ArrayAdapter<String> venueAdapter = new ArrayAdapter<>(add_event.this, android.R.layout.simple_spinner_item, venueList);
            venueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            venueSpinner.setAdapter(venueAdapter);

            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(add_event.this, android.R.layout.simple_spinner_item, categoryList);
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(categoryAdapter);
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

    private class InsertDetails extends AsyncTask<String, Void, String> {


        protected String doInBackground(String... params) {
            String apiUrl = params[0];
            String action = params[1];
            String selectedVenue = params[2];
            String selectedCategory = params[3];
            String eventName = params[4];
            String resourcePersonName = params[5];
            String resourcePersonDesignation = params[6];
            String startDate = params[7];
            String startTime = params[8];
            String endDate = params[9];
            String endTime = params[10];
            // Get other details from params
            Bitmap imageBitmap = ((BitmapDrawable) rpImage.getDrawable()).getBitmap();
            String rpImageBase64 = encodeImageToBase64(imageBitmap);
            Bitmap invitationBitmap = ((BitmapDrawable) invitation.getDrawable()).getBitmap();
            ;
            String invitationBase64 = encodeImageToBase64(invitationBitmap);

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoOutput(true);

                    // Write the action parameter and other details to the output stream
                    urlConnection.getOutputStream().write(("action=" + action +
                            "&selectedVenue=" + selectedVenue +
                            "&selectedCategory=" + selectedCategory +
                            "&eventName=" + eventName +
                            "&resourcePersonName=" + resourcePersonName +
                            "&resourcePersonDesignation=" + resourcePersonDesignation +
                            "&startDate=" + startDate +
                            "&startTime=" + startTime +
                            "&endDate=" + endDate +
                            "&endTime=" + endTime +
                            "&rpImageBase64=" + rpImageBase64 +
                            "&invitationBase64=" + invitationBase64).getBytes());
                    Log.e("ero", "image" + rpImageBase64);

                    InputStream in = urlConnection.getInputStream();
                    return convertStreamToString(in);
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        private String convertStreamToString(InputStream inputStream) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append('\n');
                }

                return stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        private String encodeImageToBase64(Bitmap imageBitmap) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                handleResponse(result);
            } else {
                // Handle the case where the result is null or there was an error
                Toast.makeText(add_event.this, "Error: Null response or network issue", Toast.LENGTH_SHORT).show();
            }
        }

        private void handleResponse(String result) {
            // Display a toast message based on the server response
            Toast.makeText(add_event.this, result, Toast.LENGTH_SHORT).show();
            Log.e("er", "error" + result);

            // You can also perform additional actions based on the response
            if ("Details inserted successfully.".equals(result)) {
                Toast.makeText(add_event.this, result, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(add_event.this,view_event_registration.class);
                intent.putExtra("username",username);
                intent.putExtra("sid",sid);
                startActivity(intent);
                // The details were inserted successfully, you can navigate to another activity or perform other actions.
            } else {
                // There was an error in inserting details, handle it accordingly.
            }
        }
    }
}
