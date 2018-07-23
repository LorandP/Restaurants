package com.example.hermes.restaurantsnearme;

/**
 * Created by Hermes on 27/06/2017.
 *
 * This class holds the parameters of a location.
 */

public class Location {
    private String address;
    private String crossStreet;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private double lat;
    private double lng;
    private double distance;

    public String getStreetAddress() {
        return address;
    }

    public void setStreetAddress(String streetAddress) {
        this.address = streetAddress;
    }

    public String getCrossStreet() {
        return crossStreet;
    }

    public void setCrossStreet(String crossStreet) {
        this.crossStreet = crossStreet;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        if (city != null){
            this.city = city.replaceAll("\\(", "").replaceAll("\\)", "");
        }
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
