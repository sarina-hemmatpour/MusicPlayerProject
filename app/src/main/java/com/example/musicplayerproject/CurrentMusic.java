package com.example.musicplayerproject;

public class CurrentMusic{
    enum MusicState{PLAYING , PAUSED}

    private MusicState state=MusicState.PAUSED;
    private Music music;
    private int index;

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public MusicState getState() {
        return state;
    }

    public void setState(MusicState state) {
        this.state = state;
    }
}
