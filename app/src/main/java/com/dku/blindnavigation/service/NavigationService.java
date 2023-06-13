package com.dku.blindnavigation.service;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.dku.blindnavigation.activity.guide.DestinationArriveActivity;
import com.dku.blindnavigation.activity.guide.NavigationActivity;
import com.dku.blindnavigation.bluetooth.BluetoothHelper;
import com.dku.blindnavigation.navigation.direction.DirectionCalculator;
import com.dku.blindnavigation.navigation.direction.DirectionType;
import com.dku.blindnavigation.navigation.dto.Poi;
import com.dku.blindnavigation.navigation.location.LocationUtils;
import com.dku.blindnavigation.navigation.location.gps.CurLocationCoordProvider;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Queue;

public class NavigationService extends Service {
    private static final String TAG = "NavigationService";
    public static final String CHANNEL_ID = "BlindNavigationChannel";
    private static final int REACH_DISTANCE = 20;
    private Poi prevLocation;
    private Poi curLocation;
    private Queue<Poi> routes;
    private CurLocationCoordProvider curLocationCoordProvider;
    private BluetoothHelper btHelper;
    private final Handler handler = new NavigationService.EventHandler(this);

    private static final class EventHandler extends Handler {
        private final WeakReference<NavigationService> reference;

        public EventHandler(NavigationService service) {
            super(Looper.getMainLooper());
            this.reference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            NavigationService service = reference.get();
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
                service.btHelper.sendDirectionToDevice(nextDirection);
                Toast.makeText(service, nextDirection.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createNotificationChannel();
        startForeground(1, createNotification());
        btHelper = new BluetoothHelper(this);
        if(!btHelper.connectBluetoothDevice()) {
            throw new RuntimeException();
        }

        curLocation = intent.getParcelableExtra("departureLocation");
        routes = new LinkedList<>(intent.getParcelableArrayListExtra("route"));
        initTestRoutes();

        double startPhoneDegree = intent.getDoubleExtra("degree", 0.0);

        Poi nextLocation = getNextLocation(curLocation);
        if (nextLocation == null) {
            Log.d(TAG, "arrive destination");
            arriveDestination();
        }

        assert nextLocation != null;
        DirectionType nextDirection =
                DirectionCalculator.getFirstDirection(curLocation, nextLocation, startPhoneDegree);
        btHelper.sendDirectionToDevice(nextDirection);
        Toast.makeText(this, nextDirection.toString(), Toast.LENGTH_LONG).show();

        curLocationCoordProvider = new CurLocationCoordProvider(this, handler);
        curLocationCoordProvider.startRequestLocation();

        return START_NOT_STICKY;
    }

    private void initTestRoutes() {
        routes.clear();
        Poi[] pois = {
                new Poi(37.320784, 127.126376),
                new Poi(37.320614, 127.126157),
                new Poi(37.320802, 127.125933),
                new Poi(37.321085, 127.126281),
                new Poi(37.321077, 127.126449),
                new Poi(37.320937, 127.126468),
                new Poi(37.320831, 127.126328),

                new Poi(37.320784, 127.126376),
                new Poi(37.320831, 127.126328),
                new Poi(37.320937, 127.126468),
                new Poi(37.321077, 127.126449),
                new Poi(37.321085, 127.126281),
                new Poi(37.320802, 127.125933),
                new Poi(37.320614, 127.126157),
                new Poi(37.320784, 127.126376),
        };
        for (Poi poi : pois) routes.offer(poi);
    }

    @Override
    public void onDestroy() {
        Log.d("NavigationService", "onDestroy called");
        curLocationCoordProvider.stopRequestLocation();
        btHelper.disconnectDevice();
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
            arriveDestination();
            return false;
        }

        double distance = LocationUtils.getDistance(curLocation.getFrontLat(), curLocation.getFrontLon(),
                nextLocationCoord.getFrontLat(), nextLocationCoord.getFrontLon());
        return distance <= REACH_DISTANCE;
    }

    private void arriveDestination() {
        Toast.makeText(this, "arrive", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, DestinationArriveActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        stopForeground(true);
        stopSelf();
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
        Intent notificationIntent = new Intent(this, NavigationActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("시각 장애인용 네비게이션")
                .setContentText("경로 알림 중 입니다")
                .setContentIntent(pendingIntent)
                .build();
    }
}