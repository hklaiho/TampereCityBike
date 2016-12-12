package fi.tamk.ratboyz.tamperecitybike.utils;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import fi.tamk.ratboyz.tamperecitybike.R;


public class MapHelper {

    public static void setMapControls(GoogleMap map) {
        map.getUiSettings().setRotateGesturesEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(true);
    }

    public static void centerOnLastKnownPos(GoogleMap map, Resources res) {
        // Fetch default map position. Some gymnastics are required as
        // doubles are not directly supported in the xml resources.
        TypedValue typedLat = new TypedValue();
        TypedValue typedLng = new TypedValue();
        res.getValue(R.dimen.map_default_lat, typedLat, true);
        res.getValue(R.dimen.map_default_lng, typedLng, true);
        double lat = typedLat.getFloat();
        double lng = typedLng.getFloat();
        int zoom = res.getInteger(R.integer.map_default_zoom);
        LatLng position = new LatLng(lat, lng);
        // Move camera to default position.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoom));
    }
}
