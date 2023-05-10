package com.dku.blindnavigation;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.dku.blindnavigation.test.activity.TestDirectionActivity;
import com.dku.blindnavigation.test.activity.TestLocationActivity;
import com.dku.blindnavigation.test.activity.TestReqDestActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button locationTestButton = findViewById(R.id.locationTestButton);
        locationTestButton.setOnClickListener(v -> startActivity(new Intent(this, TestLocationActivity.class)));

        Button degreeTestButton = findViewById(R.id.degreeTestBT);
        degreeTestButton.setOnClickListener(v -> startActivity(new Intent(this, TestDirectionActivity.class)));

        Button naviTestBT = findViewById(R.id.navigationTestBT);
        naviTestBT.setOnClickListener(v -> startActivity(new Intent(this, TestReqDestActivity.class)));
    }
}