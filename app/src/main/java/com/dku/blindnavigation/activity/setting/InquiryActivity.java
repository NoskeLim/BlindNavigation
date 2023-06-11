package com.dku.blindnavigation.activity.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.dku.blindnavigation.R;
import com.dku.blindnavigation.activity.MainMenuActivity;
import com.dku.blindnavigation.tts.TTSHelper;

public class InquiryActivity extends AppCompatActivity {
    private TTSHelper ttsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry);

        this.<Button>findViewById(R.id.inquiryToMainBT).setOnClickListener(v ->
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