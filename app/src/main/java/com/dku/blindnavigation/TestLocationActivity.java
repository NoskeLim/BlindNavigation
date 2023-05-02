package com.dku.blindnavigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.dku.blindnavigation.navigation.location.LocationUtils;
import com.dku.blindnavigation.navigation.location.destination.DestinationCallback;
import com.dku.blindnavigation.navigation.location.destination.DestinationCallbackListener;
import com.dku.blindnavigation.navigation.location.dto.Poi;
import com.dku.blindnavigation.tts.TTSHelper;

import java.util.List;

public class TestLocationActivity extends AppCompatActivity {
    private static final String TAG = "TestLocationActivity";
    private static final int PERMISSION_CODE = 101;

    private boolean locationPermGranted = false;
    private boolean backgroundPermGranted = false;

    private TTSHelper ttsHelper;

    private final DestinationCallback destinationCallback = new DestinationCallback();

    private TextView destinationInputTV;
    private TextView curDestinationTV;
    private TextView endDestinationTV;
    private List<Poi> endLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_location);

        destinationInputTV = findViewById(R.id.destinationInputTV);
        curDestinationTV = findViewById(R.id.curDestinationTV);
        endDestinationTV = findViewById(R.id.endDestinationTV);

        ttsHelper = new TTSHelper(this);

        locationPermGranted = PermissionUtils.checkLocationPermissions(this);
        if(locationPermGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                backgroundPermGranted = PermissionUtils.checkBackgroundLocationPermissions(this);
        }

        initDestinationTest();
    }

    private void initDestinationTest() {
        Button ttsTestButton = findViewById(R.id.ttsFunctionTest);
        ttsTestButton.setOnClickListener(v -> ttsHelper.speakString("TTS 기능 테스트", 1.0f, 1.0f));

        destinationCallback.addListener(new DestinationListener());
        Button destinationInputBT = findViewById(R.id.destinationInputBT);
        destinationInputBT.setOnClickListener(v ->
                LocationUtils.getDestinationInfo(destinationInputTV.getText().toString(), this, destinationCallback));

        Button yesDestinationBT = findViewById(R.id.yesDestinationBT);
        yesDestinationBT.setOnClickListener(v -> {
            if(!endLocations.isEmpty()) {
                Poi endLocation = endLocations.get(0);
                endDestinationTV.setText(endLocation.getName());
                Log.d(TAG, "endLocation.frontLat = " + endLocation.getFrontLat());
                Log.d(TAG, "endLocation.frontLon = " + endLocation.getFrontLon());
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

            // before ACCESS_BACKGROUND_LOCATION granted, ACCESS_COARSE/FIND_LOCATION must be granted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                backgroundPermGranted = PermissionUtils.checkBackgroundLocationPermissions(this);
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
}