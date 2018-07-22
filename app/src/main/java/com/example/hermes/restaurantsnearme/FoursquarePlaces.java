package com.example.hermes.restaurantsnearme;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Hermes on 22/06/2017.
 * This class hold the parameters that we want to receive from the foursquare API.
 */

public class FoursquarePlaces {
    private String name;
    private String city;
    private String category;
    private String longitude;
    private String latitude;
    private String address;

    public FoursquarePlaces() {
        this.name = "";
        this.city = "";
        this.setCategory("");
    }

    public void setCity(String city){
        if (city != null){
            this.city = city.replaceAll("\\(", "").replaceAll("\\)", "");
        }
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
