package com.dku.blindnavigation;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button locationTestButton = findViewById(R.id.locationTestButton);
        locationTestButton.setOnClickListener(v -> startActivity(new Intent(this, TestLocationActivity.class)));
    }
}