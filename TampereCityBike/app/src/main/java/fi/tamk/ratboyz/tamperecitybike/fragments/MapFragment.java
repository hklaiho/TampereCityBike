package fi.tamk.ratboyz.tamperecitybike.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import fi.tamk.ratboyz.tamperecitybike.R;
import fi.tamk.ratboyz.tamperecitybike.utils.BikeParkDataFetcher;


public class MapFragment extends Fragment implements GoogleMap.OnMapClickListener, GoogleMap.OnCameraMoveStartedListener {

    private MapView mMapView;
    private GoogleMap map;
    private static final int REQUEST_CODE_LOCATION = 1;
    private FloatingActionButton fabLocation;
    private GoogleApiClient mGoogleApiClient;

    public static final String TAG = "fi.tamk.ratboyz.tamperecitybike.MapFragment";

    /**
     * Is the preferred way of programmatically creating a MapFragment object.
     * <p>
     * Adds necessary bundle arguments upon fragment creation. As of now
     * none are needed. Constructor is present for future proofing reasons.
     *
     * @return A new MapFragment object.
     */
    public static MapFragment newInstance() {
        return new MapFragment();
    }

    // Required empty constructor
    public MapFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout
        View rootView = inflater.inflate(R.layout.content_map_fragment, container, false);

        // Establish a connection to google services if no connection exists,
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(LocationServices.API)
                    .build();
        }

        // Find a reference to map and display it immediately.
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        // Find and assign functions to floating action buttons.
        fabLocation = (FloatingActionButton) rootView.findViewById(R.id.floating_action_button_location);
        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panToCurrentLocation();
            }
        });

        // Fetch the map.
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                map = mMap;
                // Disable unwanted stock UI.
                map.getUiSettings().setRotateGesturesEnabled(true);
                map.getUiSettings().setMapToolbarEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                // Show users current position if permitted.
                if (!(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                    map.setMyLocationEnabled(true);
                }
                // Padding has to be added if the status bar is translucent.
                if (isTranslucentStatusBar()) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
                        adjustMapPadding(map);
                }
                // Fetch default map position. Some gymnastics are required as
                // doubles are not directly supported in the xml resources.
                Resources res = getResources();
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
                // OnMapClickListener and OnCameraMoveStartedListener
                // for hiding and showing floating action buttons.
                map.setOnMapClickListener(MapFragment.this);
                map.setOnCameraMoveStartedListener(MapFragment.this);
                new BikeParkDataFetcher(map).execute();
            }
        });

        return rootView;
    }

    /**
     * Adds padding to GoogleMap equal to that of the height of the status bar.
     *
     * @param map Map whose padding needs adjusting.
     */
    private void adjustMapPadding(GoogleMap map) {
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) statusBarHeight = getResources().getDimensionPixelSize(resourceId);

        map.setPadding(
                0                   // Left
                , statusBarHeight   // Top
                , 0                 // Right
                , 0                 // Bottom
        );
    }

    /**
     * Checks if translucent status bar is enabled.
     * <p>
     * Not tested on low API level devices.
     *
     * @return Is the status bar translucent.
     */
    protected boolean isTranslucentStatusBar() {
        if (isAdded()) {
            Window w = getActivity().getWindow();
            WindowManager.LayoutParams lp = w.getAttributes();
            int flags = lp.flags;
            // Compare Translucent Status Bar with flags in the window
            return ((flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }


    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // TODO Disable click based menu controls when InfoWindow is shown.
        if (fabLocation.isShown()) {
            fabLocation.hide();
        } else {
            fabLocation.show();
        }
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        // Hide controls if user panned or zoomed the map.
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            if (fabLocation.isShown()) {
                fabLocation.hide();
            }
        }
    }

    /**
     * Attempts to zoom the map on the user's last known location.
     */
    public void panToCurrentLocation() {

    }

    /**
     * Checks whether or not location services are enabled.
     *
     * @return Is the device able to give a location of any accuracy.
     */
    private boolean locationServiceIsEnabled() {
        int locationMode = 0;
        String locationProviders;
        boolean isAvailable = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            isAvailable = (locationMode != Settings.Secure.LOCATION_MODE_OFF);

            // Backwards compatibility, disregard deprecated errors.
        } else {
            locationProviders = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            isAvailable = !TextUtils.isEmpty(locationProviders);
        }

        boolean coarsePermissionCheck = (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        boolean finePermissionCheck = (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);

        return isAvailable && (coarsePermissionCheck || finePermissionCheck);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_LOCATION:
                if (permissions.length == 1 &&
                        permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (isAdded() && ContextCompat.checkSelfPermission(getActivity()
                            , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // If the user permits, focus on current location.
                        panToCurrentLocation();
                    } else {
                        // Inform the user that location data could not be used.
                        if (isAdded())
                            Toast.makeText(getActivity(), getString(R.string.message_location_info_permission_denied), Toast.LENGTH_SHORT).show();
                    }
                }

                break;
        }
    }
}
