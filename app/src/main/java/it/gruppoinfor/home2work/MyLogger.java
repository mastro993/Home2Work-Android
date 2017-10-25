package it.gruppoinfor.home2work;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.orhanobut.logger.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.FileHandler;

/**
 * Created by Federico on 11/03/2017.
 * Classe custom per i logger con scrittura su file locale
 */

public class MyLogger {

    public static FileHandler logger = null;

    private static boolean isExternalStorageAvailable = false;
    private static boolean isExternalStorageWriteable = false;
    private static File baseDir;

    public static void init(Context context) {
        int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            isExternalStorageAvailable = isExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            isExternalStorageAvailable = true;
            isExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            isExternalStorageAvailable = isExternalStorageWriteable = false;
        }

        baseDir = Environment.getExternalStorageDirectory();

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void appendLog(String TAG, String message) {
        if (isExternalStorageAvailable && isExternalStorageWriteable) {

            SimpleDateFormat filenameDateFormat = new SimpleDateFormat("dd_MM_yyyy", Locale.ITALY);
            String filename = filenameDateFormat.format(new Date());


            File logFile = new File(baseDir + "/Home2Work/Logs/" + filename + ".txt");

            try {
                if (!logFile.exists()) {
                    logFile.getParentFile().mkdirs();
                    logFile.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                //BufferedWriter for performance, true to set append to file flag
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.ITALY);
                String currentDateandTime = timeFormat.format(new Date());
                buf.write("[" + currentDateandTime + "] " + TAG + ": " + message);
                buf.newLine();
                buf.flush();
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void logger(int priority, String TAG, String message, Throwable throwable) {
        Logger.log(priority, TAG, message, throwable);
        appendLog(TAG, message);
    }

    public static void d(String TAG, String message) {
        Log.d(TAG, message);
        appendLog(TAG, message);
    }

    public static void v(String TAG, String message){
        Log.v(TAG, message);
        appendLog(TAG, message);
    }

    public static void i(String TAG, String message){
        Log.i(TAG, message);
        appendLog(TAG, message);
    }

    public static void e(String TAG, String message, Throwable throwable) {
        logger(Logger.ERROR, TAG, message, throwable);
    }

    public static void w(String TAG, String message) {
        logger(Logger.WARN, TAG, message, null);
    }


}
