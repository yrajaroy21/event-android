package com.example.venuemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button getStartedButton = findViewById(R.id.getStarted);
        getStartedButton.setOnClickListener(v -> {
            // Handle button click event
            Intent intent = new Intent(MainActivity.this,types_of_login.class);
            startActivity(intent);
            Toast.makeText(MainActivity.this, "Get Started button clicked", Toast.LENGTH_SHORT).show();
        });
    }
}
