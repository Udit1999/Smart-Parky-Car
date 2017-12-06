package com.example.hp.httpfetching;

/**
 * Created by HP on 17-Oct-17.
 */

public class SDistance
{
    String distance="",bearing="";

    public SDistance(String distance,String bearing)
    {
        this.distance = distance;
        this.bearing=bearing;
    }

    public String getDistance() {
        return distance;
    }

    public String getBearing() {
        return bearing;
    }
}
