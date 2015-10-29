package com.example.chao.mediahub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by CHAO on 10/27/2015.
 */
public class PlaybackServicePlayingList {
    final static private String DEFAULT_PLAYLIST_NAME = "New Playlsit";

    private List<MusicFile> musicFiles;
    private String playlistName;
    private ListIterator<MusicFile> it;

    private boolean repeat;

    public PlaybackServicePlayingList() {
        playlistName = DEFAULT_PLAYLIST_NAME;
        musicFiles = new ArrayList<>();
        it = musicFiles.listIterator();
        repeat = false;
    }

    public PlaybackServicePlayingList(String name) {
        playlistName = name;
        musicFiles = new ArrayList<>();
        it = musicFiles.listIterator();
        repeat = false;
    }

    public PlaybackServicePlayingList(PlaybackServicePlayingList list) {
        playlistName = list.playlistName;
        musicFiles = new ArrayList<>(list.musicFiles);
        it = musicFiles.listIterator();
        repeat = false;
    }

    public void update(List<MusicFile> list) {
        musicFiles = list;
        it = musicFiles.listIterator();
        repeat = false;
    }


    public String getPlaylistName() {
        return playlistName;
    }
    public void setPlaylistName (String newPlaylistName) {
        playlistName = newPlaylistName;
    }

//    public void addMusicFile(MusicFile musicFile) {
//        musicFiles.add(musicFile);
//    }

    public MusicFile getMusicFile(int index) {
        return musicFiles.get(index);
    }

    public boolean hasNext() {
        if (it.hasNext()) {
            return true;
        } else if (repeat) {
            resetCur();
            return it.hasNext();
        } else {
            return false;
        }
    }

    public boolean hasPrevious() {
        if (it.hasPrevious()) {
            return true;
        } else if (repeat) {
            it = musicFiles.listIterator(musicFiles.size());
            return it.hasPrevious();
        } else {
            return false;
        }
    }

    //these two functions must be called after hasNext() and hasPrevious() respectively.
    public int nextIndex() {
        return it.nextIndex();
    }
    public int previousIndex() {
        return it.previousIndex();
    }

    public MusicFile next() {
        return it.next();
    }

    public MusicFile previous() {
        return it.previous();
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat() {
        repeat = true;
    }

    public void resetRepeat() {
        repeat = false;
    }

    public void shuffle() {
        Collections.shuffle(musicFiles);
        resetCur();
    }

    public void sort() {

    }

    public int size() {
        return musicFiles.size();
    }
    public List<MusicFile> getMusicFiles() {
        return musicFiles;
    }

    public void move(int i) {
        it = musicFiles.listIterator(i);
    }

    public void resetCur() {
        it = musicFiles.listIterator();
    }
}
