package com.dku.blindnavigation.test.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.dku.blindnavigation.MainActivity;
import com.dku.blindnavigation.navigation.direction.DirectionCalculator;
import com.dku.blindnavigation.navigation.direction.DirectionType;
import com.dku.blindnavigation.navigation.dto.Poi;
import com.dku.blindnavigation.navigation.location.LocationUtils;
import com.dku.blindnavigation.navigation.location.gps.CurLocationCoordProvider;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Queue;

public class TestNavigationService extends Service {
    private boolean isDebug = true;
    private static final String TAG = "TestNavigationService";
    public static final String CHANNEL_ID = "BlindNavigationChannel";
    private static final int REACH_DISTANCE = 20;
    private Poi prevLocation;
    private Poi curLocation;
    private Queue<Poi> routes;

    private CurLocationCoordProvider curLocationCoordProvider;
    private final Handler handler = new TestNavigationService.EventHandler(this);

    public TestNavigationService() {
    }

    private static final class EventHandler extends Handler {
        private final WeakReference<TestNavigationService> reference;

        public EventHandler(TestNavigationService service) {
            super(Looper.getMainLooper());
            this.reference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            TestNavigationService service = reference.get();
            Bundle msgData = msg.getData();
            boolean status = msgData.getBoolean("status");

            if (status) {
                Poi gpsCurLocation = msgData.getParcelable("curLocationCoord");
                boolean reachNextLocation = service.checkReachNextLocation(gpsCurLocation);
                if(!reachNextLocation) return;

                service.updateLocation();
                Poi nextLocation = service.getNextLocation(gpsCurLocation);

                assert nextLocation != null;
                DirectionType nextDirection =
                        DirectionCalculator.getNextDirection(service.prevLocation, service.curLocation, nextLocation);

                if(service.isDebug) {
                    Log.d(TAG, String.valueOf(nextDirection));
                    String nextLocationString = "[" + nextLocation.getFrontLat() + ", " + nextLocation.getFrontLon() + "]";
                    Log.d(TAG, nextLocationString);
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand called");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createNotificationChannel();
        startForeground(1, createNotification());

        curLocation = intent.getParcelableExtra("departureLocation");
        routes = new LinkedList<>(intent.getParcelableArrayListExtra("route"));
        double startPhoneDegree = intent.getDoubleExtra("degree", 0.0);

        Poi nextLocation = getNextLocation(curLocation);
        if (nextLocation == null) {
            Log.d(TAG, "arrive destination");
            stopForeground(true);
            stopSelf();
        }

        assert nextLocation != null;
        DirectionType nextDirection =
                DirectionCalculator.getFirstDirection(curLocation, nextLocation, startPhoneDegree);

        curLocationCoordProvider = new CurLocationCoordProvider(this, handler);
        curLocationCoordProvider.startRequestLocation();

        if(isDebug) {
            String nextLocationString = "[" + nextLocation.getFrontLat() + ", " + nextLocation.getFrontLon() + "]";
            Log.d(TAG, nextLocationString);
            Log.d(TAG, String.valueOf(nextDirection));
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy called");
        curLocationCoordProvider.stopRequestLocation();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void updateLocation() {
        prevLocation = curLocation;
        curLocation = routes.peek();
    }

    private boolean checkReachNextLocation(Poi curLocation) {
        Poi nextLocationCoord = routes.peek();
        if (nextLocationCoord == null) {
            Log.d(TAG, "arrive destination");
            stopForeground(true);
            stopSelf();
        }

        double distance = LocationUtils.getDistance(curLocation.getFrontLat(), curLocation.getFrontLon(),
                nextLocationCoord.getFrontLat(), nextLocationCoord.getFrontLon());
        return distance <= REACH_DISTANCE;
    }

    @Nullable
    private Poi getNextLocation(Poi curLocation) {
        while (checkReachNextLocation(curLocation)) routes.poll();
        return routes.peek();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_HIGH
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        assert manager != null;
        manager.createNotificationChannel(serviceChannel);
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("시각 장애인용 네비게이션")
                .setContentText("경로 알림 중 입니다")
                .setContentIntent(pendingIntent)
                .build();
    }
}