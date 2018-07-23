package com.example.hermes.restaurantsnearme;

/**
 * Created by Hermes on 28/06/2017.
 *
 * This class is used to create the right structure for the body of the JSON that we are going
 * to request from the server.
 */

public class Response {
    private FoursquarePlaces response;

    public FoursquarePlaces getResponse() {
        return response;
    }

    public void setResponse(FoursquarePlaces response) {
        this.response = response;
    }
}
