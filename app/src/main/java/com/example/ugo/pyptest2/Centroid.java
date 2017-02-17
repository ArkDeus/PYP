package com.example.ugo.pyptest2;

/**
 * Created by user on 16/02/2017.
 */

public class Centroid {
    private Double latitude;
    private Double longitude;

    public Centroid(){}

    public Centroid(double lat, double lon){
        latitude=lat;
        longitude=lon;
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
