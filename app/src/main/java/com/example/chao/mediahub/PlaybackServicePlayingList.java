package com.example.chao.mediahub;

import android.text.style.TtsSpan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * playlist used by MusicPlaybackService.
 * Usage:
 * playlist.hasNext();
 * playlist.nextIndex();
 * playlist.next();
 * Created by CHAO on 10/27/2015.
 */

public class PlaybackServicePlayingList {
    public static final int PLAYLIST_INVALID_POS = -1;

    private List<MusicFile> mMusicFiles;
    //private ListIterator<MusicFile> mIterator;
//    private String mPlaylistName;
    private List<Integer> mOrderList;
    private ListIterator<Integer> mOrderListIterator;
    private int mOrderPos;

    //status
    private boolean mLooping;
    private boolean mShuffling;


    private PlaybackServicePlayingList() {
        mMusicFiles = new ArrayList<>();
        mOrderList = new LinkedList<>();
        mOrderListIterator = mOrderList.listIterator();
        mLooping = false;
        mShuffling = false;
    }

    //copy constructor
//    public PlaybackServicePlayingList(PlaybackServicePlayingList list) {
//        mPlaylistName = list.mPlaylistName;
//        mMusicFiles = new ArrayList<>(list.mMusicFiles);
//        mOrderList = new LinkedList<>(list.mOrderList);
//        mOrderListIterator = mOrderList.listIterator();
//        mIterator = mMusicFiles.listIterator();
//        mLooping = false;
//        mRepeat = false;
//        mShuffle = false;
//    }

    private void buildPlayOrderList() {
        int size = mMusicFiles.size();
        mOrderList.clear();
        for (int i = 0; i != size; ++i) {
            mOrderList.add(i);
        }
        mOrderListIterator = mOrderList.listIterator();
    }

    //factory method.
    public static PlaybackServicePlayingList newInstance() {
        PlaybackServicePlayingList list = new PlaybackServicePlayingList();
        return list;
    }

    public static PlaybackServicePlayingList newInstance(List<MusicFile> musicFiles) {
        PlaybackServicePlayingList list = new PlaybackServicePlayingList();
        list.update(musicFiles);
        return list;
    }

    public void update(List<MusicFile> list) {
//        mMusicFiles = list;
        //mIterator = mMusicFiles.listIterator();
//        int size = mMusicFiles.size();
        //build order list
//        mOrderList.clear();
//        for (int i = 0; i != size; ++i) {
//            mOrderList.add(i);
//        }
//        mOrderListIterator = mOrderList.listIterator();
        mMusicFiles.clear();
        mMusicFiles.addAll(list);
        buildPlayOrderList();
    }

    public MusicFile getMusicFile(int index) {
        return mMusicFiles.get(index);
    }

    public boolean hasNext() {
        if (mOrderListIterator.hasNext()) {
            return true;
        } else if (mLooping) {
            mOrderListIterator = mOrderList.listIterator();
            return mOrderListIterator.hasNext();
        } else {
            return false;
        }
    }

    public int nextIndex() {
        return mOrderList.get(mOrderListIterator.nextIndex());
    }

    public MusicFile next() {
        mOrderPos = mOrderListIterator.next();
        return mMusicFiles.get(mOrderPos);
    }

    public boolean hasPrevious() {
        if (mOrderListIterator.hasPrevious()) {
            return true;
        } else if (mLooping) {
            mOrderListIterator = mOrderList.listIterator(mMusicFiles.size());
            return mOrderListIterator.hasPrevious();
        } else {
            return false;
        }
    }
    public int previousIndex() {
        return mOrderList.get(mOrderListIterator.previousIndex());
    }

    public MusicFile previous() {
        mOrderPos = mOrderListIterator.previous();
        return mMusicFiles.get(mOrderPos);
    }

    public boolean isLooping() {
        return mLooping;
    }

    public void setLooping(boolean looping) {
        mLooping = looping;
    }

    public boolean isShuffling() {
        return mShuffling;
    }

    public void setShuffling(boolean shuffling) {
        mShuffling = shuffling;
    }

    public int size() {
        return mMusicFiles.size();
    }
    public List<MusicFile> getMusicFiles() {
        return mMusicFiles;
    }

    public void move(int i) {
//        mIterator = mMusicFiles.listIterator(i);
        mOrderListIterator = mOrderList.listIterator(i);
    }

    public void resetCur() {
//        mIterator = mMusicFiles.listIterator();
        mOrderListIterator = mOrderList.listIterator();
    }

    public void shuffle(boolean shuffling) {
        if (shuffling) {
            Collections.shuffle(mOrderList);
            mShuffling = true;
        } else {
            buildPlayOrderList();
            //mOrderListIterator = mOrderList.listIterator(mOrderPos + 1);
            mShuffling = false;
        }
    }

    public String getOrdersString() {
        String list = "";
        for (int i : mOrderList) {
            list += i;
            list += "->";
        }
        list += "end";
        return list;
    }
}
