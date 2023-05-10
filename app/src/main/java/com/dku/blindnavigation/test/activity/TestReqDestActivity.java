package com.dku.blindnavigation.test.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;

import com.dku.blindnavigation.R;
import com.dku.blindnavigation.navigation.location.destination.DestinationListener;
import com.dku.blindnavigation.navigation.location.LocationUtils;
import com.dku.blindnavigation.navigation.location.destination.DestinationCallback;
import com.dku.blindnavigation.tts.TTSHelper;

import java.lang.ref.WeakReference;

public class TestReqDestActivity extends AppCompatActivity {
    private final Handler handler = new ReqDestHandler(this);
    private TTSHelper ttsHelper;
    private final DestinationCallback destinationCallback = new DestinationCallback();
    private EditText reqDestEditTV;

    private static final class ReqDestHandler extends Handler{
        private final WeakReference<TestReqDestActivity> reference;
        public ReqDestHandler(TestReqDestActivity activity) {
            super(Looper.getMainLooper());
            this.reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            TestReqDestActivity activity = reference.get();
            Bundle msgData = msg.getData();

            if(!msgData.getBoolean("status")) {
                activity.ttsHelper.speakString("목적지를 검색할 수 없습니다", 1.0f, 1.0f);
                return;
            }

            Intent intent = new Intent(activity, TestSelectDestActivity.class);
            intent.putParcelableArrayListExtra("pois", msgData.getParcelableArrayList("pois"));
            activity.startActivity(intent);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_req_dest);

        reqDestEditTV = findViewById(R.id.reqDestEditTV);
        Button reqDestBT = findViewById(R.id.reqDestBT);
        reqDestBT.setOnClickListener(v -> {
            String destinationName = reqDestEditTV.getText().toString();
            LocationUtils.getLocationInfoByName(destinationName, this, destinationCallback);
        });

        destinationCallback.addListener(new DestinationListener(handler));
    }

    @Override
    protected void onResume() {
        super.onResume();
        ttsHelper = new TTSHelper(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ttsHelper.stopUsing();
    }
}