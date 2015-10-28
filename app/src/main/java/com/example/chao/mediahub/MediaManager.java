package com.example.chao.mediahub;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CHAO on 10/26/2015.
 */
public class MediaManager {
    final static private String TAG = "MediaManager";
    //final static private String DEFAULT_MEDIA_DIR = Environment.DIRECTORY_MUSIC;
    static public void scan(final Context context, String directory) {
        String scanDir = null;
        if (directory == null) {        //scan default if directory is null;
            //check if mounted.
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                scanDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MUSIC).getAbsolutePath();
            } else {
                Log.e(TAG, "sdcard is not available");
                return;
            }
        } else {        //scan directory
            File dir = new File(directory);
            if (dir.exists() && dir.isDirectory()) {
                scanDir = directory;
            } else {
                Log.e(TAG, "Failed to find directory : " + directory);
                return;
            }
        }
        Log.d(TAG, "Scan directory : " + scanDir);
        //add media files to media store;
        MediaScannerConnection.scanFile(context, new String[]{scanDir}, new String[]{"audio/*"}, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                //Toast.makeText(context, "Scan Complete", Toast.LENGTH_SHORT);
                Log.d(TAG, "Scan Completed. Path : " + path + " Uri : " + uri.toString());
            }
        });
    }


    static public ArrayList<String> getAllMusicFiles(Context context) {
        //Log.d("Songs Manager", "scan all songs on disk");
        ArrayList<String> songsList;
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String col[] ={android.provider.MediaStore.Audio.Media._ID};
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if (musicCursor != null && musicCursor.moveToFirst())
        {
            // clear  list to prevent duplicates
            songsList = new ArrayList<>();

            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int isMusicColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.IS_MUSIC);
            int duration = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);

            do
            {
                String filePath = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                // check if the file is a music and the type is supported
                if (musicCursor.getInt(isMusicColumn) != 0 && filePath != null && musicCursor.getInt(duration) > 0)
                {
                    int thisId = musicCursor.getInt(idColumn);
                    String thisTitle = musicCursor.getString(titleColumn);
                    String thisArtist = musicCursor.getString(artistColumn);
                    /*Song song = new Song();
                    song.setId(thisId);
                    if(!thisArtist.equals("<unknown>"))
                    {
                        song.setArtist(thisArtist);
                        song.setTitle(thisTitle);
                    }
                    else
                    {
                        song.setArtist("");
                        song.setTitle("");
                    }
                    song.setSongPath(filePath);
                    File file = new File(filePath);
                    song.setFileName(file.getName().substring(0, (file.getName().length() - 4)));*/
                    Log.d(TAG, "ID: " + thisId + " Title: " + thisTitle + " Artist: " + thisArtist);
                    songsList.add(thisTitle);
                }
            }
            while (musicCursor.moveToNext());
        } else { // if we don't have any media in the folder that we selected set NO MEDIA
            //addNoSongs();
            songsList = new ArrayList<String>();
        }

        musicCursor.close();

        if(songsList.size() == 0)
        {
            //addNoSongs();
        }
