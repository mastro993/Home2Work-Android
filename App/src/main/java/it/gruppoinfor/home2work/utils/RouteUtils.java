package it.gruppoinfor.home2work.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;


public class RouteUtils {

    public static LatLngBounds getRouteBounds(List<LatLng> locations) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (LatLng location : locations) {
            builder.include(location);
        }

        return builder.build();
    }
}
