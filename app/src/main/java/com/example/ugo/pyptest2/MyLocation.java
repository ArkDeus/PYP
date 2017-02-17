package com.example.ugo.pyptest2;

import android.location.Location;

/**
 * Created by Ugo on 03/01/2017.
 */

public class MyLocation {

    private Double latitude;
    private Double longitude;

    //Default constructor for Firebase
    public MyLocation() {}

    public MyLocation(Location location) {
        this.latitude =  location.getLatitude();
        this.longitude =  location.getLongitude();
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

}
