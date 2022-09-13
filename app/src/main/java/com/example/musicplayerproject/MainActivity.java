package com.example.musicplayerproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.facebook.drawee.backends.pipeline.Fresco;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView rvMusicList=findViewById(R.id.rv_main_playlist);
        rvMusicList.setLayoutManager(new LinearLayoutManager(this , RecyclerView.VERTICAL , false));
        rvMusicList.setAdapter(new MusicAdapter());

    }
}