package com.dku.blindnavigation.activity.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.dku.blindnavigation.R;
import com.dku.blindnavigation.activity.MainMenuActivity;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        this.<Button>findViewById(R.id.toTtsSpeedBT)
                .setOnClickListener(v -> startActivity(new Intent(this, TTSSettingActivity.class)));
        this.<Button>findViewById(R.id.toUsageBT)
                .setOnClickListener(v -> startActivity(new Intent(this, UsageActivity.class)));
        this.<Button>findViewById(R.id.toInquiryBT)
                .setOnClickListener(v -> startActivity(new Intent(this, InquiryActivity.class)));
        this.<Button>findViewById(R.id.settingToMainBT)
                .setOnClickListener(v -> startActivity(new Intent(this, MainMenuActivity.class)));
    }
}