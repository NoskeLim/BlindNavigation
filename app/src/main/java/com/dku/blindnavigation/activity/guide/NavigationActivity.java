package com.dku.blindnavigation.activity.guide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;

import com.dku.blindnavigation.R;
import com.dku.blindnavigation.navigation.dto.Poi;
import com.dku.blindnavigation.navigation.location.gps.CurLocationCoordProvider;
import com.dku.blindnavigation.navigation.location.gps.LocationNameProvider;
import com.dku.blindnavigation.service.NavigationService;
import com.dku.blindnavigation.tts.TTSHelper;

import java.lang.ref.WeakReference;

public class NavigationActivity extends AppCompatActivity {
    private final Handler handler = new EventHandler(this);
    private TTSHelper ttsHelper;
    private CurLocationCoordProvider curLocationCoordProvider;
    private LocationNameProvider locationNameProvider;
    private Poi curLocation;

    private static final class EventHandler extends Handler {
        private final WeakReference<NavigationActivity> reference;

        public EventHandler(NavigationActivity activity) {
            super(Looper.getMainLooper());
            this.reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            NavigationActivity activity = reference.get();
            Bundle msgData = msg.getData();
            int eventType = msgData.getInt("eventType");
            if (!msgData.getBoolean("status")) return;

            switch(eventType) {
                case LocationNameProvider.EVENT_TYPE:
                    String curLocationName = msgData.getString("departureName");
                    Log.d("NavigationActivity", curLocationName);
                    if (curLocationName != null) activity.ttsHelper.speakString(curLocationName, 1);
                    break;
                case CurLocationCoordProvider.EVENT_TYPE:
                    activity.curLocation = msgData.getParcelable("curLocationCoord");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        ttsHelper = new TTSHelper(this);
        curLocationCoordProvider = new CurLocationCoordProvider(this, handler);
        locationNameProvider = new LocationNameProvider(handler);

        this.<Button>findViewById(R.id.nearLocationNameBT).setOnClickListener(v ->
            locationNameProvider.getDepartureInfo(this, curLocation.getFrontLat(), curLocation.getFrontLon()));

        this.<Button>findViewById(R.id.stopGuideBT).setOnClickListener(v -> stopNavigate());

        startNavigate();
    }

    private void startNavigate() {
        Intent intent = new Intent(this, NavigationService.class);
        intent.putExtras(getIntent().getExtras());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private void stopNavigate() {
        stopService(new Intent(this, NavigationService.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        curLocationCoordProvider.startRequestLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        curLocationCoordProvider.stopRequestLocation();
    }

    @Override
    public void onBackPressed() {
        stopNavigate();
    }
}