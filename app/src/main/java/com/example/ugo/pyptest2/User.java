package com.example.ugo.pyptest2;

import android.location.Location;

import com.google.firebase.database.Exclude;

/**
 * Created by Ugo on 02/01/2017.
 */

public class User {
    private String username;
    private String email;
    private MyLocation lastLocation;
    private String uid;

    public User() {
    }

    public User(String uid, String username, String email) {
        this.uid = uid;
        this.username = username;
        this.email = email;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MyLocation getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(MyLocation lastLocation) {
        this.lastLocation = lastLocation;
    }
}
