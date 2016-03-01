package com.haraj.mersal.cobalocation;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Created by riandyrn on 3/1/16.
 */
public class MainPresenter implements OnLocationChangedCallback {

    private final static int minTime = 0; // 10 seconds
    private final static int minDistance = 0; // 1 meter
    private final static int REQUEST_CODE = 0;

    private final static String SETUP_LOCATION_LISTENER = "SETUP_LOCATION_LISTENER";
    private final static String REMOVE_LOCATION_LISTENER = "REMOVE_LOCATION_LISTENER";

    private MainActivity activity;

    private LocationModel locationModel;
    private LocationManager locationManager;
    private String provider;

    private String currentState;

    public MainPresenter(MainActivity activity)
    {
        this.activity = activity;
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        locationModel = new LocationModel(this);

        provider = locationManager.GPS_PROVIDER;
    }

    public void setupLocationListener()
    {
        currentState = SETUP_LOCATION_LISTENER;

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        /*
        String provider = locationManager.getBestProvider(criteria, true); // ini untuk provider otomatis
        Log.d("CobaLocation", "Provider: " + provider);
        */

        if(checkPermission()) {
            locationManager.requestLocationUpdates(provider, minTime, minDistance, locationModel);

            if(locationManager.isProviderEnabled(provider))
                initializeText();
        } else
            handleNoPermission();
    }

    public void removeLocationListener()
    {
        currentState = REMOVE_LOCATION_LISTENER;

        if(checkPermission()) {
            locationManager.removeUpdates(locationModel);
            activity.setNotListeningText();
        } else
            handleNoPermission();
    }

    public void handleRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch(requestCode)
        {
            case REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    if(currentState.equals(SETUP_LOCATION_LISTENER)){
                        setupLocationListener();
                    } else if(currentState.equals(REMOVE_LOCATION_LISTENER)){
                        removeLocationListener();
                    }
                } else {
                    handleNoPermission();
                }
        }
    }

    private boolean checkPermission()
    {
        return (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    private void handleNoPermission()
    {
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)){

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE);

        } else {
            showDialog("Permission Required", "The app require access to your location", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showAppSetting();
                }
            });
        }
    }

    private void showAppSetting()
    {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    private void showLocationSetting()
    {
        activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    private void showDialog(String title, String message, DialogInterface.OnClickListener listener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", listener);

        builder.create().show();
    }

    @Override
    public void getLocation(Location location) {
        Log.d("CobaLocation", "latitude: " + location.getLatitude() + ", longitude: " + location.getLongitude());
        activity.setLocation(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void isProviderEnabled(String provider, boolean isEnabled) {
        if(!isEnabled) {
            activity.setNotListeningText();
            showDialog(provider.toUpperCase() + " needs to be enabled", "Please enable your " + provider.toUpperCase(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showLocationSetting();
                }
            });
        } else {
            initializeText();
        }
    }

    private void initializeText()
    {
        if(checkPermission()) {
            activity.setListeningText();
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null)
                activity.setLocation(location.getLatitude(), location.getLongitude());
        }
    }

}
