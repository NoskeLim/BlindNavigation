package com.dku.blindnavigation.activity.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.dku.blindnavigation.R;
import com.dku.blindnavigation.activity.MainMenuActivity;

public class UsageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage);

        this.<Button>findViewById(R.id.usageToMainBT).setOnClickListener(v ->
                startActivity(new Intent(this, MainMenuActivity.class)));
    }
}