package com.dku.blindnavigation.activity.guide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.dku.blindnavigation.R;
import com.dku.blindnavigation.activity.MainMenuActivity;

public class DestinationArriveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_arrive);

        Button button = findViewById(R.id.arriveDestToMainBT);
        button.setOnClickListener(v -> startActivity(new Intent(this, MainMenuActivity.class)));
    }
}