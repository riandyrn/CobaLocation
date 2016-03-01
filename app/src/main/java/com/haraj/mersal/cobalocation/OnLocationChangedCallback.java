package com.haraj.mersal.cobalocation;

import android.location.Location;

/**
 * Created by riandyrn on 3/1/16.
 */
public interface OnLocationChangedCallback {

    void getLocation(Location location);
    void isProviderEnabled(String provider, boolean isEnabled);
}
