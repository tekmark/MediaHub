package com.example.chao.mediahub;

import android.util.Log;

import java.util.List;

/**
 * Created by chaohan on 3/3/16.
 */
public class MediaManagerUtils {
    public static void dumpListMusicFiles(List<MusicFile> list) {
        String tag = "MusicFilesDump";
        for (MusicFile file : list) {
            Log.d(tag, file.toString());
        }
    }

    public static String convertColumnNamesToString (String [] columnNames) {
        String result = " | ";
        for (String s : columnNames) {
            result += s;
            result += " | ";
        }
        return result;
    }
}
