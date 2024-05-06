package com.example.venuemanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.metrics.Event;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class user_home extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    TextView toptext;
    ImageView profile, logout;
    String id, category, title, venue, date, profileurl;
    private String sid, username;
    int capacity, count, studentsCount, temp;
    BottomNavigationView bottomNavigationView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        Intent intent = getIntent();
        sid = intent.getStringExtra("sid");
        username = intent.getStringExtra("username");
        logout = findViewById(R.id.logoutButton);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(user_home.this, types_of_login.class);
                startActivity(intent);
            }
        });
        toptext = findViewById(R.id.topText);
        toptext.setText(username);
        recyclerView = findViewById(R.id.userEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Call your reload method here
                reloadPage();
            }
        });
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList);
        recyclerView.setAdapter(eventAdapter);

        String url = "http://10.0.2.2/venue_m/fetch_event_re.php";
        String postData = "action=fetch&student=" + sid;
        new FetchEventsTask().execute(url, postData);
        bottomNavigationView = findViewById(R.id.bottom_navigation_user);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.user_home) {
                    Intent intent = new Intent(getApplicationContext(), user_home.class);
                    intent.putExtra("username", username);
                    intent.putExtra("sid", sid);
                    startActivity(intent);
                }
                if (id == R.id.user_book_events) {
                    Intent intent = new Intent(getApplicationContext(), user_register_event.class);
                    intent.putExtra("sid", sid);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
            }
        });
        profile = findViewById(R.id.uprofile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(user_home.this, user_profile.class);
                intent.putExtra("username", username);
                intent.putExtra("sid", sid);
                startActivity(intent);
            }
        });
    }

    private void reloadPage() {
        // Restart the activity
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private class FetchEventsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String urlStr = params[0];
            String postData = params[1];

            try {
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                OutputStream os = connection.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line).append('\n');
                }
                reader.close();

                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {
                        JSONArray eventsArray = jsonObject.getJSONArray("events");
                        JSONArray capacityArray = jsonObject.getJSONArray("venue");
                        JSONArray countArray = jsonObject.getJSONArray("count");
                        JSONArray studentArray = jsonObject.getJSONArray("student_count");
                        for (int i = 0; i < eventsArray.length(); i++) {
                            JSONObject eventObject = eventsArray.getJSONObject(i);
                            id = eventObject.getString("id");
                            category = eventObject.getString("category");
                            title = eventObject.getString("title");
                            date = eventObject.getString("start_date");
                            venue = eventObject.getString("event_venue");
                            profileurl = eventObject.getString("image");
                            // Fetch capacity from capacityArray
                            JSONObject capacityObject = capacityArray.getJSONObject(i);
                            capacity = capacityObject.getInt("capacity");

                            // Fetch count from countArray
                            JSONObject countObject = countArray.getJSONObject(i);
                            count = countObject.getInt("count");

                            // Fetch student count from studentArray
                            JSONObject studentObject = studentArray.getJSONObject(i);
                            studentsCount = studentObject.getInt("student_count");

                            eventList.add(new Event(id, category, title, date, venue, capacity, count, studentsCount, profileurl));
                        }
                        eventAdapter.notifyDataSetChanged();
                    } else {
                        String message = jsonObject.getString("message");
                        Toast.makeText(user_home.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(user_home.this, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(user_home.this, "Null response received", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

        private List<Event> eventList;

        public EventAdapter(List<Event> eventList) {
            this.eventList = eventList;
        }

        @NonNull
        @Override
        public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
            return new EventViewHolder(view);
        }

        @Override

        public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
            Event event = eventList.get(position);
            holder.titleTextView.setText(event.getTitle());
            holder.venueTextView.setText(event.getVenue());
            holder.category.setText(event.getCategory());
            holder.date.setText(event.getDate());

//            int availableCapacity = 0;
            int availableCapacity = event.getCapacity() - event.getCount();
            holder.capacityCountTextView.setText(String.valueOf(availableCapacity));
            Picasso.get().load("http://10.0.2.2/venue_m/" + event.getImageur()).into(holder.img);
// Check if available capacity is less than 25% of total capacity
            if (availableCapacity < event.getCapacity() / 4) {
                // Set text color to red
                holder.capacityCountTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_dark));
            } else if (availableCapacity > event.getCapacity() / 4 && availableCapacity < event.getCapacity() / 2) {
                // Set text color to orange
                holder.capacityCountTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_orange_dark));
            } else {
                // Set text color to default color
                holder.capacityCountTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark));
            }
            if (availableCapacity == 0) {
                holder.registerButton.setVisibility(View.INVISIBLE);
                holder.ss.setVisibility(View.VISIBLE);
                holder.ss.setText("Seats full");
            } else {
                if (event.getStudentCount() >= 1) {
                    holder.registerButton.setVisibility(View.INVISIBLE);
                    holder.ss.setVisibility(View.VISIBLE);
                } else {
                    holder.registerButton.setEnabled(true);
                    holder.registerButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            registerStudent(event.getId().toString(), sid);
                        }
                    });
                }

            }
        }

        private void registerStudent(String eventId, String studentId) {
            String url = "http://10.0.2.2/venue_m/fetch_event_re.php";
            String postData = "action=register&event_id=" + eventId + "&student_id=" + studentId;
            Log.e("sds", "ssdd" + postData);
            new RegisterStudentTask().execute(url, postData);
        }

        // AsyncTask to register the student
        private class RegisterStudentTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String urlStr = params[0];
                String postData = params[1];

                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);

                    OutputStream os = connection.getOutputStream();
                    os.write(postData.getBytes());
                    os.flush();
                    os.close();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line).append('\n');
                    }
                    reader.close();
                    Log.e("sd", "sd" + response);
                    return response.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                // Handle the response from the PHP script
                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {

                            Toast.makeText(user_home.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            reloadPage();
                        } else {
                            // Registration failed, handle accordingly
                            // For example, show an error message to the user
                            String message = jsonObject.getString("message");
                            Toast.makeText(user_home.this, message, Toast.LENGTH_SHORT).show();
                            reloadPage();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                        Toast.makeText(user_home.this, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                        reloadPage();
                    }
                } else {
//                   example, show an error message to the user
                    Toast.makeText(user_home.this, "Null response received", Toast.LENGTH_SHORT).show();
                    reloadPage();
                }
            }

        }


        @Override
        public int getItemCount() {
            return eventList.size();
        }

        public class EventViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView, venueTextView, capacityCountTextView, category, date, ss;
            Button registerButton;
            ImageView img;

            public EventViewHolder(@NonNull View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.titleTextView);
                venueTextView = itemView.findViewById(R.id.venueTextView);
                capacityCountTextView = itemView.findViewById(R.id.capacityCountTextView);
                registerButton = itemView.findViewById(R.id.registerButton);
                category = itemView.findViewById(R.id.categoryTextView);
                ss = itemView.findViewById(R.id.status);
                date = itemView.findViewById(R.id.date_assign);
                img = itemView.findViewById(R.id.fetch_profile_image);
            }
        }
    }

    public class Event {
        private String id;
        private String category;
        private String title;
        private String date;
        private String venue;
        private int capacity;
        private int count;
        private int studentCount;
        private String imageur;

        public Event(String id, String category, String title, String date, String venue, int capacity, int count, int studentCount, String imageur) {
            this.id = id;
            this.category = category;
            this.title = title;
            this.date = date;
            this.venue = venue;
            this.capacity = capacity;
            this.count = count;
            this.studentCount = studentCount;
            this.imageur = imageur;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getVenue() {
            return venue;
        }

        public void setVenue(String venue) {
            this.venue = venue;
        }

        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getStudentCount() {
            return studentCount;
        }

        public void setStudentCount(int studentCount) {
            this.studentCount = studentCount;
        }

        public String getImageur() {
            return imageur;
        }

        public void setImageur() {
            this.imageur = imageur;
        }

    }


}
