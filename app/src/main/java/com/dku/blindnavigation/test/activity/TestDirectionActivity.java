package com.dku.blindnavigation.test.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dku.blindnavigation.R;
import com.dku.blindnavigation.navigation.direction.DirectionCalculator;
import com.dku.blindnavigation.navigation.direction.DirectionType;
import com.dku.blindnavigation.navigation.location.dto.Poi;

public class TestDirectionActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];

    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];
    private boolean accelerometerFinish = false;
    private boolean magnetometerFinish = false;
    private double degree = 0.0;
    private TextView degreeTV;
    private TextView directionTV;
    private EditText startLatTV;
    private EditText startLngTV;
    private EditText endLatTV;
    private EditText endLngTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_direction);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        degreeTV = findViewById(R.id.degreeTV);
        directionTV = findViewById(R.id.directionTV);
        startLatTV = findViewById(R.id.startLatEditTV);
        startLngTV = findViewById(R.id.startLngEditTV);
        endLatTV = findViewById(R.id.endLatEditTV);
        endLngTV = findViewById(R.id.endLngEditTV);

        Button calcDegreeButton = findViewById(R.id.calcDegreeBT);
        calcDegreeButton.setOnClickListener(v -> {
            DirectionType curDirection = DirectionCalculator.getFirstDirection(
                    new Poi(Double.parseDouble(startLatTV.getText().toString()), Double.parseDouble(startLngTV.getText().toString())),
                    new Poi(Double.parseDouble(endLatTV.getText().toString()), Double.parseDouble(endLngTV.getText().toString())),
                    degree);
            directionTV.setText(curDirection.toString());
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
            accelerometerFinish = true;
        }
        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticField != null) {
            sensorManager.registerListener(this, magneticField,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);

            magnetometerFinish = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.length);
        }

        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.length);
        }

        updateOrientationAngles();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void updateOrientationAngles() {
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading);
        SensorManager.getOrientation(rotationMatrix, orientationAngles);

        if(accelerometerFinish && magnetometerFinish) {
            degree = (Math.toDegrees(orientationAngles[0]) + 360) % 360;
            degreeTV.setText(String.valueOf(degree));
        }
    }
}