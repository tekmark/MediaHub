package com.example.chao.mediahub;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
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

    static public void scan (final Context context, String directory) {
        String scanDir = null;
        if (directory == null) {        //scan default if directory is null;
            Log.d(TAG, "Directory is not specified. Check external storage status ...");
            //check if mounted.
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Log.d(TAG, "External storage is mounted");
                scanDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MUSIC).getAbsolutePath();
            } else {
                Log.e(TAG, "External storage is not available");
                return;
            }
        } else {        //scan directory
            File dir = new File(directory);
            if (dir.exists() && dir.isDirectory()) {
                Log.d(TAG, "Directory exists. path: " + directory);
                String names[] = dir.list();
                for (String s: names) {
                    Log.d(TAG, s);
                }
                scanDir = directory;
            } else {
                Log.e(TAG, "Failed to find directory : " + directory);
                return;
            }
        }
        Log.d(TAG, "Scan directory : " + scanDir);
        //add media files to media store;
        MediaScannerConnection.scanFile(context, new String[]{scanDir}, /*new String[]{"audio/*"}*/null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                //Toast.makeText(context, "Scan Complete", Toast.LENGTH_SHORT);
                Log.d(TAG, "Scan Completed. Path : " + path + " Uri : " + uri.toString());
            }
        });
    }

    static public List<MusicFile> getAllMusicFiles(Context context) {
        Log.d(TAG, "Scan all songs in MediaStore");
        ArrayList<MusicFile> musicFiles;
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        Log.d(TAG, "Query Content Resolver. URI = " + musicUri.toString());
        if (musicCursor != null && musicCursor.moveToFirst())
        {
            // clear list to prevent duplicates
            musicFiles = new ArrayList<>();
            //Log.d(TAG, "TITLE | _ID | ARTIST | IS_MUSIC | DURATION");
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ALBUM);
            int isMusicColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.IS_MUSIC);
            int durationColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.DURATION);
            int pathColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            Log.d(TAG, "Start to parse MediaStore ");
            do
            {
                String filePath = musicCursor.getString(pathColumn);
                // check if the file is a music and the type is supported
                if (musicCursor.getInt(isMusicColumn) != 0 && filePath != null && musicCursor.getInt(durationColumn) > 0)
                {
                    int thisId = musicCursor.getInt(idColumn);
                    String thisTitle = musicCursor.getString(titleColumn);
                    String thisArtist = musicCursor.getString(artistColumn);
                    String thisAlubm = musicCursor.getString(albumColumn);
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
                    //Log.v(TAG, "Found music file : " + filePath);
                    Log.d(TAG, "ID: " + thisId + " Title: " + thisTitle + " Artist: " + thisArtist +
                            "Album : " + thisAlubm + " Path: " + filePath);
                    MusicFile file = new MusicFile();
                    file.setTitle(thisTitle);
                    file.setArtist(thisArtist);
                    file.setId(thisId);
                    file.setAlbum(thisAlubm);
                    musicFiles.add(file);
                }
            }
            while (musicCursor.moveToNext());
            Log.d(TAG, "Finish." + musicFiles.size() + " songs added");
            musicCursor.close();
        } else { // if we don't have any media in the folder that we selected set NO MEDIA
            //addNoSongs();
            Log.d(TAG, "No songs");
            //clear list;
            musicFiles = new ArrayList<MusicFile>();
        }


        if (musicFiles.size() == 0)
        {
            //addNoSongs();
        }
