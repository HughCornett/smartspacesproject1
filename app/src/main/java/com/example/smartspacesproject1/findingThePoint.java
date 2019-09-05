package com.example.smartspacesproject1;

import com.google.android.gms.maps.model.LatLng;

public class findingThePoint {


    public static LatLng find(LatLng last, LatLng current, int samples, int whichsample)
    {
        double lastLat, lastLng, currentLat, currentLng, resultLat, resultLng;

        lastLat = last.latitude;
        lastLng = last.longitude;
        currentLat = current.latitude;
        currentLng = current.longitude;


        resultLat = (currentLat-lastLat)*whichsample/samples;

        resultLat += lastLat;

        resultLng = (currentLng-lastLng)*whichsample/samples;

        resultLng += lastLng;

        return new LatLng(resultLat,resultLng);

    }




}
