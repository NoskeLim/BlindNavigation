package com.dku.blindnavigation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.dku.blindnavigation.navigation.location.LocationUtils;
import com.dku.blindnavigation.navigation.location.destination.DestinationCallback;
import com.dku.blindnavigation.navigation.location.destination.DestinationCallbackListener;
import com.dku.blindnavigation.navigation.location.dto.Poi;
import com.dku.blindnavigation.navigation.route.RouteCallback;
import com.dku.blindnavigation.navigation.route.RouteCallbackListener;
import com.dku.blindnavigation.navigation.route.dto.Coordinate;
import com.dku.blindnavigation.tts.TTSHelper;

import java.io.IOException;
import java.util.List;

public class TestLocationActivity extends AppCompatActivity {
    private static final String TAG = "TestLocationActivity";
    private static final int PERMISSION_CODE = 101;

    private boolean locationPermGranted = false;
    private boolean backgroundPermGranted = false;

    private TTSHelper ttsHelper;

    private final DestinationCallback destinationCallback = new DestinationCallback();
    private final RouteCallback routeCallback = new RouteCallback();

    private TextView curCoordTV;
    private TextView curLocationTV;
    private TextView destinationInputTV;
    private TextView curDestinationTV;
    private TextView endDestinationTV;

    private Poi startLocation;
    private Poi endLocation;
    private List<Poi> endLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_location);

        curCoordTV = findViewById(R.id.curCoordTV);
        curLocationTV = findViewById(R.id.curLocationTV);
        destinationInputTV = findViewById(R.id.destinationInputTV);
        curDestinationTV = findViewById(R.id.curDestinationTV);
        endDestinationTV = findViewById(R.id.endDestinationTV);

        ttsHelper = new TTSHelper(this);

        locationPermGranted = PermissionUtils.checkLocationPermissions(this);
        if(locationPermGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                backgroundPermGranted = PermissionUtils.checkBackgroundLocationPermissions(this);
        }

        destinationCallback.addListener(new DestinationListener());
        routeCallback.addListener(new RouteListener());

        initDestinationTest();
        Button getRouteBT = findViewById(R.id.getRouteBT);
        getRouteBT.setOnClickListener(v -> LocationUtils.getRoute(startLocation, endLocation, this, routeCallback));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(locationPermGranted) getDepartureInfo();
    }

    private void onGetDepartureInfo() {
        if(startLocation == null) return;
        curLocationTV.setText(startLocation.getName());
    }

    private void getDepartureInfo() {
        startLocation = LocationUtils.getDepartureCoord(this);
        if(startLocation == null) {
            curLocationTV.setText("출발지 위 경도를 알 수 없음");
            return;
        }

        String curCoordStr = "[" + startLocation.getFrontLat() + ", " + startLocation.getFrontLon() + "]";
        curCoordTV.setText(curCoordStr);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            try {
                String departureName = LocationUtils.getDepartureName(this, startLocation.getFrontLon(), startLocation.getFrontLat());
                startLocation.setName(departureName);
                onGetDepartureInfo();
            } catch (IOException ignored) {}
        }
        else LocationUtils.getDepartureName(this, startLocation.getFrontLat(), startLocation.getFrontLon(), new DepartureListener());
    }

    private void initDestinationTest() {
        Button ttsTestButton = findViewById(R.id.ttsFunctionTest);
        ttsTestButton.setOnClickListener(v -> ttsHelper.speakString("TTS 기능 테스트", 1.0f, 1.0f));

        Button destinationInputBT = findViewById(R.id.destinationInputBT);
        destinationInputBT.setOnClickListener(v ->
                LocationUtils.getDestinationInfo(destinationInputTV.getText().toString(), this, destinationCallback));

        Button yesDestinationBT = findViewById(R.id.yesDestinationBT);
        yesDestinationBT.setOnClickListener(v -> {
            if(!endLocations.isEmpty()) {
                endLocation = endLocations.get(0);
                endDestinationTV.setText(endLocation.getName());
            }
        });

        Button noDestinationBT = findViewById(R.id.noDestinationBT);
        noDestinationBT.setOnClickListener(v -> {
            if(endLocations.size() > 1) {
                endLocations.remove(0);
                curDestinationTV.setText(endLocations.get(0).getName());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != PERMISSION_CODE) return;

        // ACCESS_BACKGROUND_LOCATION
        if(permissions.length == 1) {
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED) return;
            backgroundPermGranted = true;
        }

        // ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION
        else if(permissions.length == 2) {
            for (int grantResult : grantResults)
                if (grantResult != PackageManager.PERMISSION_GRANTED) return;
            locationPermGranted = true;
            getDepartureInfo();

            // before ACCESS_BACKGROUND_LOCATION granted, ACCESS_COARSE/FIND_LOCATION must be granted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                backgroundPermGranted = PermissionUtils.checkBackgroundLocationPermissions(this);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public class DepartureListener implements Geocoder.GeocodeListener {
        @Override
        public void onGeocode(@NonNull List<Address> addresses) {
            if(addresses.isEmpty()) throw new RuntimeException();

            String addressLine = null;
            for (Address location : addresses) {
                addressLine = location.getAddressLine(0);
                if(addressLine != null) break;
            }
            if(addressLine != null) startLocation.setName(addressLine);
            if(startLocation != null) onGetDepartureInfo();
        }

        @Override
        public void onError(@Nullable String errorMessage) {
            Geocoder.GeocodeListener.super.onError(errorMessage);
        }
    }

    public class DestinationListener implements DestinationCallbackListener {
        @Override
        public void onFailureGetDestination() {
        }

        @Override
        public void onSuccessGetDestination(List<Poi> pois) {
            endLocations = pois;
            curDestinationTV.setText(pois.get(0).getName());
        }
    }

    public class RouteListener implements RouteCallbackListener {
        @Override
        public void onFailureRoute() {
        }

        @Override
        public void onSuccessRoute(List<Coordinate> coordinates) {
            if(coordinates.isEmpty()) return;
            for (Coordinate coordinate : coordinates) {
                Log.d(TAG, coordinate.toString());
            }
        }
    }
}