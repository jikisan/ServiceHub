package com.example.servicehub;

import com.google.android.gms.maps.model.LatLng;

public class CurrentLocation {

    LatLng currentLocation;

    public CurrentLocation() {
    }

    public CurrentLocation(LatLng currentLocation) {
        this.currentLocation = currentLocation;
    }

    public LatLng getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LatLng currentLocation) {
        this.currentLocation = currentLocation;
    }
}
