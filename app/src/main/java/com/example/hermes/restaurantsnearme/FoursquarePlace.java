package com.example.hermes.restaurantsnearme;


/**
 * Created by Hermes on 22/06/2017.
 * This class hold the parameters that we want to receive from the foursquare API.
 */

public class FoursquarePlace {
    private String name;
    private String category;
    private Location location;

    public FoursquarePlace() {
        this.name = "";
        this.setCategory("");
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
