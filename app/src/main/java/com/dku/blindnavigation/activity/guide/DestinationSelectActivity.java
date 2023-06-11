package com.dku.blindnavigation.activity.guide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;

import com.dku.blindnavigation.R;
import com.dku.blindnavigation.navigation.direction.OrientationListener;
import com.dku.blindnavigation.navigation.dto.Poi;
import com.dku.blindnavigation.navigation.location.LocationUtils;
import com.dku.blindnavigation.navigation.location.destination.DestinationCallback;
import com.dku.blindnavigation.navigation.location.destination.DestinationListener;
import com.dku.blindnavigation.navigation.location.gps.CurLocationCoordProvider;
import com.dku.blindnavigation.navigation.location.gps.LocationNameProvider;
import com.dku.blindnavigation.navigation.route.RouteCallback;
import com.dku.blindnavigation.navigation.route.RouteListener;
import com.dku.blindnavigation.tts.TTSHelper;

import java.lang.ref.WeakReference;
import java.util.List;

public class DestinationSelectActivity extends AppCompatActivity {
    private final Handler handler = new DestinationSelectHandler(this);
    private EditText inputDestinationEditTV;
    private TTSHelper ttsHelper;
    private final DestinationCallback destinationCallback = new DestinationCallback();
    private final RouteCallback routeCallback = new RouteCallback();

    private OrientationListener orientationListener;
    private CurLocationCoordProvider curLocationCoordProvider;
    private LocationNameProvider locationNameProvider;

    private Poi departureLocation;
    private List<Poi> destinationLocations;
    private final Bundle bundle = new Bundle();

    private static final class DestinationSelectHandler extends Handler {
        private final WeakReference<DestinationSelectActivity> reference;

        public DestinationSelectHandler(DestinationSelectActivity activity) {
            super(Looper.getMainLooper());
            this.reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            DestinationSelectActivity activity = reference.get();
            Bundle msgData = msg.getData();
            int eventType = msgData.getInt("eventType");

            if (eventType == OrientationListener.EVENT_TYPE) {
                activity.bundle.putDouble("degree", msgData.getDouble("degree"));
                return;
            }

            boolean status = msgData.getBoolean("status");
            switch (eventType) {
                case DestinationListener.EVENT_TYPE:
                    handleDestinationPois(activity, msgData, status);

                case CurLocationCoordProvider.EVENT_TYPE: //Departure Coordinate
                    handleDepartureCoord(activity, msgData, status);
                    break;

                case LocationNameProvider.EVENT_TYPE: //Departure Name
                    handleDepartureName(activity, msgData, status);
                    break;

                case RouteListener.EVENT_TYPE: //Route
                    handleRoute(activity, msgData, status);
                    break;
            }
        }

        private static void handleDestinationPois(DestinationSelectActivity activity, Bundle msgData, boolean status) {
            if (!status) {
                activity.ttsHelper.speakString("목적지를 검색할 수 없습니다", 1);
                return;
            }
            activity.destinationLocations = msgData.getParcelableArrayList("pois");
            activity.ttsHelper.speakString(activity.destinationLocations.get(0).getName(), 1);
        }

        private static void handleDepartureCoord(DestinationSelectActivity activity, Bundle msgData, boolean status) {
            if (!status) {
                activity.ttsHelper.speakString("현재 위치 청보를 가져올 수 없습니다", 1);
                return;
            }
            Poi curLocationCoord = msgData.getParcelable("curLocationCoord");
            if(curLocationCoord == null) return;
            activity.departureLocation = curLocationCoord;
            activity.bundle.putParcelable("departureLocation", activity.departureLocation);
            activity.curLocationCoordProvider.stopRequestLocation();
            activity.locationNameProvider.getDepartureInfo(
                    activity, activity.departureLocation.getFrontLat(), activity.departureLocation.getFrontLon());
        }

        private static void handleDepartureName(DestinationSelectActivity activity, Bundle msgData, boolean status) {
            if (!status) {
                activity.ttsHelper.speakString("현재 주변의 건물의 이름을 가져올 수 없습니다", 1);
                return;
            }
            activity.departureLocation.setName(msgData.getString("departureName"));
        }

        private static void handleRoute(DestinationSelectActivity activity, Bundle msgData, boolean status) {
            if (!status) {
                activity.ttsHelper.speakString("목적지 까지의 경로를 가져올 수 없습니다", 1);
                return;
            }
            activity.bundle.putParcelableArrayList("route", msgData.getParcelableArrayList("route"));
            Intent intent = new Intent(activity, NavigationActivity.class);
            intent.putExtras(activity.bundle);
            activity.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_select);
        inputDestinationEditTV = findViewById(R.id.inputDestinationEditTV);

        initListeners();
        initButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ttsHelper = new TTSHelper(this);
        orientationListener.registerSensorListeners();
        curLocationCoordProvider.startRequestLocation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ttsHelper.stopUsing();
        orientationListener.unregisterSensorListeners();
        curLocationCoordProvider.stopRequestLocation();
    }

    private void initListeners() {
        destinationCallback.addListener(new DestinationListener(handler));
        orientationListener = new OrientationListener((SensorManager) getSystemService(SENSOR_SERVICE), handler);
        curLocationCoordProvider = new CurLocationCoordProvider(this, handler);
        locationNameProvider = new LocationNameProvider(handler);
        routeCallback.addListener(new RouteListener(handler));
    }

    private void initButtons() {
        this.<Button>findViewById(R.id.findDestinationBT).setOnClickListener(v ->
                LocationUtils.getLocationInfoByName(inputDestinationEditTV.getText().toString(),
                        this, destinationCallback)
        );

        this.<Button>findViewById(R.id.selectDestinationBT).setOnClickListener(v -> {
            if (departureLocation == null) {
                ttsHelper.speakString("출발지 정보를 가져오지 못했습니다", 1f);
                return;
            }
            Poi destinationLocation = destinationLocations.get(0);
            LocationUtils.getRoute(departureLocation, destinationLocation, this, routeCallback);
        });

        this.<Button>findViewById(R.id.nextDestinationBT).setOnClickListener(v -> {
            if (destinationLocations == null || destinationLocations.isEmpty()) {
                ttsHelper.speakString("일치하는 목적지가 없습니다", 1);
                return;
            }
            destinationLocations.remove(0);
            ttsHelper.speakString(destinationLocations.get(0).getName(), 1);
        });

        this.<Button>findViewById(R.id.selectDestToMainBT).setOnClickListener(v -> finish());
    }
}