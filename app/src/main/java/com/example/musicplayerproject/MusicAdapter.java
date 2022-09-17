package com.example.musicplayerproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private List<Music> musicList;

    public MusicAdapter(List<Music> musicList) {
        this.musicList = musicList;
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
        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover=itemView.findViewById(R.id.img_itemMusic_cover);
            tvArtist=itemView.findViewById(R.id.tv_itemMusic_artist);
            tvTitle=itemView.findViewById(R.id.tv_itemMusic_songTitle);
        }

        public void bind(Music music)
        {
            imgCover.setActualImageResource(music.getCoverResId());
            tvTitle.setText(music.getName().trim());
            tvArtist.setText(music.getArtist());
        }
    }




}
