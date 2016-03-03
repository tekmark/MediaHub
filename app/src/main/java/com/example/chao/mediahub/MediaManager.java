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
        List<MusicFile> musicFiles = new ArrayList<>();
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Log.d(TAG, "Scan all Music Files in MediaStore. Uri: " + musicUri.toString());
        //TODO: set projection[]
        String proj[] = {};
        Cursor cursor = musicResolver.query(musicUri, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            showColumnNames(cursor.getColumnNames());

            int titleColumn = cursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = cursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = cursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int albumColumn = cursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ALBUM);
            int isMusicColumn = cursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.IS_MUSIC);
            int durationColumn = cursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.DURATION);
            int pathColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            Log.d(TAG, "Start to parse MediaStore ");
            while (!cursor.isAfterLast()) {
                String filePath = cursor.getString(pathColumn);
                // check if the file is a music and the type is supported
                if (cursor.getInt(isMusicColumn) != 0 && filePath != null
                        && cursor.getInt(durationColumn) > 0) {
                    int thisId = cursor.getInt(idColumn);
                    String thisTitle = cursor.getString(titleColumn);
                    String thisArtist = cursor.getString(artistColumn);
                    String thisAlubm = cursor.getString(albumColumn);
                    int thisDuration = cursor.getInt(durationColumn);
                    Log.d(TAG, "ID: " + thisId + " Title: " + thisTitle + " Duration: " +
                            Utils.millSecondsToTime(thisDuration) + " Artist: " + thisArtist +
                            "Album : " + thisAlubm + " Path: " + filePath);
                    MusicFile file = new MusicFile();
                    file.setTitle(thisTitle);
                    file.setArtist(thisArtist);
                    file.setAudioId(thisId);
                    file.setAlbum(thisAlubm);
                    file.setDuration(thisDuration);
                    musicFiles.add(file);
                }
                cursor.moveToNext();
            }
            Log.d(TAG, "Finish. # of music files : " + musicFiles.size());
            cursor.close();
        } else { // if we don't have any media in the folder that we selected set NO MEDIA
            Log.d(TAG, "No music files in MediaStore.");
        }


//        if (musicFiles.size() == 0)
//        {
            //addNoSongs();
