package com.example.hp.httpfetching;

/**
 * Created by HP on 10/13/2017.
 */

public class MyInformation {

    private String lattitude,longitude;

    public MyInformation(String lattitude, String longitude) {
        this.lattitude = lattitude;
        this.longitude = longitude;
    }
    public String getLattitude() {
        return lattitude;
    }

    public String getLongitude() {
        return longitude;
    }



    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


}
