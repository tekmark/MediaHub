package com.example.chao.mediahub;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by CHAO on 10/26/2015.
 */
public class MusicFile implements Parcelable {
    final static public String UNKNOWN_MUSIC="Unknown Music";
    final static public String UNKNOWN_ARTIST="Unknown Artist";
    final static public String UNKNOWN_ALBUM="Unknown Album";
    final static public int UNKNOWN_DURATION = 0;
    final static public String INVALID_PATH = "";
    final static public int INVALID_ID = -1;

    final static public int AUDIO_ID = 0;                      //0
    final static public int TITLE = 1;                         //1
    final static public int ARTIST = 2;                        //2
    final static public int ID = 3;                            //3
    final static public int PATH = 4;                          //4
    final static public int DURATION = 5;                      //5
    final static public int ALBUM = 6;                         //6

    private int id;
    private int audioId;
    private String title;
    private String artist;
    private String album;
    private int duration;
    private String path;

    public MusicFile() {
        title = UNKNOWN_MUSIC;
        artist = UNKNOWN_ARTIST;
        album = UNKNOWN_ALBUM;
        path = INVALID_PATH;
        duration = UNKNOWN_DURATION;
        id = INVALID_ID;
        audioId = INVALID_ID;
    }

    public MusicFile(MusicFile musicFile) {
        title = musicFile.title;
        artist = musicFile.artist;
        album = musicFile.album;
        path = musicFile.path;
        duration = musicFile.duration;
        id = musicFile.id;
        audioId = musicFile.audioId;
    }

    public MusicFile(Parcel in) {
        audioId = in.readInt();
        title = in.readString();
        artist = in.readString();
        id = in.readInt();
        path = in.readString();
        duration = in.readInt();
        album = in.readString();
    }

    @Override
    public boolean equals(Object o) {
        //boolean isSame = false;
        if (o != null && o instanceof MusicFile) {
            if (audioId == ((MusicFile)o).audioId){
                return true;
            }
        }
        return false;
    }

    public void set(int pos, String s) {
        switch (pos) {
            case AUDIO_ID:
                setAudioId(Integer.parseInt(s));
            case ID:
                setId(Integer.parseInt(s));
            case TITLE:
                setTitle(s);
            case ARTIST:
                setArtist(s);
            case ALBUM:
                setAlbum(s);
            case PATH:
                setPath(s);
            case DURATION:
                setDuration(Integer.parseInt(s));
        }
    }

    public String get(int pos) {
        switch (pos) {
            case AUDIO_ID:
                return Integer.toString(getAudioId());
            case ID:
                return Integer.toString(getId());
            case TITLE:
                return getTitle();
            case ARTIST:
                return getArtist();
            case ALBUM:
                return getAlbum();
            case PATH:
                return getPath();
            case DURATION:
                return Integer.toString(getDuration());
            default:
                return "";
        }
    }

    public int getAudioId() {
        return audioId;
    }

    public int getId() {
        return id;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public int getDuration() {
        return duration;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAudioId(int audioId) {
        this.audioId = audioId;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String toString() {
        String string = "|";
        for (int i = 0; i < 6; ++i ) {
            string += get(i);
            string += "|";
        }
        return string;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(audioId);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeInt(id);
        dest.writeString(path);
        dest.writeInt(duration);
        dest.writeString(album);
    }

    public static final Parcelable.Creator<MusicFile> CREATOR = new Parcelable.Creator<MusicFile> () {

        public MusicFile createFromParcel(Parcel in) {
            return new MusicFile(in);
        }
        public MusicFile[] newArray(int size) {
            return new MusicFile[size];
        }
    };
}