//        }
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

    public static List<Playlist> getAllPlaylists(Context context) {
        Log.d(TAG, "Scan all playlists in MediaStore. Playlist_ID | Playlist_NAME | Playlist_SIZE");
        List<Playlist> playlists = new ArrayList<>();
        String proj [] = {MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME};
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
//        Log.d(TAG, "URI = " + uri.toString());
        Cursor cursor = resolver.query(uri, proj, null, null, null);
        if (cursor == null) {
            Log.e(TAG, "Query MediaStore failed");
            return playlists;
        } else {

        }

        int columnCount = cursor.getColumnCount();
        int rowCount = cursor.getCount();
        Log.d(TAG, "Query MediaStore, # of records(playlists): " + rowCount);
        if (columnCount > 0) {
            String columnNames[] = cursor.getColumnNames();
            //show column names, for debug;
            showColumnNames(columnNames);

            int playlistIdColIndex = cursor.getColumnIndex(columnNames[0]);
            int playlistNameColIndex = cursor.getColumnIndex(columnNames[1]);
            if (rowCount > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    int playlistId = cursor.getInt(playlistIdColIndex);
                    String playlistName = cursor.getString(playlistNameColIndex);

                    //TODO: memory cost/efficiency, a new private method might be better.
                    int playlistSize = getPlaylistMusicFiles(context, playlistId).size();
                    Playlist playlist = new Playlist(playlistId, playlistName, playlistSize);
                    Log.d(TAG, "Playlist Id : " + playlistId + ", Name : " + playlistName +
                            ", # of files : " + playlistSize);

                    playlists.add(playlist);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        } else {
            Log.e(TAG, "Error: Get no column from MediaStore.");
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
                " # of records: " + rowCount);
        if (columnCount > 0) {
            String columnNames[] = musicCursor.getColumnNames();
            Log.d(TAG, MediaManagerUtils.convertColumnNamesToString(columnNames));
            int indexes[] = new int[columnCount];
            int i = 0;
            for (String name : columnNames) {
                indexes[i] = musicCursor.getColumnIndex(name);
                Log.v(TAG, "Column Names: " + name + " Column Index : " + indexes[i]);
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
                    Log.d(TAG, audioId + "|" + id + "|" + title + "|" + artist + "|" + album + "|" + duration + "|");
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
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        String proj [] = {MediaStore.Audio.Playlists._ID};
        String filter = MediaStore.Audio.Playlists.NAME+"="+playlistName;
        ContentResolver resolver = context.getContentResolver();
        if (resolver != null) {
            Cursor cursor = resolver.query(uri, proj, filter, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int rowCnt = cursor.getCount();
                if (rowCnt > 1) {
                    Log.w(TAG, "given playlist id has multiple entries in MediaStore.");
                }
                playlistId = cursor.getInt(0);
                cursor.close();
            }
        }
        return playlistId;
    }

    public static String lookupPlaylistName(Context context, int playlistId) {
        String playlistName = null;
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        String proj [] = {MediaStore.Audio.Playlists.NAME};
        String filter = MediaStore.Audio.Playlists._ID+"="+playlistId;
        ContentResolver resolver = context.getContentResolver();
        if (resolver != null) {
            Cursor cursor = resolver.query(uri, proj, filter, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int rowCnt = cursor.getCount();
                if (rowCnt > 1) {
                    Log.w(TAG, "given playlist id has multiple entries in MediaStore.");
                }
                playlistName = cursor.getString(0);
                cursor.close();
            }
        }

        return playlistName;
    }

    public static String generatePlaylistName(Context context) {
        String playlistName = "New Playlist";
        int n = 100;
        for (int i = 0; i < n; ++i) {
            playlistName += " ";
            playlistName +=i;
            if (lookupPlaylistId(context, playlistName) == -1) {
                return playlistName;
            }
        }
        return playlistName;
    }

    public static void writePlaylist(Context context, int playlistId, String playlistName,
                                     List<String> audioIds) {
        if (playlistName == null) {
            //generate a new playlist name
            //TODO: a method to generate a unique playlist name;
            playlistName = "New Playlist xxx";
        }

        ContentResolver resolver = context.getContentResolver();
        Uri uri = null;

        if (playlistId != -1 ) {
            //check if given playlist id exists in database;
            uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
            String proj [] = {MediaStore.Audio.Playlists.NAME};
            String filter = MediaStore.Audio.Playlists._ID+"="+playlistId;
            Cursor cursor = resolver.query(uri, proj, filter, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int rowCnt = cursor.getCount();
                if (rowCnt > 1) {
                    Log.w(TAG, "given playlist id has multiple entries in MediaStore.");
                }
                String name = cursor.getString(0);
                cursor.close();
                if (name.equals(playlistName)) {
                    Log.d(TAG, "Playlist name matches playlist name in MediaStore.");
                    uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
                    resolver.delete(uri, null, null);
                } else {
                    ContentValues values = new ContentValues(1);
                    values.put(MediaStore.Audio.Playlists.NAME, playlistName);
                    int result = resolver.update(uri, values, filter, null);
                    if (result == 0) {
                        Log.e(TAG, "Failed to update playlist name: " + name + "->" + playlistName);
                    } else if (result == 1) {
                        Log.i(TAG, "Success. playlist id: " + playlistId + " name: " + name + "->" + playlistName);
                        uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
                        resolver.delete(uri, null, null);
                    } else {
                        Log.w(TAG, "More than one playlists with same playlist id. ");
                    }
                }
            } else {
                Log.i(TAG, "given playlist id doesn't exist in MediaStore.");
                playlistId = -1;
            }
        }

        if (playlistId == -1) {           //create new playlist
            Log.d(TAG, "Create a new playlist : " + playlistName);
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Audio.Playlists.NAME, playlistName);
            uri = resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
        }

        int playOrder = 1;
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
    public static void writePlaylist(Context context, String playlistName,
                                      List<String> audioIds) {
        //int playlistId = lookupPlaylistId(context, playlistName);
        writePlaylist(context, -1, playlistName, audioIds);
    }


    public static boolean addMusicFileToPlaylist(Context context, int audioId, String playlistName) {
        int playlistId = lookupPlaylistId(context, playlistName);
        if (playlistId > 0) {
            return addMusicFileToPlaylist(context, audioId, playlistId);
        }
        return false;
    }


    public static boolean addMusicFileToPlaylist(Context context, int audioId, int playlistId) {
        ContentResolver resolver = context.getContentResolver();
        int playOrder = 1;

        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);

        if (playlistId < 0 || uri == null) {
            return false;
        } else {        //valid playlist id
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
            Log.d(TAG, "Insert AudioId : " + audioId + " to PlaylistId: " + playlistId);
            resolver.insert(uri, values);
            return true;
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

    private static void showColumnNames (String [] columns) {
        String colNames = " | ";
        for (String s: columns) {
            colNames += s;
            colNames += " | ";
        }
        Log.d(TAG, colNames);
    }

}
