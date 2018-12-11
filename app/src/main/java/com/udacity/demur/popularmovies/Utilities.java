package com.udacity.demur.popularmovies;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Utilities {
    /*
     * This function is the product of gar at https://stackoverflow.com/a/4009133
     * suggested to use by Udacity to implement network connection check
     * */
    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }
}