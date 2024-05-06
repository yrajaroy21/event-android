package com.example.venuemanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class view_organizer extends AppCompatActivity {

    private RecyclerView recyclerView;
    BottomNavigationView bottomNavigationView;
    private CustomAdapter adapter;
    Button addu;
    private List<Organizer> dataList;
    private List<Organizer> filteredList;
    TextView top;
    String username;
    String url = "http://10.0.2.2/venue_m/view_organizer.php";

    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_organizer);
        username = getIntent().getStringExtra("username");
        addu=findViewById(R.id.add);
        addu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view_organizer.this,add_organizer.class);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });
        top = findViewById(R.id.adminttext);
        top.setText(username);
        dataList = new ArrayList<>();
        filteredList = new ArrayList<>(dataList);
        username = getIntent().getStringExtra("username");
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomAdapter(filteredList);
        recyclerView.setAdapter(adapter);
        fetchfromPHP();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_home) {
                    Intent intent = new Intent(getApplicationContext(), adminHome.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
                if (id == R.id.add_events) {
                    Intent intent = new Intent(getApplicationContext(), add_venue.class);
                    intent.putExtra("username",username);
                    startActivity(intent);
                }
                if (id == R.id.navigation_notifications) {
                    Intent intent = new Intent(getApplicationContext(), view_organizer.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }

            }
        });
        SearchView searchView = findViewById(R.id.searchview);
        searchView.setFocusable(true); // Ensure the SearchView is focusable
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Do something with the query, if needed
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the list as the user types
                filter(newText);
                return true;
            }
        });

        // Fetch organizer details when the page is initially loaded
        filter("");
    }

    public void fetchfromPHP() {
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
                                String id = jsonObject.optString("bioid");
                                String name = jsonObject.optString("username");
                                String email = jsonObject.optString("email");
                                String status = jsonObject.optString("status");

                                Log.d("tag1", id);
                                Log.d("tag1", name);
                                Log.d("tag1", email);
                                Log.d("tag1", status);

                                dataList.add(new Organizer(id, name, email, status));
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
            for (Organizer item : dataList) {
                if (item.getId() != null && item.getName() != null && item.getEmail() != null && item.getStatus() != null) {
                    if (item.getId().toLowerCase().contains(text)
                            || item.getName().toLowerCase().contains(text)
                            || item.getEmail().toLowerCase().contains(text)
                            || item.getStatus().toLowerCase().contains(text)) {
                        filteredList.add(item);
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        private List<Organizer> dataList;

        public CustomAdapter(List<Organizer> dataList) {
            this.dataList = dataList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.organizer_view, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Organizer organizer = dataList.get(position);
            Log.d("lastTag", organizer.getId());

            if (holder.idTextView != null) {
                holder.idTextView.setText("ID         : " + (organizer.getId() != null ? organizer.getId() : ""));
            }
            if (holder.nameTextView != null) {
                holder.nameTextView.setText("Name  : " + (organizer.getName() != null ? organizer.getName() : ""));
            }
            if (holder.emailTextView != null) {
                holder.emailTextView.setText("Email   : " + (organizer.getEmail() != null ? organizer.getEmail() : ""));
            }
            if (holder.StatusTextView != null) {
                holder.StatusTextView.setText("Status : " + (organizer.getStatus() != null ? organizer.getStatus() : ""));
            }

            // Assume you have a profile ImageView in your card_view layout with the id "profile"
            // Replace "profile" with the actual id of your ImageView


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String selectedItem = organizer.getId() != null ? organizer.getId() : "";
                    Intent intent = new Intent(view_organizer.this, manage_organiser.class);
                    intent.putExtra("bioid", selectedItem);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView idTextView, nameTextView, emailTextView, StatusTextView;


            public ViewHolder(View itemView) {
                super(itemView);
                idTextView = itemView.findViewById(R.id.bioid);
                nameTextView = itemView.findViewById(R.id.oname);
                emailTextView = itemView.findViewById(R.id.oemail);
                StatusTextView = itemView.findViewById(R.id.ostatus);

            }
        }
    }

    private void handleError(VolleyError error) {
        if (error instanceof TimeoutError) {
            Toast.makeText(this, "Request timed out. Check your internet connection.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, error.toString().trim(), Toast.LENGTH_SHORT).show();
        }
    }
}

class Organizer {
    private String id;
    private String name;
    private String email;
    private String status;


    public Organizer(String id, String name, String email, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
    }

    // Add getters for each field
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }


}
