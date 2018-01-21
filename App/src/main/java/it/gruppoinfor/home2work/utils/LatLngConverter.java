package it.gruppoinfor.home2work.utils;

import android.arch.persistence.room.TypeConverter;


import java.util.Locale;

import it.gruppoinfor.home2workapi.model.LatLng;

public class LatLngConverter {

    @TypeConverter
    public static LatLng fromString(String stringLatLng) {
        String[] strings = stringLatLng.split(",");
        double lat = Double.parseDouble(strings[0]);
        double lng = Double.parseDouble(strings[1]);
        return new LatLng(lat, lng);
    }

    @TypeConverter
    public static String toString(LatLng latLng) {
        return String.format(Locale.ROOT, "%1$.8f,%2$.8f", latLng.getLat(), latLng.getLng());
    }
}
