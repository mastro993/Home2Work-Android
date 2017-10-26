package it.gruppoinfor.home2work.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

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

    public static Long timeToTimestamp(String time) {

        String[] times = time.split(":");

        Long hours, minutes, seconds = 0L;

        hours = Long.parseLong(times[0]);
        minutes = Long.parseLong(times[1]);

        if (times.length == 3) {
            seconds = times[2] == null ? 0L : Integer.parseInt(times[2]);
        }

        return hours * 3600L + minutes * 60L + seconds;
    }

    public static Long timeToTimestamp(int hours, int minutes, int seconds) {

        return hours * 3600L + minutes * 60L + seconds;
    }

    public static String timestampToTime(long timestamp, String template) {
        Date date = new Date(timestamp * 1000L);
        DateFormat df = new SimpleDateFormat(template, Locale.ITALY);
        df.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        df.setTimeZone(TimeZone.getDefault());
        return df.format(date);
    }

    public static Date stringToDate(String dateString, String template) {
        try {
            DateFormat format = new SimpleDateFormat(template, Locale.ITALIAN);
            return format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
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

    public static void latLngToAddress(final Context context, final LatLng latLng, final GeocoderCallback geocoderCallback) {
        new AsyncTask<Void, Void, Address>() {

            @Override
            protected Address doInBackground(Void... voids) {
                if (latLng == null) {
                    return null;
                } else {
                    Double latitude = latLng.latitude;
                    Double longitude = latLng.longitude;

                    try {
                        Geocoder geocoder = new Geocoder(context, Locale.ITALY);
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        if (addresses.size() > 0) {
                            return addresses.get(0);
                        } else {
                            return null;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }

            public void onPostExecute(Address address) {
                geocoderCallback.onFinish(address);
            }

        }.execute();
    }

    public static interface GeocoderCallback {
        void onFinish(Address address);
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
