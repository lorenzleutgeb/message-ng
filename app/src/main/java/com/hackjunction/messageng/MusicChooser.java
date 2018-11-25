package com.hackjunction.messageng;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MusicChooser extends AppCompatActivity {

    ArrayList<String> audioList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_chooser);

        ListView audioView = (ListView) findViewById(R.id.songView);

        audioList = new ArrayList<>();

        String[] proj = { MediaStore.Audio.Media._ID,MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA };// Can include more data for more details and check it.

        Cursor audioCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);

        if(audioCursor != null){
            if(audioCursor.moveToFirst()){
                do{
                    //int audioIndex = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                    String path = audioCursor.getString(audioCursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                    //audioList.add(audioCursor.getString(audioIndex));
                    audioList.add(path);
                }while(audioCursor.moveToNext());
            }
        }
        audioCursor.close();

        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,android.R.id.text1, audioList);
        //audioView.setAdapter(adapter);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                audioList );

        audioView.setAdapter(arrayAdapter);

        audioView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                String item = ((TextView)view).getText().toString();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("song", item);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();

            }
        });


    }


}
