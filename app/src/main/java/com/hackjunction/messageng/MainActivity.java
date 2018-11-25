package com.hackjunction.messageng;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private LocalBroadcastManager localBroadcastManager;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), MessageNG.WAVE_UPDATE)) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("emotion")
                        .document("state")
                        .update(
                                "state",intent.getBooleanExtra("state",true)
                        );
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void permissionCheck() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, "The permission to get BLE location data is required", Toast.LENGTH_SHORT).show();
            } else {
                requestPermissions(new String[] {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE
                }, 1);
            }
        } else {
            Log.i(TAG, "Permissions are fine.");
            //Toast.makeText(this, "Location permissions are fine!", Toast.LENGTH_SHORT).show();
        }
    }

    // Brian Code ¡¡¡¡¡
    private LineGraphSeries<DataPoint> series;

    private final Handler handler= new Handler();

    private final Runnable r = new Runnable() {
        private int x = 0;
        @Override
        public void run() {
            series.appendData(
                    new DataPoint(x,getRandom()),false, 100, false
            );
            x++;
            handler.postDelayed(r,20);
        }
    };

    double start = -5;
    double end = 5;
    Random mRand = new Random();

    private double getRandom() {
        return start + (mRand.nextDouble() * (end - start));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    // ¡¡¡
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // !! Brian Code

        handler.post(r);

        getSupportActionBar().hide();
        initGraph();
        // ¡¡

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionCheck();
        }

        //FirebaseFirestore.setLoggingEnabled(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //final DocumentReference docRef = db.collection("heartbeat").document(
        //    FirebaseInstanceId.getInstance().getId()
        //);

        final DocumentReference docRef = db.collection("emotion").document("state");

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    Object o = snapshot.get("state");
                    if (o == null) {
                        return;
                    }
                    Toast.makeText(MainActivity.this, o.toString(), Toast.LENGTH_LONG).show();

                    //updateUI(o.toString());

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(MessageNG.WAVE_UPDATE);
        localBroadcastManager.registerReceiver(broadcastReceiver, filter);

        startService(new Intent(MainActivity.this, MuseService.class));



    }

    /*public void updateUI(String stateString) {
        TextView tv1 = findViewById(R.id.stateText);
        tv1.setText(stateString);
    }*/

    void initGraph() {
        GraphView graph = (GraphView) findViewById(R.id.graph);
        this.series = new LineGraphSeries<>();
        series.setThickness(15);
        series.setColor(Color.argb(50, 255, 255, 255));
        series.setBackgroundColor(Color.argb(50, 255, 255, 255));
        series.setDrawBackground(true);
        graph.addSeries(series);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);// It will remove the background grids
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);// remove horizontal x labels and line
        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
    }


    public void didTapPlayButton(View view) {
        animateButton();
    }

    void animateButton() {
        // Load the animation
        final Animation myAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);

        // Use custom animation interpolator to achieve the bounce effect
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);

        myAnim.setInterpolator(interpolator);

        // Animate the button
        Button button = (Button)findViewById(R.id.button);
        button.startAnimation(myAnim);


    }
}
