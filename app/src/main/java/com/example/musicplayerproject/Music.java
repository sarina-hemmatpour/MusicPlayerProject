package com.example.musicplayerproject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Music {
    private int id;
    private String name;
    private String artist;
    private int coverResId;
    private int artistResId;
    private int fileResId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getCoverResId() {
        return coverResId;
    }

    public void setCoverResId(int coverResId) {
        this.coverResId = coverResId;
    }

    public int getArtistResId() {
        return artistResId;
    }

    public void setArtistResId(int artistResId) {
        this.artistResId = artistResId;
    }

    public int getFileResId() {
        return fileResId;
    }

    public void setFileResId(int fileResId) {
        this.fileResId = fileResId;
    }

    public static List<Music> getList(){
        List<Music> musicList=new ArrayList<>();


        Music music1 = new Music();
        music1.setArtist("Lana Del Rey");
        music1.setName("Dark Paradise");
        music1.setCoverResId(R.drawable.music_1_cover);
        music1.setArtistResId(R.drawable.music_1_artist);
        music1.setFileResId(R.raw.music_1);

        Music music2 = new Music();
        music2.setArtist("Harry Styles");
        music2.setName("As It Was");
        music2.setCoverResId(R.drawable.music_2_cover);
        music2.setArtistResId(R.drawable.music_2_artist);
        music2.setFileResId(R.raw.music_2);

        Music music3 = new Music();
        music3.setArtist("Winona Oak");
        music3.setName("Piano In The Sky");
        music3.setCoverResId(R.drawable.music_3_cover);
        music3.setArtistResId(R.drawable.music_3_artist);
        music3.setFileResId(R.raw.music_3);

        Music music4=new Music();
        music4.setArtist("Gloria Gaynor");
        music4.setName("I Will Survive");
        music4.setCoverResId(R.drawable.music_4_cover);
        music4.setArtistResId(R.drawable.music_4_artist);
        music4.setFileResId(R.raw.music_4);

        musicList.add(music1);
        musicList.add(music2);
        musicList.add(music3);
        musicList.add(music4);
        return musicList;
    }

    public static String convertMillisToString(long durationInMillis) {
        long second = (durationInMillis / 1000) % 60;
        long minute = (durationInMillis / (1000 * 60)) % 60;

        return String.format(Locale.US, "%02d:%02d", minute, second);
    }
}
