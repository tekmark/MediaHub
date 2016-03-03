package com.example.chao.mediahub;

/**
 * Created by CHAO on 10/26/2015.
 */
public class Playlist {
    final static private String DEFAULT_PLAYLIST_NAME = "New Playlist";
    final static public int INVALID_PLAYLIST_ID = -1;

    private String mName;
    private int mId;
    private int mSize;

    //private int mMediaStoreId;
    public Playlist() {
        mName = DEFAULT_PLAYLIST_NAME;
        mId = INVALID_PLAYLIST_ID;
        mSize = -1;
    }
    public Playlist(String name) {
        mName = name;
        mId = INVALID_PLAYLIST_ID;
        mSize = -1;
    }

    public Playlist(int playlistId, String playlistName, int playlistSize) {
        mId = playlistId;
        mName = playlistName;
        mSize = playlistSize;
    }


    //copy constructor
    public Playlist(Playlist playlist) {
        mName = playlist.mName;
        mId = playlist.mId;
        mSize = playlist.mSize;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
    public int getId() {
        return mId;
    }
    public void setId(int id) {
        mId = id;
    }
    public void setSize(int size) {
        mSize = size;
    }
    public int getSize() {
        return mSize;
    }




}
