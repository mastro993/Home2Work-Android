package it.gruppoinfor.home2work.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.arasthel.asyncjob.AsyncJob;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import it.gruppoinfor.home2workapi.model.LatLng;

/**
 * Created by Federico on 04/02/2017.
 * <p>
 * Funzioni per la conversione di dati
 */

public class Converters {

    public static LatLng addressToLatLng(Context context, String addr) {

        Geocoder geocoder = new Geocoder(context, Locale.ITALY);

        double lat = 0.0;
        double lon = 0.0;

        try {
            List<Address> addressList = geocoder.getFromLocationName(addr, 1);
            if (addressList.size() > 0) {
                Address address = addressList.get(0);
                lat = address.getLatitude();
                lon = address.getLongitude();
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new LatLng(lat, lon);
    }

    public static String dateToString(Date date, String template) {
        DateFormat df = new SimpleDateFormat(template, Locale.ITALY);
        return df.format(date);
    }

    public static File bitmapToFile(Context context, Bitmap bitmap) {
        File filesDir = context.getCacheDir();
        File imageFile = new File(filesDir, "avatar.png");

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageFile;
    }

    public static String dateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ITALY);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        return sdf.format(date);
    }

}
