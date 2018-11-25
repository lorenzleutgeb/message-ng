package com.hackjunction.messageng;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MusicSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_settings);



    }

    public void buttonClick1(View view) {
        Intent myIntent = new Intent(MusicSettings.this, MusicChooser.class);
        //MusicSettings.this.startActivity(myIntent);

        //startActivityForResult(MusicChooser, ActivityTwoRequestCode)

        startActivityForResult(myIntent, 1);



    }

    public void buttonClick2(View view) {
        Intent myIntent = new Intent(MusicSettings.this, MusicChooser.class);
        //MusicSettings.this.startActivity(myIntent);

        //startActivityForResult(MusicChooser, ActivityTwoRequestCode)

        startActivityForResult(myIntent, 2);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String song = data.getStringExtra("song");
                Button tv1 = findViewById(R.id.happySongButton);
                tv1.setText(song);
            }
        } else {
            if (resultCode == RESULT_OK) {
                String song = data.getStringExtra("song");
                Button tv1 = findViewById(R.id.sadSongButton);
                tv1.setText(song);
            }
        }
    }
}
