package com.example.hermes.restaurantsnearme;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.functions.Action1;


public class MapsActivity extends FragmentActivity implements
        VenueInfoPage.UpdateTextViewsListener,
        OnMapReadyCallback,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Boolean mPopUpActive = false;
    private Retrofit mRetrofit;
    private double mVenuesLatitude;
    private double mVenuesLongitude;
    private String mVenueTitle;
    private String mVenueCity;
    private String mVenueAddress;
    private String mVenueCountry;
    private HashMap<String, HashMap> mMarkerInfo = new HashMap<String, HashMap>();
    private HashMap<String, String> dataForASpecificVenue;
    private FragmentTransaction mFragmentTransaction;
    private FrameLayout mFrameLayout;
    private VenueInfoPage mVenueInfoPage;
    private AnimatorSet mAnimatorSet = new AnimatorSet();

    private ObjectAnimator mAnimator;
    private int mMapPinSelectedCounter = 0;
    private String address = "";
    private String city = "";
    private String country = "";

    /**
     * We set a code to start another Activity in case of a connection failure.
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFrameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        mFrameLayout.setVisibility(View.GONE);

        try {
            // For building the client object we use the Builder pattern.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    // Here we tel the API that the MapsActivity will handle connection stuff
                    // because this class now implements the appropriate interfaces
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    // Here we add the LocationServices to the API
                    .addApi(LocationServices.API)
                    // We build the client
                    .build();
        } catch (RuntimeException e) {
            Log.d(TAG, e.getMessage());
        }

        try {
            // Create the LocationRequest object
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    // 10 seconds, in milliseconds
                    /**
                     * The Interval, represents the interval between requests for the users location
                     * For a high accuracy location, the interval should be low, but that will consme
                     * more battery.
                     * Also, depending on the sources available, you may or may not receive your location
                     * at that interval.
                     */
                    .setInterval(10 * 1000)
                    // 1 second, in milliseconds
                    /**
                     * Here we can set this lower because, there may be other applications as well that
                     * receiving location updates so we can listen from them. And that doesn't cost any
                     * extra power.
                     */
                    .setFastestInterval(1 * 1000);
        } catch (RuntimeException e) {
            Log.d(TAG, e.getMessage());
        }

        try {
            mRetrofit = new Retrofit.Builder()
                    .baseUrl("https://api.foursquare.com/v2/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        } catch (RuntimeException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /**
         * When the user clicks on the info popup above every venue's maker, we are are
         * starting a new fragment that will display information about that venue.
         */
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                HashMap<String, String> markerVenuesData = mMarkerInfo.get(marker.getId());
                Log.d(TAG, marker.getId());
                Log.d(TAG, markerVenuesData.get(Constants.VENUE_TITLE));
                setmVenueTitle(markerVenuesData.get(Constants.VENUE_TITLE));
                setmVenueAddress(markerVenuesData.get(Constants.VENUE_ADDRESS));
                setmVenueCity(markerVenuesData.get(Constants.VENUE_CITY));
                setmVenueCountry(markerVenuesData.get(Constants.VENUE_COUNTRY));

               // if (mVenueInfoPage == null) {
                    mVenueInfoPage = new VenueInfoPage();
                //}

                mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                mFragmentTransaction
                        .replace(R.id.frame_layout, mVenueInfoPage)
                        // commitNow commits the transaction synchroniously when commit will not
                        // commit the transaction immediately and instead it will schedule it.
                        .commitNow();
                //mVenueInfoPage.refreshTextViews();
                mFrameLayout.setVisibility(View.VISIBLE);

                if (mMapPinSelectedCounter == 0) {
                    mAnimator = ObjectAnimator.ofFloat(mFrameLayout,
                            "translationY", 600f, 0);
                    mAnimator.setDuration(300);
                    mAnimatorSet.play(mAnimator);
                    mAnimatorSet.start();
                }
                mMapPinSelectedCounter++;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mAnimator = ObjectAnimator.ofFloat(mFrameLayout,
                        "translationY", -0f, 600f);
                mAnimator.setDuration(300);
                mAnimatorSet.play(mAnimator);
                mAnimatorSet.start();
                mMapPinSelectedCounter = 0;
            }
        });
    }

    /**
     * Here we reconnection to the GooglePlayServices after the activity was resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        try {
            mGoogleApiClient.connect();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!mPopUpActive) {
            mGoogleApiClient.disconnect();
            mPopUpActive = false;
        }
    }

    /**
     * In this method we are request permission to the user to receive it's current location.
     * If we are allowed to receive the current location of the user, then we store it and
     * also display it on the map.
     *
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        Intent intent = new Intent(this, MapsActivity.class);
        RxPermissions rxPermissions = new RxPermissions(this);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mPopUpActive = true;
        try {
            rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            if (aBoolean) {
                                // Luam ultima locatie a utilizatorului de la clientul de API.
                                // Insa verificam daca aceasta locatie este null sau nu.
                                Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                                if (location == null) {
                                    /**
                                     * Here we call the requestLocationUpdates that we set in onCreate that listens
                                     * for location updates.
                                     */
                                    LocationServices.FusedLocationApi
                                            .requestLocationUpdates(mGoogleApiClient, mLocationRequest, pendingIntent);
                                } else {
                                    handleNewLocation(location);
                                }
                            } else {
                                 Log.i(TAG,"Please enable location service in order to get your location.");
                            }
                        }
                    });
        } catch (IllegalStateException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    /**
     * We receive and display the current location of the user after which we place a marker on
     * the users location.
     * Also, using Foursquare API, here we make a request to the server to receive nearby
     * venues and also place a marker on their location.
     *
     * @param location the last location of the user.
     */
    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        Geocoder geocoder;
        List<android.location.Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        // We receive our positions longitude coordonates
        final double currentLongitude = location.getLongitude();
        // We receive our positions latitude coordonates
        final double currentLatitude = location.getLatitude();
        Log.d(TAG, currentLatitude +" "+currentLongitude);
        // We create a variable that holds both our longitude and latitude coordonates of our
        // current position.
        try {
            addresses = geocoder.getFromLocation(currentLatitude, currentLongitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        address = addresses.get(0).getAddressLine(0);
        city = addresses.get(0).getLocality();
        country = addresses.get(0).getCountryName();

        dataForASpecificVenue = new HashMap<String, String>();
        dataForASpecificVenue.put(Constants.VENUE_TITLE, Constants.USER_LOCATION_TITLE);
        dataForASpecificVenue.put(Constants.VENUE_ADDRESS, address);
        dataForASpecificVenue.put(Constants.VENUE_CITY, city);
        dataForASpecificVenue.put(Constants.VENUE_COUNTRY, country);

        LatLng userCurrentPosition = new LatLng(currentLatitude, currentLongitude);
        // We create a marker that will have a title that will say Here I am!
        Marker options = mMap.addMarker(new MarkerOptions()
                .position(userCurrentPosition)
                .title(Constants.USER_LOCATION_TITLE));

        // Here we set the camera to zoom in on our location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userCurrentPosition, 15));
        mMarkerInfo.put(options.getId(), dataForASpecificVenue);

        FoursquareService foursquareService = mRetrofit.create(FoursquareService.class);

        String newLatitude = Double.toString(userCurrentPosition.latitude);
        String newLongitude = Double.toString(userCurrentPosition.longitude);

        //Here I add my current location longitude and latitude in a string variable
        String userLocation = newLatitude + "," + newLongitude;
        Date currentDate = new Date();
        String formatedDate = new SimpleDateFormat("yyyyMMdd").format(currentDate);


        foursquareService.getAllVenues(
                "browse",
                Constants.RADIUS,
                userLocation,
                Constants.LIMIT_RESULTS,
                Constants.FOOD_CATEGORY,
                Constants.FOURSQUARE_CLIENT_KEY,
                Constants.FOURSQUARE_CLIENT_SECRET, formatedDate)
                .enqueue(new Callback<com.example.hermes.restaurantsnearme.Response>() {
                    @Override
                    public void onResponse(@NonNull Call<com.example.hermes.restaurantsnearme.Response> call,
                                           @NonNull Response<com.example.hermes.restaurantsnearme.Response> response) {
                /* If the response.code is 400 or 404 then we display a message in the Log cat
                     * with the respective error code.
                     * */
                        if (response.code() == 400 || response.code() == 404) {
                            try {
                                if (response.errorBody().string() != null) {
                                    Log.d(TAG, "Error body: " + response.errorBody().string());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            // Iterating trough the response body which is the JSON that we requested
                            // and we add a marker at the position of the venues that we received.
                            for (FoursquarePlace place : response.body().getResponse().getVenues()) {
                                mVenuesLatitude = place.getLocation().getLat();
                                mVenuesLongitude = place.getLocation().getLng();
                                LatLng venuesLatLng = new LatLng(mVenuesLatitude, mVenuesLongitude);

                                Marker mVenuesMarker = mMap.addMarker(new MarkerOptions()
                                        .position(venuesLatLng)
                                        .title(place.getName()));

                                dataForASpecificVenue = new HashMap<String, String>();
                                dataForASpecificVenue.put(Constants.VENUE_TITLE, place.getName());
                                dataForASpecificVenue.put(Constants.VENUE_ADDRESS, place.getLocation().getStreetAddress());
                                dataForASpecificVenue.put(Constants.VENUE_CITY, place.getLocation().getCity());
                                dataForASpecificVenue.put(Constants.VENUE_COUNTRY, place.getLocation().getCountry());

                                mMarkerInfo.put(mVenuesMarker.getId(), dataForASpecificVenue);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<com.example.hermes.restaurantsnearme.Response> call, Throwable t) {
                        Log.d(TAG, t.toString());
                    }
                });
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            // Start an Activity that tries to resolve the error
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }

    }

    /**
     * This method is called every time the user changes it's location.
     *
     * @param location the current location of the user.
     */
    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    public String getmVenueTitle() {
        return mVenueTitle;
    }

    public void setmVenueTitle(String mVenueTitle) {
        this.mVenueTitle = mVenueTitle;
    }

    public String getmVenueCity() {
        return mVenueCity;
    }

    public void setmVenueCity(String mVenueCity) {
        this.mVenueCity = mVenueCity;
    }

    public String getmVenueAddress() {
        return mVenueAddress;
    }

    public void setmVenueAddress(String mVenueAddress) {
        this.mVenueAddress = mVenueAddress;
    }

    public String getmVenueCountry() {
        return mVenueCountry;
    }

    public void setmVenueCountry(String mVenueCountry) {
        this.mVenueCountry = mVenueCountry;
    }

    @Override
    public void setTextForTextViews(TextView venueTitle, TextView venueAddress, TextView venueCity, TextView venueCountry) {
        venueTitle.setText(getmVenueTitle());
        venueAddress.setText(getmVenueAddress());
        venueCity.setText(getmVenueCity());
        venueCountry.setText(getmVenueCountry());
    }

}
