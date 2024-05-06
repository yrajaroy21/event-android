package com.example.venuemanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.SearchView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class view_event_registration extends AppCompatActivity {

    private Spinner selectEventsSpinner;
    ImageView img;
    private List<RegistrationModel> registrationList;
    private List<RegistrationModel> filteredList;
    RecyclerView recyclerView;
    private RegistrationAdapter adapter;
    SearchView searchView;
    String username,sid;
    TextView toptext;
    BottomNavigationView bottomNavigationView;
    ImageView log;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event_registration);
        img = findViewById(R.id.serach_registr);
        recyclerView = findViewById(R.id.recyclerRegistrations);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        sid=intent.getStringExtra("sid");
        toptext = findViewById(R.id.orgText);
        toptext.setText(username);
        log = findViewById(R.id.clickLogout);
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view_event_registration.this, types_of_login.class);
                startActivity(intent);
            }
        });
        registrationList = new ArrayList<>();
        filteredList = new ArrayList<>(registrationList);
        selectEventsSpinner = findViewById(R.id.select_events);
        registrationList = new ArrayList<>();
        adapter = new RegistrationAdapter(filteredList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new FetchEventData().execute();
        searchView = findViewById(R.id.search_registrations);
        searchView.setFocusable(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrationList.clear();
                String selectedTitle = selectEventsSpinner.getSelectedItem().toString();
                new RegisterEventTask().execute(selectedTitle);
            }
        });
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


        filter("");
    }

    private class RegisterEventTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String title = params[0];
            String urlStr = "http://10.0.2.2/venue_m/view_registrations.php";
            String postData = "action=register&title=" + title;
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
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String status = jsonResponse.getString("status");
                    if (status.equals("success")) {
                        JSONArray resArray = jsonResponse.getJSONArray("register_data");
                        for (int i = 0; i < resArray.length(); i++) {
                            JSONObject resObject = resArray.getJSONObject(i);
                            String student = resObject.getString("student");
                            String username = resObject.getString("username");
                            String email = resObject.getString("email");
                            String phone = resObject.getString("phone");
                            registrationList.add(new RegistrationModel(student, username, email, phone));
                        }
                        adapter.notifyDataSetChanged();
                        filter("");
                    } else if (status.equals("error")) {
                        String message = jsonResponse.getString("message");
                        if (message.equals("No registrations found for the specified event")) {
                            registrationList.clear();
                            filteredList.clear();
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.e("Response", "Status: " + status);
                        }
                    }
                } catch (JSONException e) {
                    Log.e("JSON", "Error parsing JSON response: " + e.getMessage());
                }
            } else {
                Log.e("Response", "Null response received");
            }
        }
    }

    private void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(registrationList);
        } else {
            text = text.toLowerCase().trim();
            for (RegistrationModel item : registrationList) {
                if (item.getUsername() != null && item.getEmail() != null && item.getPhone() != null) {
                    if (item.getUsername().toLowerCase().contains(text)
                            || item.getEmail().toLowerCase().contains(text)
                            || item.getPhone().toLowerCase().contains(text)) {
                        filteredList.add(item);
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private class FetchEventData extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String urlStr = "http://10.0.2.2/venue_m/view_registrations.php";
            String postData = "action=fetch&organized_by=staff1";
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
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray titlesArray = jsonResponse.getJSONArray("titles");
                    List<String> titlesList = new ArrayList<>();
                    for (int i = 0; i < titlesArray.length(); i++) {
                        titlesList.add(titlesArray.getString(i));
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(view_event_registration.this, android.R.layout.simple_spinner_item, titlesList);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    selectEventsSpinner.setAdapter(spinnerAdapter);
                } catch (JSONException e) {
                    Log.e("JSON", "Error parsing JSON response: " + e.getMessage());
                }
            } else {
                Log.e("Response", "Null response received");
            }
        }
    }

    class RegistrationAdapter extends RecyclerView.Adapter<RegistrationAdapter.ViewHolder> {

        private List<RegistrationModel> registrationList;
        private List<RegistrationModel> filteredRegistrationList;

        public RegistrationAdapter(List<RegistrationModel> registrationList) {
            this.registrationList = registrationList;

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.registrations_org, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RegistrationModel registration = registrationList.get(position);
            holder.nameTextView.setText("Name: " + registration.getUsername());
            holder.emailTextView.setText("Email: " + registration.getEmail());
            holder.contactTextView.setText("Contact: " + registration.getPhone());

        }

        @Override
        public int getItemCount() {
            return registrationList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView nameTextView;
            private TextView emailTextView;
            private TextView contactTextView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.fetch_register_name);
                emailTextView = itemView.findViewById(R.id.fetch_register_email);
                contactTextView = itemView.findViewById(R.id.fetch_register_contact);
            }
        }


//        public void filter(String constraint) {
//            List<RegistrationModel> filteredList = new ArrayList<>();
//            if (TextUtils.isEmpty(constraint)) {
//                filteredList.addAll(filteredRegistrationList);
//            } else {
//                String filterPattern = constraint.toLowerCase().trim();
//                for (RegistrationModel item : filteredRegistrationList) {
//                    if (item.getUsername().toLowerCase().contains(filterPattern) ||
//                            item.getEmail().toLowerCase().contains(filterPattern) ||
//                            item.getPhone().toLowerCase().contains(filterPattern)) {
//                        filteredList.add(item);
//                    }
//                }
//            }
//            registrationList.clear();
//            registrationList.addAll(filteredList);
//            notifyDataSetChanged();
//        }
    }

    class RegistrationModel {
        private String student;
        private String username;
        private String email;
        private String phone;

        public RegistrationModel(String student, String username, String email, String phone) {
            this.student = student;
            this.username = username;
            this.email = email;
            this.phone = phone;
        }

        public String getStudent() {
            return student;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }
    }
}
