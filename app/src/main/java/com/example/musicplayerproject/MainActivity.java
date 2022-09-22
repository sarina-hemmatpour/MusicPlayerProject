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
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements MusicAdapter.MusicClickedListener {

    //************************lyrics
    private ActivityMainBinding binding;

    private MediaPlayer mediaPlayer;

    public CurrentMusic currentMusic=new CurrentMusic();

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
        musicAdapter=new MusicAdapter(musicList ,this);
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
                        musicAdapter.onMusicStateChanged(currentMusic.getMusic() ,true);
                        break;
                    case PLAYING:
                        mediaPlayer.pause();
                        currentMusic.setState(CurrentMusic.MusicState.PAUSED);
                        binding.btnMainPlay.setImageResource(R.drawable.ic_play_32dp);
                        musicAdapter.onMusicStateChanged(currentMusic.getMusic() , false);

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
                changingMusicValidation(true);
            }
        });

        binding.imgMainBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changingMusicValidation(false);
//                goBackward();
            }
        });

        binding.mainImgRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentMusic.setRepeat(!currentMusic.isRepeat());
                //changing color
                if(currentMusic.isRepeat())
                {
                    //binding.mainImgRepeat.setImageAlpha(100);
                    //turning shuffle off
                    currentMusic.setShuffle(false);
                    binding.mainImgShuffle.setImageResource(R.drawable.ic_baseline_shuffle_24);

                    binding.mainImgRepeat.setImageResource(R.drawable.ic_baseline_repeat_24_alpha100);
                }
                else
                {
                    //binding.mainImgRepeat.setImageAlpha(55);
                    binding.mainImgRepeat.setImageResource(R.drawable.ic_baseline_repeat_24);

                }

            }
        });
        binding.mainImgShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentMusic.setShuffle(!currentMusic.isShuffle());

                if (currentMusic.isShuffle())
                {
                    //turning off repeat
                    currentMusic.setRepeat(false);
                    binding.mainImgRepeat.setImageResource(R.drawable.ic_baseline_repeat_24);

                    binding.mainImgShuffle.setImageResource(R.drawable.ic_baseline_shuffle_24_alpha100);
                }
                else
                    binding.mainImgShuffle.setImageResource(R.drawable.ic_baseline_shuffle_24);

            }
        });

    }

    private void onMusicChange(Music music , int index)
    {
        if (music==null)
            return;


        binding.sliderMainMusic.setValue(0);

        musicAdapter.setPlaying(true);

        currentMusic.setMusic(music);
        currentMusic.setIndex(index);

        musicAdapter.onMusicChanged(music);

        mediaPlayer=MediaPlayer.create(this , music.getFileResId());
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                currentMusic.setState(CurrentMusic.MusicState.PLAYING);
                musicAdapter.onMusicStateChanged(currentMusic.getMusic() , true);
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
                        changingMusicValidation(true);
//                        goForward();
                    }
                });
            }
        });


        binding.imgMainArtist.setActualImageResource(music.getArtistResId());
        binding.imgMainCover.setActualImageResource(music.getCoverResId());
        binding.tvMainArtist.setText(music.getArtist());
        binding.tvMainName.setText(music.getName());
    }

    private void changingMusicValidation(boolean forward)
    {
        if(currentMusic.isRepeat())
        {
            timer.cancel();
            timer.purge();
            mediaPlayer.release();
            onMusicChange(currentMusic.getMusic() , currentMusic.getIndex());
        }
        else if(currentMusic.isShuffle())
        {
            //random index
            timer.cancel();
            timer.purge();
            mediaPlayer.release();
            int randomPos=generateRandomIndex();
            onMusicChange(musicList.get(randomPos) , randomPos);
        }
        else
        {
            if(forward)
                goForward();
            else
                goBackward();
        }
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


    @Override
    public void onMusicClicked(Music music) {
        int index=musicList.indexOf(music);
        if(index==-1)
            return;

        timer.cancel();
        timer.purge();
        mediaPlayer.release();
        onMusicChange(musicList.get(index) , index);
    }

    private int generateRandomIndex()
    {
        int currentIndex=currentMusic.getIndex();
        int newIndex=-1;
        Random random=new Random();
        do
        {
            newIndex=random.nextInt(musicList.size());
        }while (newIndex==currentIndex);
        return newIndex;
    }
}