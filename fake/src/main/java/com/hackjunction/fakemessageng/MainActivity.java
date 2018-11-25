package com.hackjunction.fakemessageng;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.iid.FirebaseInstanceId;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void permissionCheck() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, "The permission to get BLE location data is required", Toast.LENGTH_SHORT).show();
            } else {
                requestPermissions(new String[] {
                    // TODO
                }, 1);
            }
        } else {
            Log.i(TAG, "Permissions are fine.");
            //Toast.makeText(this, "Location permissions are fine!", Toast.LENGTH_SHORT).show();
        }
    }
    Button button;
    Button button2;
    SeekBar viewById;
    TextView seekBarValue;
    FirebaseFirestore db;
    //private final Handler handler = new Handler();
    //Map<String, Object> latest = new HashMap<>();
    int counter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseFirestore.setLoggingEnabled(true);
        db = FirebaseFirestore.getInstance();
        viewById = (SeekBar) findViewById(R.id.seekBar);
        seekBarValue = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        //final BigDecimal[] latest = {BigDecimal.valueOf(0)};
        viewById.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                BigDecimal b = BigDecimal.valueOf(progress).movePointLeft(9);
                //latest.put("alpha",Collections.singletonList(b.doubleValue()));
                seekBarValue.setText( b.toString());
                counter++;
                if (counter >= 5) {
                    Map<String, Object> newData = new HashMap<>();
                    newData.put("alpha", Collections.singletonList(b.doubleValue()));
                    db.collection("emotion").document("state").set(newData);
                    counter = 0;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

        });
        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Map<String, Object> newData = new HashMap<>();
                newData.put("alpha", 1);
                db.collection("emotion").document("state").set(newData);
            }
        });
        button2.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Map<String, Object> newData = new HashMap<>();
                newData.put("alpha", 0);
                db.collection("emotion").document("state").set(newData);
            }
        });
            /*
            private final Runnable tickUi = new Runnable() {
                @Override
                public void run() {
                    if (!latest.isEmpty()) {
                        Log.d(TAG, latest.toString());
                        db.collection("emotion").document("state").set(latest);
                        latest.clear();
                    }
                    handler.postDelayed(tickUi, 200);
                }
            };
            */

    //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        //    permissionCheck();
        //}

        //FirebaseFirestore.setLoggingEnabled(true);
/*
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> update = new HashMap<>();
        update.put("startup", new Date());
        update.put("message", "Hello!");
        final DocumentReference docRef = db.collection("heartbeat").document(
            FirebaseInstanceId.getInstance().getId()
        );
        docRef.set(update);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    Object o = snapshot.get("message");
                    if (o == null) {
                        return;
                    }
                    Toast.makeText(MainActivity.this, o.toString(), Toast.LENGTH_LONG).show();
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }

        });*/

    }


}
