package com.haraj.mersal.cobalocation;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by riandyrn on 3/1/16.
 */
public class LocationModel
        implements LocationListener {

    private OnLocationChangedCallback callback;

    public LocationModel(OnLocationChangedCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onLocationChanged(Location location) {
        callback.getLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        callback.isProviderEnabled(provider, true);
    }

    @Override
    public void onProviderDisabled(String provider) {
        callback.isProviderEnabled(provider, false);
    }
}
