package com.example.chao.mediahub;

import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by CHAO on 10/26/2015.
 */
public class Utils {
    static public String millSecondsToTime(long milliseconds) {
        int hour = (int) TimeUnit.MILLISECONDS.toHours(milliseconds);
        milliseconds = milliseconds - TimeUnit.HOURS.toMillis(hour);
        int min = (int) TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        milliseconds = milliseconds - TimeUnit.MINUTES.toMillis(min);
        int sec = (int) TimeUnit.MILLISECONDS.toSeconds(milliseconds);

        String finalTime = "";
        String secondField = "";
        if (hour > 0) {
            finalTime = hour + ":";
        }

        if (sec < 10) {
            secondField = "0" + sec;
        } else {
            secondField = "" + sec;
        }

        finalTime = finalTime + min + ":" + secondField;
        return finalTime;
    }

}
