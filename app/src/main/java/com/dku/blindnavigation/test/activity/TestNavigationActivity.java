package com.dku.blindnavigation.test.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.dku.blindnavigation.R;
import com.dku.blindnavigation.navigation.direction.DirectionCalculator;
import com.dku.blindnavigation.navigation.direction.DirectionType;
import com.dku.blindnavigation.navigation.dto.Poi;
import com.dku.blindnavigation.navigation.location.gps.CurLocationCoordProvider;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Queue;

public class TestNavigationActivity extends AppCompatActivity {
    private static final String TAG = "TestNavigationActivity";
    private static final int REACH_DISTANCE = 20;
    private Poi prevLocation;
    private Poi curLocation;
    private Queue<Poi> routes;

    private CurLocationCoordProvider curLocationCoordProvider;
    private TextView prevLocationCoordTV;
    private TextView curLocationCoordTV;
    private TextView nextLocationCoordTV;
    private TextView nextDirectionTV;
    private final Handler handler = new EventHandler(this);

    private static final class EventHandler extends Handler {
        private final WeakReference<TestNavigationActivity> reference;
        public EventHandler(TestNavigationActivity activity) {
            super(Looper.getMainLooper());
            this.reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            TestNavigationActivity activity = reference.get();
            Bundle msgData = msg.getData();
            int eventType = msgData.getInt("eventType");
            boolean status = msgData.getBoolean("status");

            switch(eventType) {
                case CurLocationCoordProvider.EVENT_TYPE:
                    Poi gpsCurLocation = msgData.getParcelable("curLocationCoord");
                    boolean reachNextLocation = activity.checkReachNextLocation(gpsCurLocation);
                    String curLocationString = "[" + gpsCurLocation.getFrontLat() + " " + gpsCurLocation.getFrontLon() + "]";
                    activity.curLocationCoordTV.setText(curLocationString);

                    if(!reachNextLocation && !activity.routes.isEmpty()) return;

                    activity.prevLocation = activity.curLocation;
                    activity.curLocation = activity.routes.peek();
                    Poi nextLocation = activity.getNextLocation(gpsCurLocation);
                    if(nextLocation == null) activity.nextLocationCoordTV.setText("목적지 도착!");

                    String prevLocationString = "[" + activity.prevLocation.getFrontLat() + " " + activity.prevLocation.getFrontLon() + "]";
                    String nextLocationString = "[" + nextLocation.getFrontLat() + " " + nextLocation.getFrontLon() + "]";
                    activity.prevLocationCoordTV.setText(prevLocationString);
                    activity.nextLocationCoordTV.setText(nextLocationString);
                    Log.d(TAG, nextLocationString);

                    DirectionType nextDirection = DirectionCalculator.getNextDirection(activity.prevLocation, activity.curLocation, nextLocation);
                    activity.nextDirectionTV.setText(nextDirection.toString());
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_navigation);

        curLocation = getIntent().getParcelableExtra("departureLocation");
        routes = new LinkedList<>(getIntent().getParcelableArrayListExtra("route"));
        double startPhoneDegree = getIntent().getDoubleExtra("degree", 0.0);

        curLocationCoordProvider = new CurLocationCoordProvider(this, handler);

        prevLocationCoordTV = findViewById(R.id.prevLocationCoordTV);
        curLocationCoordTV = findViewById(R.id.curLocationCoordTV);
        nextLocationCoordTV = findViewById(R.id.nextLocationCoordTV);
        nextDirectionTV = findViewById(R.id.nextDirectionTV);

        String curLocationCoordStr = "[" + curLocation.getFrontLat() + " " + curLocation.getFrontLon() + "]";
        curLocationCoordTV.setText(curLocationCoordStr);

        Poi nextLocation = getNextLocation(curLocation);
        if(nextLocation != null)  {
            DirectionType curDirection = DirectionCalculator.getFirstDirection(curLocation, nextLocation, startPhoneDegree);
            String nextLocationCoordStr = "[" + nextLocation.getFrontLat() + " " + nextLocation.getFrontLon() + "]";
            nextLocationCoordTV.setText(nextLocationCoordStr);
            nextDirectionTV.setText(curDirection.toString());
        }
    }

    @Nullable
    private Poi getNextLocation(Poi curLocation) {
        while(checkReachNextLocation(curLocation)) routes.poll();
        Poi nextLocation = routes.peek();
        return nextLocation;
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

    private boolean checkReachNextLocation(Poi curLocation) {
        if(routes.isEmpty()) return false;
        Poi nextLocationCoord = routes.peek();
        if(nextLocationCoord == null) return true;

        double distance = getDistance(curLocation.getFrontLat(), curLocation.getFrontLon(),
                nextLocationCoord.getFrontLat(), nextLocationCoord.getFrontLon());
        return distance <= REACH_DISTANCE;
    }

    private static double getDistance(double lat1, double lon1, double lat2, double lon2){
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))* Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))*Math.cos(deg2rad(lat2))*Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60*1.1515*1609.344;

        return Double.isNaN(dist) ? 0 : dist; //단위 meter
    }

    private static double deg2rad(double deg){
        return (deg * Math.PI/180.0);
    }
    //radian(라디안)을 10진수로 변환
    private static double rad2deg(double rad){
        return (rad * 180 / Math.PI);
    }
}