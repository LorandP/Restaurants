package com.example.hermes.restaurantsnearme;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Hermes on 22/06/2017.
 * This interface holds the parameters that we want from the JSON.
 */

public interface FoursquareService {
    @GET("venues/search")
    Call<List<FoursquarePlaces>> getAllVenues(@Query("client_id") String clientId,
                                              @Query("client_secret") String clientSecret,
                                              @Query("v") String date,
                                              @Query("ll") String longitudeLatitude);
}
