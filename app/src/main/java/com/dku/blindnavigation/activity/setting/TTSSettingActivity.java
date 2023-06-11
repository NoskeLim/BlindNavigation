package com.dku.blindnavigation.activity.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.dku.blindnavigation.BlindNavigationApplication;
import com.dku.blindnavigation.R;
import com.dku.blindnavigation.activity.MainMenuActivity;
import com.dku.blindnavigation.tts.TTSHelper;

public class TTSSettingActivity extends AppCompatActivity {
    private TTSHelper ttsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ttssetting);

        this.<Button>findViewById(R.id.testTTSBT).setOnClickListener(v -> ttsHelper.speakString("TTS 테스트", 1));

        Button button = findViewById(R.id.saveTTSBT);
        button.setOnClickListener(v -> {
            float ttsSpeed = 1.0f;
            ((BlindNavigationApplication) getApplication()).updateTtsSpeed(ttsSpeed);
        });

        this.<Button>findViewById(R.id.ttsSpeedToMainBT).setOnClickListener(v ->
                startActivity(new Intent(this, MainMenuActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        ttsHelper = new TTSHelper(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ttsHelper.stopUsing();
    }
}