package com.hackjunction.fakemessageng;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    SeekBar viewById;
    TextView seekBarValue;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseFirestore.setLoggingEnabled(true);
        db = FirebaseFirestore.getInstance();
        viewById = (SeekBar) findViewById(R.id.seekBar);
        seekBarValue = findViewById(R.id.textView);
        viewById.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                BigDecimal b = BigDecimal.valueOf(progress).movePointLeft(9);
                seekBarValue.setText( b.toString());
                Map<String, Object> newData = new HashMap<>();
                newData.put("alpha",Collections.singletonList(b.doubleValue()));
                db.collection("emotion").document("state").set(newData);
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
