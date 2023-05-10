package com.dku.blindnavigation.tts;

import android.app.Activity;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class TTSHelper {
    private final TextToSpeech tts;

    public TTSHelper(Activity context) {
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS)
                    tts.setLanguage(Locale.KOREAN);
            }
        });
    }

    public void speakString(String string, float pitch, float speechRate) {
        tts.setPitch(pitch);
        tts.setSpeechRate(speechRate);
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
