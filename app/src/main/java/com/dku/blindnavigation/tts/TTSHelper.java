package com.dku.blindnavigation.tts;

import android.app.Activity;
import android.speech.tts.TextToSpeech;

import com.dku.blindnavigation.BlindNavigationApplication;

import java.util.Locale;

public class TTSHelper {
    private final TextToSpeech tts;
    private final Activity context;

    public TTSHelper(Activity context) {
        this.context = context;
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS)
                    tts.setLanguage(Locale.KOREAN);
            }
        });
    }

    public void speakString(String string, float pitch) {
        tts.setPitch(pitch);
        tts.setSpeechRate(((BlindNavigationApplication) context.getApplicationContext()).getTtsSpeed());
        tts.speak(string, TextToSpeech.QUEUE_FLUSH, null, string);
    }

    public void stopUsing() {
        tts.stop();
        tts.shutdown();
    }

    @Override
    protected void finalize() throws Throwable {
        stopUsing();
        super.finalize();
    }
}
