package com.example.venuemanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class types_of_login extends AppCompatActivity {
    Button admin, org, user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_types_of_login);
        // Put this code in the onCreate method of your activity
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        admin = findViewById(R.id.admin);
        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(types_of_login.this, login_page.class);
                startActivity(intent);
            }
        });
        org = findViewById(R.id.organizer);
        org.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(types_of_login.this, org_login.class);
                startActivity(intent);
            }
        });
        user = findViewById(R.id.user);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(types_of_login.this, user_login.class);
                startActivity(intent);
            }
        });

    }
}