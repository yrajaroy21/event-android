package com.example.venuemanagement;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class org_view_events extends AppCompatActivity {
    String organizer ,sid;
    private RecyclerView re;

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_view_events);
        Intent intent = getIntent();
        organizer=intent.getStringExtra("username");
        sid=intent.getStringExtra("sid");
        re = findViewById(R.id.recyclerEvents);
        re.setLayoutManager(new LinearLayoutManager(this));
        // Call AsyncTask to fetch data from PHP script
        new FetchEventDataTask().execute(organizer);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_home) {
                    Intent intent = new Intent(getApplicationContext(), view_event_registration.class);
                    intent.putExtra("username",organizer);
                    intent.putExtra("sid",sid);
                    startActivity(intent);
                }
                if (id == R.id.add_events) {
                    Intent intent = new Intent(getApplicationContext(), add_event.class);
                    intent.putExtra("username",organizer);
                    intent.putExtra("sid",sid);
                    startActivity(intent);
                }
                if (id == R.id.navigation_notifications) {
                    Intent intent = new Intent(getApplicationContext(), org_view_events.class);
                    intent.putExtra("username",organizer);
                    intent.putExtra("sid",sid);
                    startActivity(intent);
                }
            }
        });
    }


    private class FetchEventDataTask extends AsyncTask<String, Void, String> {

        private static final String TAG = "FetchEventDataTask";

        @Override
        protected String doInBackground(String... params) {
            String organizer = params[0];
            String response = "";

            try {
                URL url = new URL("http://10.0.2.2/venue_m/view_org_events.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                // Create the POST data
                String postData = "organizer=" + organizer;

                // Write the POST data to the connection
                connection.getOutputStream().write(postData.getBytes());

                // Get the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response += line;
                }

                // Close connections
                reader.close();
                connection.disconnect();
            } catch (IOException e) {
                Log.e(TAG, "Error fetching data: " + e.getMessage());
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
//            Toast.makeText(org_view_events.this, "sd" + result, Toast.LENGTH_SHORT).show();
            // Parse and handle the JSON response here
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.getString("status");
                if (status.equals("success")) {
                    JSONArray dataArray = jsonObject.getJSONArray("data");
                    ArrayList<EventModel> events = new ArrayList<>();
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject eventObject = dataArray.getJSONObject(i);
                        String live = eventObject.getString("live");

                        String title = eventObject.getString("title");
                        String category = eventObject.getString("category");
                        String image = eventObject.getString("image");
                        String startDate = eventObject.getString("start_date");
                        EventModel event = new EventModel(live, title, category, image, startDate);
                        events.add(event);
                    }

                    // Create an adapter and set it to the RecyclerView
                    EventAdapter adapter = new EventAdapter(events);
                    re.setAdapter(adapter);

                } else {
                    String errorMessage = jsonObject.getString("message");
                    Log.e(TAG, "Error: " + errorMessage);
                }
            } catch (JSONException e) {
                Log.e(TAG, "JSON parsing error: " + e.getMessage());
            }
        }
    }

    public static class EventModel {
        private String live;
        private String title;
        private String category;
        private String image;
        private String startDate;

        public EventModel(String live, String title, String category, String image, String startDate) {
            this.live = live;
            this.title = title;
            this.category = category;
            this.image = image;
            this.startDate = startDate;
        }

        // Add getter methods here
        public String getLive() {
            return live;
        }

        public String getTitle() {
            return title;
        }

        public String getCategory() {
            return category;
        }

        public String getImage() {
            return image;
        }

        public String getStartDate() {
            return startDate;
        }
    }

    class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
        private ArrayList<EventModel> eventList;

        public EventAdapter(ArrayList<EventModel> eventList) {
            this.eventList = eventList;
        }

        @NonNull
        @Override
        public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            Toast.makeText(parent.getContext(), "onCreateViewHolder called", Toast.LENGTH_SHORT).show();

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.org_card_view, parent, false);
            return new EventViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
            EventModel event = eventList.get(position);
            holder.title.setText(event.getTitle());
            holder.catgory.setText(event.getCategory());
            holder.date.setText(event.getStartDate());
            Picasso.get().load("http://10.0.2.2/venue_m/" + event.getImage()).into(holder.img);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(org_view_events.this,org_manage_events.class);
                    intent.putExtra("title",event.getTitle());
                    intent.putExtra("category",event.getCategory());
                    intent.putExtra("date",event.getStartDate());
                    intent.putExtra("live",event.getLive());
                    intent.putExtra("username",organizer);
                    intent.putExtra("sid",sid);
                    startActivity(intent);
                }
            });
            // Bind more data if needed
        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }

        class EventViewHolder extends RecyclerView.ViewHolder {
            TextView title, catgory, date;

            ImageView img;

            EventViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.fetch_title);
                catgory = itemView.findViewById(R.id.fetch_category);
                date = itemView.findViewById(R.id.fetch_date);
                img = itemView.findViewById(R.id.fetch_profile);
                // Initialize other UI elements here if needed
            }
        }
    }
}
