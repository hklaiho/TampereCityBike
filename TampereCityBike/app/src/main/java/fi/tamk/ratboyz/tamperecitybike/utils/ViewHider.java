package fi.tamk.ratboyz.tamperecitybike.utils;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import fi.tamk.ratboyz.tamperecitybike.R;


public class ViewHider implements GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnMapClickListener {

    private static final boolean ANIMATE = true;

    FloatingActionButton location;
    ActionBar appBar;

    public ViewHider(AppCompatActivity host) {
        location = (FloatingActionButton) host.findViewById(R.id.floating_action_button_location);
        appBar = host.getSupportActionBar();
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            if (location.isShown()) {
                location.hide();
                appBar.hide();
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (location.isShown()) {
            location.hide();
            appBar.hide();
        } else {
            location.show();
            appBar.show();
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }
}
