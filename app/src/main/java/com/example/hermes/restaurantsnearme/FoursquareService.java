package com.example.hermes.restaurantsnearme;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Hermes on 22/06/2017.
 * This interface holds the parameters that we want from the JSON.
 */

public interface FoursquareService {
    @GET("venues/search")
    Call<Response> getAllVenues(@Query("intent") String intent,
                                @Query("radius") int radius,
                                @Query("ll") String location,
                                @Query("limit") int amountOfResult,
                                @Query("categoryId") String venueCategory,
                                @Query("client_id") String clientId,
                                @Query("client_secret") String clientSecret,
                                @Query("v") String date);
}
