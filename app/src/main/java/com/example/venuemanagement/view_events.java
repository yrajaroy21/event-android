package com.example.venuemanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class view_events extends AppCompatActivity {
    private RecyclerView recyclerView;
    BottomNavigationView bottomNavigationView;
    private CustomAdapter adapter;
    private List<Events> dataList;
    TextView top;
    private List<Events> filteredList;
    String username;
    String url = "http://10.0.2.2/venue_m/view_events.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);

        username = getIntent().getStringExtra("username");
        top = findViewById(R.id.topEventsText);
        top.setText(username);

        dataList = new ArrayList<>();
        filteredList = new ArrayList<>(dataList);

        recyclerView = findViewById(R.id.recyclerEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CustomAdapter(filteredList);
        recyclerView.setAdapter(adapter);

        fetchFromPHP();

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

        SearchView searchView = findViewById(R.id.searchviewEvents);
        searchView.setFocusable(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void fetchFromPHP() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            dataList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String title = jsonObject.optString("title");
                                String venue = jsonObject.optString("venue");
                                String start = jsonObject.optString("start");
                                String end = jsonObject.optString("end");

                                Log.d("tag1", title);
                                Log.d("tag1", venue);
                                Log.d("tag1", start);
                                Log.d("tag1", end);

                                dataList.add(new Events(title, venue, start, end));
                            }
                            adapter.notifyDataSetChanged();
                            filter("");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleError(error);
            }
        });

        queue.add(stringRequest);
    }

    private void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(dataList);
        } else {
            text = text.toLowerCase().trim();
            for (Events item : dataList) {
                if (item.getTitle() != null && item.getVenue() != null && item.getStart() != null && item.getEnd() != null) {
                    if (item.getTitle().toLowerCase().contains(text)
                            || item.getVenue().toLowerCase().contains(text)
                            || item.getStart().toLowerCase().contains(text)
                            || item.getEnd().toLowerCase().contains(text)) {
                        filteredList.add(item);
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void handleError(VolleyError error) {
        if (error instanceof TimeoutError) {
            Toast.makeText(this, "Request timed out. Check your internet connection.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, error.toString().trim(), Toast.LENGTH_SHORT).show();
        }
    }

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        private List<Events> dataList;

        public CustomAdapter(List<Events> dataList) {
            this.dataList = dataList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_view, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Events event = dataList.get(position);
            Log.d("lastTag", event.getTitle());

            if (holder.titleTextView != null) {
                holder.titleTextView.setText("Title               :  " + (event.getTitle() != null ? event.getTitle() : ""));
            }
            if (holder.venueTextView != null) {
                holder.venueTextView.setText("Event Venue :  " + (event.getVenue() != null ? event.getVenue() : ""));
            }
            if (holder.startTextView != null) {
                holder.startTextView.setText("Start Date     :  " + (event.getStart() != null ? event.getStart() : ""));
            }
            if (holder.endTextView != null) {
                holder.endTextView.setText("End Date       :  " + (event.getEnd() != null ? event.getEnd() : ""));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String selectedItem = event.getTitle() != null ? event.getTitle() : "";
                    Intent intent = new Intent(view_events.this, manage_event.class);
                    intent.putExtra("title", selectedItem);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView, venueTextView, startTextView, endTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.category_event);
                venueTextView = itemView.findViewById(R.id.venue_event);
                startTextView = itemView.findViewById(R.id.venue_start);
                endTextView = itemView.findViewById(R.id.venue_end);
            }
        }
    }

    class Events {
        private String title;
        private String venue;
        private String start;
        private String end;

        public Events(String title, String venue, String start, String end) {
            this.title = title;
            this.venue = venue;
            this.start = start;
            this.end = end;
        }

        public String getTitle() {
            return title;
        }

        public String getVenue() {
            return venue;
        }

        public String getStart() {
            return start;
        }

        public String getEnd() {
            return end;
        }
    }
}
