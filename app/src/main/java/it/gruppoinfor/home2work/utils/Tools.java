package it.gruppoinfor.home2work.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import it.gruppoinfor.home2work.utils.ScalingUtilities;

/**
 * Created by Federico on 04/02/2017.
 * <p>
 * Altre funzioni
 */

public class Tools {

    public static String getFileExtension(String path) {
        String filenameArray[] = path.split("\\.");
        return filenameArray[filenameArray.length - 1];
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String decodeFile(String path, int DESIREDWIDTH, int DESIREDHEIGHT) {
        String strMyImagePath = null;
        Bitmap scaledBitmap;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

            if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
                // Part 2: Scale image
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);
            } else {
                unscaledBitmap.recycle();
                return path;
            }

            // Store to tmp file

            String extr = Environment.getExternalStorageDirectory().toString();
            File mFolder = new File(extr + "/TMMFOLDER");
            if (!mFolder.exists()) {
                boolean dirCreated = mFolder.mkdir();
            }

            String s = "tmp.png";

            File f = new File(mFolder.getAbsolutePath(), s);

            strMyImagePath = f.getAbsolutePath();
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            }

            scaledBitmap.recycle();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;

    }

    @SuppressWarnings("SameParameterValue")
    public static Bitmap shrinkBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    public static Date getCurrentDate() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
        return cal.getTime();
    }

    public static long getCurrentTimestamp() {
        return System.currentTimeMillis() / 1000L;
    }

    public static float getDistance(LatLng a, LatLng b) {
        Location loc1 = new Location("");
        loc1.setLatitude(a.latitude);
        loc1.setLongitude(a.longitude);

        Location loc2 = new Location("");
        loc2.setLatitude(b.latitude);
        loc2.setLongitude(b.longitude);

        return loc1.distanceTo(loc2);
    }

    public static float getDistance(Location a, LatLng b) {
        Location loc2 = new Location("");
        loc2.setLatitude(b.latitude);
        loc2.setLongitude(b.longitude);

        return a.distanceTo(loc2);
    }


    //method to get the right URL to use in the intent
    public static String getFacebookPageURL(Context context, String pageID, String pageUrl) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + pageUrl;
            } else { //older versions of fb app
                return "fb://page/" + pageID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return pageUrl; //normal web url
        }
    }

    public static Date datetimeToDate(String datetime) {
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY);
            return format.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static Intent getOpenFacebookIntent(Context context, String username) {

        Uri uri = Uri.parse("http://fb.me/" + username);
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                // http://stackoverflow.com/a/24547437/1048340
                uri = Uri.parse("fb://facewebmodal/f?href=" + "http://fb.me/" + username);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return new Intent(Intent.ACTION_VIEW, uri);
    }

    public static Intent getOpenTwitterIntent(Context context, String username) {

        try {
            context.getPackageManager().getPackageInfo("com.twitter.android", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=" + username));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + username));
        }
    }


    public static void removeKeyboard(Activity activity) {

        View view = activity.getCurrentFocus();

        if (view != null) {
            // Chiude la tastiera se presente
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
