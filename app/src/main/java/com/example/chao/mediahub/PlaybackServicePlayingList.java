package com.example.chao.mediahub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by CHAO on 10/27/2015.
 */
public class PlaybackServicePlayingList {
    private static final String DEFAULT_PLAYLIST_NAME = "New Playlsit";

    private List<MusicFile> mMusicFiles;
    private String mPlaylistName;
    private ListIterator<MusicFile> mIterator;

    private boolean mLooping;
    private boolean mRepeat;

    public PlaybackServicePlayingList() {
        mPlaylistName = DEFAULT_PLAYLIST_NAME;
        mMusicFiles = new ArrayList<>();
        mIterator = mMusicFiles.listIterator();
        mRepeat = false;
        mLooping = false;
    }

    public PlaybackServicePlayingList(String name) {
        mPlaylistName = name;
        mMusicFiles = new ArrayList<>();
        mIterator = mMusicFiles.listIterator();
        mRepeat = false;
        mLooping = false;
    }

    //copy constructor
    public PlaybackServicePlayingList(PlaybackServicePlayingList list) {
        mPlaylistName = list.mPlaylistName;
        mMusicFiles = new ArrayList<>(list.mMusicFiles);
        mIterator = mMusicFiles.listIterator();
        mRepeat = false;
        mLooping = false;
    }

    //factory method.
    public static PlaybackServicePlayingList newInstance() {
        PlaybackServicePlayingList list = new PlaybackServicePlayingList();
        return list;
    }

    //getter and setter
    public String getPlaylistName() {
        return mPlaylistName;
    }
    public void setPlaylistName (String newPlaylistName) {
        mPlaylistName = newPlaylistName;
    }

    public void update(List<MusicFile> list) {
        mMusicFiles = list;
        mIterator = mMusicFiles.listIterator();
    }

    public void loadPlaylingList(List<MusicFile> list) {
        mMusicFiles = list;
        mIterator = mMusicFiles.listIterator();
    }

    public MusicFile getMusicFile(int index) {
        return mMusicFiles.get(index);
    }

    public boolean hasNext() {
        if (mIterator.hasNext()) {
            return true;
        } else if (mRepeat) {
            resetCur();
            return mIterator.hasNext();
        } else {
            return false;
        }
    }

    public boolean hasPrevious() {
        if (mIterator.hasPrevious()) {
            return true;
        } else if (mRepeat) {
            mIterator = mMusicFiles.listIterator(mMusicFiles.size());
            return mIterator.hasPrevious();
        } else {
            return false;
        }
    }

    //these two functions must be called after hasNext() and hasPrevious() respectively.
    public int nextIndex() {
        return mIterator.nextIndex();
    }
    public int previousIndex() {
        return mIterator.previousIndex();
    }

    public MusicFile next() {
        return mIterator.next();
    }

    public MusicFile previous() {
        return mIterator.previous();
    }

    public boolean isRepeat() {
        return mRepeat;
    }

    public void setRepeat() {
        mRepeat = true;
    }

    public void resetRepeat() {
        mRepeat = false;
    }

    public void shuffle() {
        Collections.shuffle(mMusicFiles);
        resetCur();
    }

    public void sort() {

    }

    public int size() {
        return mMusicFiles.size();
    }
    public List<MusicFile> getMusicFiles() {
        return mMusicFiles;
    }

    public void move(int i) {
        mIterator = mMusicFiles.listIterator(i);
    }

    public void resetCur() {
        mIterator = mMusicFiles.listIterator();
    }
}
