package com.example.musicplayerproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import com.example.musicplayerproject.databinding.ActivityMainBinding;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.slider.Slider;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private MediaPlayer mediaPlayer;

    private CurrentMusic currentMusic=new CurrentMusic();

    private List<Music> musicList =Music.getList();

    private Timer timer;

    private boolean isSliderDragging=false;

    private MusicAdapter musicAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecyclerView rvMusicList=binding.rvMainPlaylist;
        rvMusicList.setLayoutManager(new LinearLayoutManager(this , RecyclerView.VERTICAL , false));
        musicAdapter=new MusicAdapter(musicList);
        rvMusicList.setAdapter(musicAdapter);

        onMusicChange(musicList.get(0) , 0);


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


        binding.sliderMainMusic.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                binding.tvMainTime.setText(Music.convertMillisToString((long) value));
            }
        });

        binding.sliderMainMusic.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                isSliderDragging=true;
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                isSliderDragging=false;
                mediaPlayer.seekTo((int) slider.getValue());
            }
        });


        binding.imgMainForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goForward();
            }
        });

        binding.imgMainBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackward();
            }
        });

    }

    private void onMusicChange(Music music , int index)
    {
        if (music==null)
            return;


        binding.sliderMainMusic.setValue(0);

        currentMusic.setMusic(music);
        currentMusic.setIndex(index);

        mediaPlayer=MediaPlayer.create(this , music.getFileResId());
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                binding.sliderMainMusic.setValue(0);

                timer=new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //it doesnt run on the main thread so we dont have access to the view
                        //so we have to run it on the main thread =>
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!isSliderDragging)
                                {
                                    binding.tvMainTime.setText(Music.convertMillisToString(
                                            mediaPlayer.getCurrentPosition()
                                    ));
                                    binding.sliderMainMusic.setValue(mediaPlayer.getCurrentPosition());
                                }
                            }
                        });
                    }
                } , 1000 , 1000);

                binding.sliderMainMusic.setValueTo(mediaPlayer.getDuration());
                currentMusic.setState(CurrentMusic.MusicState.PLAYING);

                binding.tvMainTotalDuration.setText(Music.convertMillisToString(
                        mediaPlayer.getDuration()
                ));
                binding.btnMainPlay.setImageResource(R.drawable.ic_pause_24dp);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        goForward();
                    }
                });
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

    public void goForward()
    {
        timer.cancel();
        timer.purge(); //removes all cancelled tasks from timer task queue
        mediaPlayer.release();
        int index=currentMusic.getIndex();

        if (index==musicList.size()-1)
            index=0;
        else
            index++;

        onMusicChange(musicList.get(index) , index);
    }
    public void goBackward()
    {
        timer.cancel();
        timer.purge(); //removes all cancelled tasks from timer task queue
        mediaPlayer.release();
        int index=currentMusic.getIndex();

        if (index==0)
            index=musicList.size()-1;
        else
            index--;

        onMusicChange(musicList.get(index) , index);
    }



}