/*
        Collections.sort(musicFiles, new Comparator<Song>() {
            @Override
            public int compare(Song song, Song song2) {
                int compare = song.getTitle().compareTo(song2.getTitle());
                return ((compare == 0) ? song.getArtist().compareTo(
                        song2.getArtist()) : compare);
            }
        });
*/
        return musicFiles;
    }
    static public ArrayList<String> getAllMusicFileTitles(Context context) {
        Log.d(TAG, "Scan all songs in MediaStore");
        ArrayList<String> songsList;
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        Log.d(TAG, "Query Content Resolver. URI = " + musicUri.toString());
        if (musicCursor != null && musicCursor.moveToFirst())
        {
            // clear list to prevent duplicates
            songsList = new ArrayList<>();
            //Log.d(TAG, "TITLE | _ID | ARTIST | IS_MUSIC | DURATION");
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int isMusicColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.IS_MUSIC);
            int durationColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);
            int pathColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            Log.d(TAG, "Start to parse MediaStore ");
            do
            {
                String filePath = musicCursor.getString(pathColumn);
                // check if the file is a music and the type is supported
                if (musicCursor.getInt(isMusicColumn) != 0 && filePath != null && musicCursor.getInt(durationColumn) > 0)
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
                    //Log.v(TAG, "Found music file : " + filePath);
                    Log.d(TAG, "ID: " + thisId + " Title: " + thisTitle + " Artist: " + thisArtist +
                            "Path: " + filePath);
                    songsList.add(thisTitle);
                }
            }
            while (musicCursor.moveToNext());
            Log.d(TAG, "Finish." + songsList.size() + " songs added");
            musicCursor.close();
        } else { // if we don't have any media in the folder that we selected set NO MEDIA
            //addNoSongs();
            Log.d(TAG, "No songs");
            //clear list;
            songsList = new ArrayList<String>();
        }


        if (songsList.size() == 0)
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
        Log.d(TAG, "Scan all playlists in MediaStore. Playlist_ID | Playlist_NAME | Playlist_SIZE");
        List<Playlist> playlists = new ArrayList<Playlist>();
        String proj [] = {MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME};
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        Log.d(TAG, "URI = " + uri.toString());
        Cursor playlistCursor = resolver.query(uri, proj, null, null, null);
        if (playlistCursor == null) {
            Log.e(TAG, "Query MediaStore failed");
            return playlists;
        } else {
            String colNames = "";
            for (String s: playlistCursor.getColumnNames()) {
                colNames += s;
                colNames += "|";
            }
            Log.d(TAG, "column names: " + colNames);
        }
        int columnCount = playlistCursor.getColumnCount();
        int rowCount = playlistCursor.getCount();
        Log.d(TAG, "Found " + rowCount + " records(playlists).");
        if (columnCount > 0) {
            String columnNames[] = playlistCursor.getColumnNames();
            int indexes[] = new int[columnCount];
            int i = 0;
            for(String colName : columnNames) {
                indexes[i] = playlistCursor.getColumnIndex(colName);
                Log.v(TAG, "Column Name: " + colName + " Column Index: " + indexes[i]);
                ++i;
            }
            if (rowCount > 0 && playlistCursor.moveToFirst()) {
                Log.d(TAG, "Start to read playlist");
                while (!playlistCursor.isAfterLast()) {
                    int playlistId = playlistCursor.getInt(indexes[0]);
                    String playlistName = playlistCursor.getString(indexes[1]);
                    //int playlistSize = playlistCursor.getInt(indexes[2]);
                    Playlist playlist = new Playlist(playlistName);
                    playlist.setId(playlistId);
                    Log.d(TAG, "Playlist Id : " + playlistId + " Playlist Name : " + playlistName);

                    //TODO: memory cost/efficiency, a new private method might be better.
                    List<MusicFile> files = getPlaylistMusicFiles(context, playlistId);
                    playlist.setSize(files.size());
                    Log.d(TAG, " # of files : " + files.size());
                    playlists.add(playlist);
                    playlistCursor.moveToNext();
                }
            }
            playlistCursor.close();
        } else {
            Log.e(TAG, " 0 column, check MediaStore");
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
                    musicFile.setDuration(Integer.parseInt(duration));
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

    public static int lookupPlaylistId(Context context, String playlistName) {

        int playlistId = -1;

        return playlistId;
    }

    public static void writePlaylist(Context context, String playlistName,
                                      List<String> audioIds) {
        ContentResolver resolver = context.getContentResolver();

        int playlistId = lookupPlaylistId(context, playlistName);
        int playOrder = 1;
        Uri uri;
        if (playlistId == -1) {     //create new playlist
            Log.d(TAG, "Create a new playlist : " + playlistName);
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Audio.Playlists.NAME, playlistName);
            uri = resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);

        } else {                    //update old playlist
            Log.d(TAG, "update old playlist");
            uri = null;
        }

        if (uri != null) {
            //add songs;
            Log.d(TAG, "# of songs : " + audioIds.size());
            Log.d(TAG, "playlist uri: " + uri.toString());
            int size = audioIds.size();
            ContentProviderClient provider = resolver.acquireContentProviderClient(uri);
            if (provider != null) {
                ContentValues values[] = new ContentValues[size];
                for (int i = 0; i < size; ++i) {
                    values[i] = new ContentValues();
                    values[i].put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioIds.get(i));
                    values[i].put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, playOrder);
                    ++playOrder;
                    resolver.insert(uri, values[i]);
                }
                //resolver.bulkInsert(uri, values);
                provider.release();
                Log.d(TAG, size + " music files are added.");
            } else {
                Log.e(TAG, "ContentProvider Error");
            }
        } else {
            Log.e(TAG, "URI is null");
        }
    }

    public static void deletePlaylist(Context context, int playlistId) {
        ContentResolver resolver = context.getContentResolver();
        // Delete playlist contents
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external",
                playlistId);
        resolver.delete(uri, null, null);
        // Delete row in playlist database
        String filter = MediaStore.Audio.Playlists._ID + "=" + playlistId;
        resolver.delete(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, filter, null);
    }
}
