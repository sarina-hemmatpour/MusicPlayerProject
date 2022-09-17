package com.example.musicplayerproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.example.musicplayerproject.databinding.ActivityMainBinding;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private MediaPlayer mediaPlayer;

    private CurrentMusic currentMusic=new CurrentMusic();

    private List<Music> musicList =Music.getList();

    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecyclerView rvMusicList=binding.rvMainPlaylist;
        rvMusicList.setLayoutManager(new LinearLayoutManager(this , RecyclerView.VERTICAL , false));
        rvMusicList.setAdapter(new MusicAdapter(musicList));

        onMusicChange(musicList.get(0));


        binding.btnMainPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentMusic==null)
                    return;

                switch (currentMusic.getState()){
                    case PAUSED:
                        mediaPlayer.start();
                        currentMusic.setState(CurrentMusic.MusicState.PLAYING);
                        binding.btnMainPlay.setImageResource(R.drawable.ic_pause_24dp);
                        break;
                    case PLAYING:
                        mediaPlayer.pause();
                        currentMusic.setState(CurrentMusic.MusicState.PAUSED);
                        binding.btnMainPlay.setImageResource(R.drawable.ic_play_32dp);

                }
            }
        });

    }

    private void onMusicChange(Music music)
    {
        if (music==null)
            return;

        currentMusic.setMusic(music);

        mediaPlayer=MediaPlayer.create(this , music.getFileResId());
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                timer=new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //it doesnt run on the main thread so we dont have access to the view
                        //so we have to run it on the main thread =>
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.tvMainTime.setText(Music.convertMillisToString(
                                        mediaPlayer.getCurrentPosition()
                                ));
                                binding.sliderMainMusic.setValue(mediaPlayer.getCurrentPosition());
                            }
                        });
                    }
                } , 1000 , 1000);

                currentMusic.setState(CurrentMusic.MusicState.PLAYING);

                binding.tvMainTotalDuration.setText(Music.convertMillisToString(
                        mediaPlayer.getDuration()
                ));
                binding.btnMainPlay.setImageResource(R.drawable.ic_pause_24dp);
                binding.sliderMainMusic.setValueTo(mediaPlayer.getDuration());
            }
        });


        binding.imgMainArtist.setActualImageResource(music.getArtistResId());
        binding.imgMainCover.setActualImageResource(music.getCoverResId());
        binding.tvMainArtist.setText(music.getArtist());
        binding.tvMainName.setText(music.getName());
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        //memory leak
        // garbage collector az heap kharejesh mikone
        mediaPlayer.release();
        mediaPlayer=null; //media player memory ziyad masraf mikone

        binding=null;
        timer.cancel();
    }



}