package com.example.venuemanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

public class testing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);
        TextView textView = findViewById(R.id.tryanimate);

        // Set up the ObjectAnimator for left-to-right animation
        ObjectAnimator animator = ObjectAnimator.ofFloat(textView, "translationX", -1000f, 0f);
        animator.setDuration(5000);  // Set the animation duration in milliseconds
        animator.setInterpolator(new AccelerateDecelerateInterpolator()); // Optional: Set an interpolator for acceleration and deceleration

        // Start the animation
        animator.start();
    }
}