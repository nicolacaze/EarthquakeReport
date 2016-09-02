package com.example.android.earthquakereport;

/**
 * This file defines the main class Quake that will be displayed as an ArrayList in the app. It will
 * store the main information about earthquakes, like place, magnitude or date.
 */
public class Quake {

    //Here are declared the states of the class. Magnitude, location and date.

    //We declare the magnitude as a decimal value.
    private double mMagnitude;

    //We declare the location as a String.
    private String mLocation;

    //We declare the date as a String.
    private  long mTime;

    //The associated url link to the website.
    private String mUrl;


    /**
     *  Define the public constructor of the class.
     *  @param mag for the given magnitude
     *  @param loc for the given location
     *  @param time for the given date
     */

    public Quake(double mag, String loc, long time, String url) {
        mMagnitude = mag;
        mLocation = loc;
        mTime = time;
        mUrl = url;
    }

    //Below are getter methods for the class.
    public double getQuakeMagnitude() {
        return mMagnitude;
    }

    public String getQuakeLocation() {
        return  mLocation;
    }

    public long getQuakeTime() {
        return mTime;
    }

    public String getQuakeUrl() {return mUrl;}

}