/*
        Collections.sort(songsList, new Comparator<Song>() {
            @Override
            public int compare(Song song, Song song2) {
                int compare = song.getTitle().compareTo(song2.getTitle());
                return ((compare == 0) ? song.getArtist().compareTo(
                        song2.getArtist()) : compare);
            }
        });
*/
        return songsList;
    }

    static public List<Playlist> getAllPlaylists(Context context) {
        List<Playlist> playlists = new ArrayList<>();
        String proj [] = {MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME};
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        Cursor playlistCursor = resolver.query(uri, proj, null, null, null);
        if (playlistCursor == null) {
            Log.e(TAG, "Query MediaStore failed");
            return playlists;
        }
        int columnCount = playlistCursor.getColumnCount();
        int rowCount = playlistCursor.getCount();
        Log.d(TAG, "# of Columns : " + columnCount + " # of records : " + rowCount);
        if (columnCount > 0) {
            String columnNames[] = playlistCursor.getColumnNames();
            int indexes[] = new int[columnCount];
            int i = 0;
            for(String colName : columnNames) {
                indexes[i] = playlistCursor.getColumnIndex(colName);
                Log.d(TAG, "Column Name: " + colName + " Column Index: " + indexes[i]);
                ++i;
            }
            if (rowCount > 0 && playlistCursor.moveToFirst()) {
                while (!playlistCursor.isAfterLast()) {
                    int playlistId = playlistCursor.getInt(indexes[0]);
                    String playlistName = playlistCursor.getString(indexes[1]);
                    Playlist playlist = new Playlist(playlistName);
                    playlist.setId(playlistId);
                    Log.d(TAG, "Playlist Id : " + playlistId + " Playlist Name : " + playlistName);
                    playlists.add(playlist);
                    playlistCursor.moveToNext();
                }
            }
            playlistCursor.close();
        }
        return playlists;
    }

    static public List<MusicFile> getPlaylistMusicFiles(Context context, int playlistId) {
        List<MusicFile> musicFiles = new ArrayList<>();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);

        String[] proj = {
                MediaStore.Audio.Playlists.Members.AUDIO_ID,            //0
                MediaStore.Audio.Playlists.Members.TITLE,               //1
                MediaStore.Audio.Playlists.Members.ARTIST,              //2
                MediaStore.Audio.Playlists.Members._ID,                 //3
                MediaStore.Audio.Playlists.Members.DATA,                //4
                MediaStore.Audio.Playlists.Members.DURATION,            //5
                MediaStore.Audio.Playlists.Members.ALBUM                //6
        };

        Cursor musicCursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (musicCursor == null) {
            Log.d(TAG, "Query MediaStore failed");
            return musicFiles;
        }

        int columnCount = musicCursor.getColumnCount();
        int rowCount = musicCursor.getCount();
        Log.d(TAG, "PlaylistID: " + playlistId + ", # of columns: " + columnCount + " ," +
                " # of records: "  + rowCount);
        if (columnCount > 0) {
            String columnNames[] = musicCursor.getColumnNames();
            int indexes[] = new int[columnCount];
            int i = 0;
            for (String name : columnNames) {
                indexes[i] = musicCursor.getColumnIndex(name);
                Log.d(TAG, "Column Names: " + name + " Column Index : " + indexes[i]);
                ++i;
            }

            if (rowCount > 0 && musicCursor.moveToFirst()) {
                while (!musicCursor.isAfterLast()) {
                    MusicFile musicFile = new MusicFile();
                    String duration = musicCursor.getString(indexes[MusicFile.DURATION]);
                    String title = musicCursor.getString(indexes[MusicFile.TITLE]);
                    String artist = musicCursor.getString(indexes[MusicFile.ARTIST]);
                    String album = musicCursor.getString(indexes[MusicFile.ALBUM]);
                    String path = musicCursor.getString(indexes[MusicFile.PATH]);
                    String id = musicCursor.getString(indexes[MusicFile.ID]);
                    String audioId = musicCursor.getString(indexes[MusicFile.AUDIO_ID]);


                    musicFile.setAudioId(Integer.parseInt(audioId));
                    musicFile.setId(Integer.parseInt(id));
                    musicFile.setDuration(duration);
                    musicFile.setAlbum(album);
                    musicFile.setArtist(artist);
                    musicFile.setTitle(title);
                    musicFile.setPath(path);

                    //order: audioId, title, artist, album, duration
                    Log.d(TAG, audioId + "|" + title + "|" + artist + "|" + album + "|" + duration);
                    Log.d(TAG, "Path: " + path);

                    musicFiles.add(musicFile);
                    musicCursor.moveToNext();
                }
            }
        }
        musicCursor.close();

        return musicFiles;
    }
}
