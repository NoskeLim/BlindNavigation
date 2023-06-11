package com.dku.blindnavigation.test.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Button;
import android.widget.TextView;

import com.dku.blindnavigation.R;
import com.dku.blindnavigation.navigation.direction.OrientationListener;
import com.dku.blindnavigation.navigation.location.LocationUtils;
import com.dku.blindnavigation.navigation.location.gps.CurLocationCoordProvider;
import com.dku.blindnavigation.navigation.location.gps.LocationNameProvider;
import com.dku.blindnavigation.navigation.dto.Poi;
import com.dku.blindnavigation.navigation.route.RouteCallback;
import com.dku.blindnavigation.navigation.route.RouteListener;
import com.dku.blindnavigation.test.service.TestNavigationService;
import com.dku.blindnavigation.tts.TTSHelper;

import java.lang.ref.WeakReference;
import java.util.List;

public class TestSelectDestActivity extends AppCompatActivity{
    private final EventHandler handler = new EventHandler(this);
    private Poi departureLocation;
    private List<Poi> destinationLocations;
    private OrientationListener orientationListener;
    private CurLocationCoordProvider curLocationCoordProvider;
    private LocationNameProvider locationNameProvider;
    private final RouteCallback routeCallback = new RouteCallback();
    private TextView destNameTV;
    private TextView curLocNameTV;
    private TTSHelper ttsHelper;
    private final Bundle bundle = new Bundle();
    private boolean finishGetDeparture = false;

    private static final class EventHandler extends Handler {
        private final WeakReference<TestSelectDestActivity> reference;
        public EventHandler(TestSelectDestActivity activity) {
            super(Looper.getMainLooper());
            this.reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            TestSelectDestActivity activity = reference.get();
            Bundle msgData = msg.getData();
            int eventType = msgData.getInt("eventType");

            if(eventType == OrientationListener.EVENT_TYPE) {
                activity.bundle.putDouble("degree", msgData.getDouble("degree"));
                return;
            }

            boolean status = msgData.getBoolean("status");
            switch (eventType) {
                case CurLocationCoordProvider.EVENT_TYPE: //Departure Coordinate
                    handleDestinationCoord(activity, msgData, status);
                    break;

                case LocationNameProvider.EVENT_TYPE: //Departure Name
                    handleDestinationName(activity, msgData, status);
                    break;

                case RouteListener.EVENT_TYPE: //Route
                    handleRoute(activity, msgData, status);
                    break;
            }
        }

        private static void handleDestinationCoord(TestSelectDestActivity activity, Bundle msgData, boolean status) {
            if(!status) {
                activity.ttsHelper.speakString("현재 위치 청보를 가져올 수 없습니다", 1);
                return;
            }
            activity.departureLocation = msgData.getParcelable("curLocationCoord");
            activity.bundle.putParcelable("departureLocation", activity.departureLocation);
            activity.locationNameProvider.getDepartureInfo(
                    activity, activity.departureLocation.getFrontLat(), activity.departureLocation.getFrontLon());
        }

        private static void handleDestinationName(TestSelectDestActivity activity, Bundle msgData, boolean status) {
            if(!status) {
                activity.ttsHelper.speakString("현재 주변의 건물의 이름을 가져올 수 없습니다", 1);
                activity.curLocNameTV.setText("현재 주변의 건물의 이름을 가져올 수 없습니다");
                return;
            }
            activity.departureLocation.setName(msgData.getString("departureName"));
            activity.curLocNameTV.setText(msgData.getString("departureName"));
            activity.finishGetDeparture = true;
        }

        private static void handleRoute(TestSelectDestActivity activity, Bundle msgData, boolean status) {
            if(!status) {
                activity.ttsHelper.speakString("목적지 까지의 경로를 가져올 수 없습니다", 1);
                return;
            }
            activity.bundle.putParcelableArrayList("route", msgData.getParcelableArrayList("route"));
//            Intent intent = new Intent(activity, TestNavigationActivity.class);
            Intent intent = new Intent(activity, TestNavigationService.class);
            intent.putExtras(activity.bundle);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity.startForegroundService(intent);
            }
            else {
                activity.startService(intent);
            }
//            activity.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_select_dest);
        destinationLocations = getIntent().getParcelableArrayListExtra("pois");

        orientationListener = new OrientationListener((SensorManager) getSystemService(SENSOR_SERVICE), handler);
        curLocationCoordProvider = new CurLocationCoordProvider(this, handler);
        locationNameProvider = new LocationNameProvider(handler);

        routeCallback.addListener(new RouteListener(handler));

        curLocNameTV = findViewById(R.id.curLocNameTV);
        destNameTV = findViewById(R.id.destNameTV);
        destNameTV.setText(destinationLocations.get(0).getName());

        Button destYesBT = findViewById(R.id.destYesBt);
        destYesBT.setOnClickListener(v -> {
            if(!finishGetDeparture) {
                ttsHelper.speakString("출발지 정보를 가져오지 못했습니다", 1);
                return;
            }
            Poi destinationLocation = destinationLocations.get(0);
            LocationUtils.getRoute(departureLocation, destinationLocation, this, routeCallback);
        });

        Button destNoBt = findViewById(R.id.destNoBT);
        destNoBt.setOnClickListener(v -> {
            if(destinationLocations.isEmpty()) return;
            destinationLocations.remove(0);
            destNameTV.setText(destinationLocations.get(0).getName());
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ttsHelper = new TTSHelper(this);
        orientationListener.registerSensorListeners();
        curLocationCoordProvider.startRequestLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ttsHelper.stopUsing();
        orientationListener.unregisterSensorListeners();
        curLocationCoordProvider.stopRequestLocation();
    }
}