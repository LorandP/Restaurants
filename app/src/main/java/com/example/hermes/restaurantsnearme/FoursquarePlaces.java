package com.example.hermes.restaurantsnearme;

import java.util.List;

/**
 * Created by Hermes on 28/06/2017.
 *
 * This class holds a List of FoursquarePlace type of object in order to create
 * the right structure that we will use to receive the body of the JSON.
 */

public class FoursquarePlaces {
    private List<FoursquarePlace> venues;

    public List<FoursquarePlace> getVenues() {
        return venues;
    }

    public void setVenues(List<FoursquarePlace> places) {
        this.venues = places;
    }
}
