package com.example.musicplayerproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private List<Music> musicList;

    private int currentPos=-1;

    private MusicClickedListener listener;

    private boolean isPlaying=true;

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public MusicAdapter(List<Music> musicList , MusicClickedListener listener) {
        this.musicList = musicList;
        this.listener=listener;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MusicViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music , parent , false));
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        holder.bind(musicList.get(position));
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public class MusicViewHolder extends RecyclerView.ViewHolder{

        SimpleDraweeView imgCover;
        TextView tvArtist , tvTitle;
        LottieAnimationView musicEffect;
        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover=itemView.findViewById(R.id.img_itemMusic_cover);
            tvArtist=itemView.findViewById(R.id.tv_itemMusic_artist);
            tvTitle=itemView.findViewById(R.id.tv_itemMusic_songTitle);
            musicEffect=itemView.findViewById(R.id.item_animation_playing);
        }

        public void bind(Music music)
        {
            imgCover.setActualImageResource(music.getCoverResId());
            tvTitle.setText(music.getName().trim());
            tvArtist.setText(music.getArtist());

            if (getAdapterPosition()==currentPos)
            {
                musicEffect.setVisibility(View.VISIBLE);
                if (isPlaying)
                {
                    musicEffect.playAnimation();
                }
                else
                {
                    musicEffect.pauseAnimation();
                }
            }
            else
            {
                musicEffect.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onMusicClicked(music);
                }
            });
        }
    }

    interface MusicClickedListener {
        void onMusicClicked(Music music);
    }


    public void onMusicChanged(Music music){
        int index=musicList.indexOf(music);

        if (index==-1 || index==currentPos)
            return;

        notifyItemChanged(currentPos);
        currentPos=index;
        notifyItemChanged(currentPos);
    }

    public void onMusicStateChanged(Music music , boolean state)
    {
        int index=musicList.indexOf(music);
        if (index==-1)
            return;

        isPlaying=state;
        notifyItemChanged(index);

    }

}
