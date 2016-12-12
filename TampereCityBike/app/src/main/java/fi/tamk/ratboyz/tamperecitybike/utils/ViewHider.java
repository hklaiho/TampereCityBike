package fi.tamk.ratboyz.tamperecitybike.utils;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import fi.tamk.ratboyz.tamperecitybike.R;
import fi.tamk.ratboyz.tamperecitybike.activities.MapActivity;


public class ViewHider implements GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnMapClickListener {

    private ActionBar appBar;
    private FloatingActionButton fab;
    private BottomSheetUtility utility;

    public ViewHider(MapActivity host, BottomSheetUtility utility) {
        this.appBar = host.getSupportActionBar();
        this.fab = (FloatingActionButton) host.findViewById(R.id.floating_action_button_location);
        this.utility = utility;
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            if (appBar.isShowing()) {
                appBar.hide();
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (appBar.isShowing()) {
            appBar.hide();
        } else {
            appBar.show();
        }
    }

    public void onMapLongClick(LatLng latLng) {
        fab.show();
        utility.hideBottomSheet();
    }
}
