package it.gruppoinfor.home2work;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

import it.gruppoinfor.home2work.models.Route;
import it.gruppoinfor.home2work.models.RoutePoint;


public class RouteUtils {

    public static ArrayList<LatLng> getWayPoints(Route route, final int max) {
        return getWayPoints(route.getPoints(), max);
    }

    public static ArrayList<LatLng> getWayPoints(List<RoutePoint> points, final int max) {

        ArrayList<LatLng> locations = new ArrayList<>();
        ArrayList<LatLng> waypoints = new ArrayList<>();

        // Trasformo in array di LatLng
        for (RoutePoint point : points)
            locations.add(point.getLatLng());

        LatLng start = locations.get(0); // Inizio percorso
        LatLng end = locations.get(points.size() - 1); // Fine percorso

        int step = getStep(locations.size(), max);

        waypoints.add(start);
        for (int i = step + 1; i < locations.size() - (step + 1); i += step) {
            waypoints.add(locations.get(i));
        }
        waypoints.add(end);

        return waypoints;

    }

    private static int getStep(final int size, final int MAX) {

        int step = size / MAX;
        int rest = size % MAX;

        return rest > 0 ? step + 1 : step;

    }

    public static LatLngBounds getRouteBounds(Route route) {
        List<LatLng> latLngs = new ArrayList<>();
        for (RoutePoint point : route.getPoints())
            latLngs.add(point.getLatLng());
        return getRouteBounds(latLngs);
    }

    public static LatLngBounds getRouteBounds(List<LatLng> locations) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (LatLng location : locations) {
            builder.include(location);
        }

        return builder.build();
    }
}
