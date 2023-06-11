package com.dku.blindnavigation;

import android.app.Application;
import android.content.SharedPreferences;

public class BlindNavigationApplication extends Application {
    private final String TTS_SPEED = "ttsSpeed";
    private float ttsSpeed;

    public float getTtsSpeed() {
        return ttsSpeed;
    }

    private void setTtsSpeed(float ttsSpeed) {
        this.ttsSpeed = ttsSpeed;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        loadTtsSpeed();
    }

    public void loadTtsSpeed() {
        SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
        setTtsSpeed(setting.getFloat(TTS_SPEED, 1));
    }

    public void updateTtsSpeed(float speed) {
        SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);

        SharedPreferences.Editor edit = setting.edit();
        edit.putFloat(TTS_SPEED, speed);
        edit.apply();

        loadTtsSpeed();
    }
}