package com.dku.blindnavigation.navigation.direction;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

public class OrientationListener  implements SensorEventListener {
    public static final int EVENT_TYPE = 400;
    private final Handler handler;
    private final SensorManager sensorManager;
    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];

    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];
    private boolean accelerometerFinish = false;
    private boolean magnetometerFinish = false;
    private double degree = -1;


    public OrientationListener(SensorManager sensorManager, Handler handler) {
        this.handler = handler;
        this.sensorManager = sensorManager;
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
            if(degree != 0.0) sendDegreeToHandler();
        }
    }

    public void registerSensorListeners() {
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

    public void unregisterSensorListeners() {
        sensorManager.unregisterListener(this);
    }

    private void sendDegreeToHandler() {
        Bundle bundle = generateBundleByDegree();
        Message message = new Message();
        message.setData(bundle);
        handler.sendMessage(message);
    }

    @NonNull
    private Bundle generateBundleByDegree() {
        Bundle bundle = new Bundle();
        bundle.putInt("eventType", EVENT_TYPE);
        bundle.putDouble("degree", degree);
        return bundle;
    }
}
