package com.example.ugo.pyptest2;

/**
 * Created by user on 16/02/2017.
 */

public class RDV {
    private String name;
    private String date;
    private String time;
    private Double latitude;
    private Double longitude;
    private String creator;

    public RDV(String name, String date, String time, Double latitude, Double longitude, String creator) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.creator = creator;
    }

    public RDV(){}

    public String getDate() {

        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
