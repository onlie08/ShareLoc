package com.ch.fishinglocation.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.ch.fishinglocation.R;

// LocationSelectionActivity.java
public class LocationSelectionActivity extends AppCompatActivity {
    private Button buttonDone;

    // You would also define members to hold selected location, parkingSpots, and walkPaths here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_selection);

        buttonDone = findViewById(R.id.buttonDone);

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Construct the result to send back
                Intent resultIntent = new Intent();
                // Add location, parkingSpots, and walkPaths to the resultIntent
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        // Other code for initializing map and UI elements
    }
}

