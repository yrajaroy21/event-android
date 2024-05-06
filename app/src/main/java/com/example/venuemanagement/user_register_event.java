    package com.example.venuemanagement;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.content.ContextCompat;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import android.annotation.SuppressLint;
    import android.content.Intent;
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

    public class user_register_event extends AppCompatActivity {
        private RecyclerView recyclerView;
        private EventAdapter eventAdapter;
        private List<Event> eventList;
        BottomNavigationView bottomNavigationView;
        String id, category, title, venue, date, profileurl;
        private String sid ;

        @SuppressLint("MissingInflatedId")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_register_event);
            Intent intent = getIntent();
            sid=intent.getStringExtra("sid");
            String username= intent.getStringExtra("username");
            recyclerView = findViewById(R.id.fetch_user_bookings);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            eventList = new ArrayList<>();
            eventAdapter = new EventAdapter(eventList);
            recyclerView.setAdapter(eventAdapter);
            String url = "http://10.0.2.2/venue_m/view_user_bookings.php";
            String postData = "student=" + sid;
            new FetchEventsTask().execute(url, postData);
            bottomNavigationView = findViewById(R.id.bottom_navigation_user);
            bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
                @Override
                public void onNavigationItemReselected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.user_home) {
                        Intent intent = new Intent(getApplicationContext(), user_home.class);
                        intent.putExtra("username",username);
                        intent.putExtra("sid",sid);
                        startActivity(intent);
                    }
                    if (id == R.id.user_book_events) {
                        Intent intent = new Intent(getApplicationContext(), user_register_event.class);
                        intent.putExtra("sid",sid);
                        startActivity(intent);
                    }
                }
            });
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
                            JSONArray capacityArray = jsonObject.getJSONArray("user_events");

                            for (int i = 0; i < capacityArray.length(); i++) {

                                // Fetch capacity from capacityArray
                                JSONObject capacityObject = capacityArray.getJSONObject(i);
                                category = capacityObject.getString("category");
                                profileurl = capacityObject.getString("image");
                                title = capacityObject.getString("title");
                                date = capacityObject.getString("start_date");


                                eventList.add(new Event( category, title, date, profileurl));
                            }
                            eventAdapter.notifyDataSetChanged();
                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(user_register_event.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(user_register_event.this, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(user_register_event.this, "Null response received", Toast.LENGTH_SHORT).show();
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
            public EventAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_events, parent, false);
                return new EventAdapter.EventViewHolder(view);
            }
            @Override
            public void onBindViewHolder(@NonNull EventAdapter.EventViewHolder holder, int position) {
                Event event = eventList.get(position);
                holder.titleTextView.setText(event.getTitle());
                holder.category.setText(event.getCategory());
                holder.date.setText(event.getDate());
                Picasso.get().load("http://10.0.2.2/venue_m/" + event.getImageur()).into(holder.img);
            }




            @Override
            public int getItemCount() {
                return eventList.size();
            }

            public class EventViewHolder extends RecyclerView.ViewHolder {
                TextView titleTextView,  category, date;
                ImageView img;

                public EventViewHolder(@NonNull View itemView) {
                    super(itemView);
                    titleTextView = itemView.findViewById(R.id.fetch_user_title);
                    category = itemView.findViewById(R.id.fetch_user_category);
                    date = itemView.findViewById(R.id.fetch_user_date);
                    img = itemView.findViewById(R.id.fetch_user_profile);
                }
            }
        }

        public class Event {
            private String category;
            private String title;
            private String date;


            private String imageur;

            public Event( String category, String title, String date, String imageur) {

                this.category = category;
                this.title = title;
                this.date = date;

                this.imageur = imageur;
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


            public String getImageur() {
                return imageur;
            }

            public void setImageur() {
                this.imageur = imageur;
            }

        }
    }