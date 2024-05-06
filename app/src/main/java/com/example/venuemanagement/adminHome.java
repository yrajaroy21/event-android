package com.example.venuemanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class adminHome extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private List<VenueInfo> dataList;
    private CustomAdapter adapter;
    TextView toptext;
    ImageView nav_events,profile;
    String username;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        username = getIntent().getStringExtra("username");
        toptext = findViewById(R.id.admintext);
        toptext.setText(username);
        recyclerView = findViewById(R.id.recyclerView);
        profile = findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(adminHome.this, types_of_login.class);
                startActivity(intent);
            }
        });
        nav_events =findViewById(R.id.nav_events);
        nav_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(adminHome.this,view_events.class);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });
        dataList = new ArrayList<>();
        adapter = new CustomAdapter(dataList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setBackgroundColor(getResources().getColor(android.R.color.white));
        recyclerView.setAdapter(adapter);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
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

        new FetchVenueDetailsTask().execute();
    }

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        private List<VenueInfo> dataList;

        public CustomAdapter(List<VenueInfo> dataList) {
            this.dataList = dataList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.venue_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            VenueInfo venue = dataList.get(position);

            if (holder.nameTextView != null) {
                holder.nameTextView.setText("Venue: " + (venue.getName() != null ? venue.getName() : ""));
            }
            if (holder.capacityTextView != null) {
                holder.capacityTextView.setText("Capacity: " + (venue.getCapacity() != null ? venue.getCapacity() : ""));
            }
            if (holder.floorTextView != null) {
                holder.floorTextView.setText("Floor: " + (venue.getFloor() != null ? venue.getFloor() : ""));
            }
            if (holder.areaTextView != null) {
                holder.areaTextView.setText("Area: " + (venue.getArea() != null ? venue.getArea() : ""));
            }
            if (holder.locationTextView != null) {
                holder.locationTextView.setText("Location: " + (venue.getLocation() != null ? venue.getLocation() : ""));
            }
            if (holder.statusTextView != null) {
                holder.statusTextView.setText("Status: " + (venue.getStatus() != null ? venue.getStatus() : ""));
            }

            if (holder.profileImageView != null && venue.getImageUrl() != null && !venue.getImageUrl().isEmpty()) {
                String completeImageUrl = "http://10.0.2.2/venue_m/" + venue.getImageUrl();
                Picasso.get().load(completeImageUrl).into(holder.profileImageView);
            } else {
                holder.profileImageView.setImageResource(R.drawable.person);
            }
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameTextView, capacityTextView, floorTextView, areaTextView, locationTextView, statusTextView;
            ImageView profileImageView;

            public ViewHolder(View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.name);
                capacityTextView = itemView.findViewById(R.id.capacity);
                floorTextView = itemView.findViewById(R.id.floor);
                areaTextView = itemView.findViewById(R.id.area);
                locationTextView = itemView.findViewById(R.id.location);
                statusTextView = itemView.findViewById(R.id.status);
                profileImageView = itemView.findViewById(R.id.profile);
            }
        }
    }

    private class FetchVenueDetailsTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://10.0.2.2/venue_m/fetch_venues.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line).append("\n");
                }

                bufferedReader.close();
                inputStream.close();
                urlConnection.disconnect();

                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("ojo", "Result from server: " + result);

            handleVenueDetailsResponse(result);
        }
    }

    private void handleVenueDetailsResponse(String response) {
        if (response != null) {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                String status = jsonResponse.optString("status", "");

                if ("Success".equals(status)) {
                    JSONArray venueDataArray = jsonResponse.optJSONArray("data");

                    if (venueDataArray != null) {
                        for (int i = 0; i < venueDataArray.length(); i++) {
                            JSONObject venueData = venueDataArray.optJSONObject(i);

                            if (venueData == null) {
                                continue;
                            }

                            VenueInfo venue = new VenueInfo(
                                    venueData.optString("name", ""),
                                    venueData.optString("capacity", ""),
                                    venueData.optString("floor", ""),
                                    venueData.optString("area", ""),
                                    venueData.optString("location", ""),
                                    venueData.optString("status", ""),
                                    venueData.optString("image", "")
                            );
                            dataList.add(venue);
                        }

                        adapter.notifyDataSetChanged();
                    }
                } else if ("NoData".equals(status)) {
                    // Handle case when no venue data is available
                    Log.e("ojo", "No venue data available");
                } else {
                    // Handle other error cases
                    Log.e("ojo", "Error: " + status);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                // Handle JSON parsing error
                Log.e("ojo", "JSON parsing error");
            }
        } else {
            // Handle case when response is null
            Log.e("ojo", "Response is null");
        }
    }

    class VenueInfo {
        private String name;
        private String capacity;
        private String floor;
        private String area;
        private String location;
        private String status;
        private String imageUrl;

        public VenueInfo(String name, String capacity, String floor, String area, String location, String status, String imageUrl) {
            this.name = name;
            this.capacity = capacity;
            this.floor = floor;
            this.area = area;
            this.location = location;
            this.status = status;
            this.imageUrl = imageUrl;
        }

        public String getName() {
            return name;
        }

        public String getCapacity() {
            return capacity;
        }

        public String getFloor() {
            return floor;
        }

        public String getArea() {
            return area;
        }

        public String getLocation() {
            return location;
        }

        public String getStatus() {
            return status;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }
}
