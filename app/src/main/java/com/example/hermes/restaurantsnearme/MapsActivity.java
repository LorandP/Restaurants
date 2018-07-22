package com.example.hermes.restaurantsnearme;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

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
import com.google.android.gms.maps.model.MarkerOptions;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.functions.Action1;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Boolean mPopUpActive = false;
    private Retrofit retrofit;
    private FoursquareService foursquareService;
    private int venuesLatitude;
    private int venuesLongitude;
    private MarkerOptions venuesMarker = new MarkerOptions();
    private LatLng venuesLatLng;
    private Date currentDate;
    private String formatedDate;
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
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.foursquare.com/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        currentDate = new Date();
        formatedDate = new SimpleDateFormat("yyyyMMdd").format(currentDate);


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
      //  mMap.setMyLocationEnabled(true);

        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng(-34, 151);
        // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    /**
     * Here we re-connection to the GooglPlayServices after the activity was resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!mPopUpActive){
            mGoogleApiClient.disconnect();
            mPopUpActive = false;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        Intent intent = new Intent(this, MapsActivity.class);
        RxPermissions rxPermissions = new RxPermissions(this);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mPopUpActive = true;
        rxPermissions.request(Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(new Action1<Boolean>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            // Luam ultima locatie a utilizatorului de la clientul de API.
                            // Insa verificam daca aceasta locatie este null sau nu.
                            @SuppressLint("MissingPermission") Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                            if (location == null) {
                                /**
                                 * Here we call the requestLocationUpdates that we set in onCreate that listens
                                 * for location updates.
                                 */
                                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, pendingIntent);
                            } else {
                                handleNewLocation(location);
                            }
                        } else {
                            // Log.i(TAG,"Please enable location service in order to get your location.");
                        }
                    }
                });
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        // We receive our positions longitude coordonates
        final double currentLongitude = location.getLongitude();
        // We receive our positions latitude coordonates
        final double currentLatitude = location.getLatitude();
        // We create a variable that holds both our longitude and latitude coordonates of our
        // current position.
        venuesLatLng = new LatLng(currentLatitude, currentLongitude);

        // We create a marker that will have a title that will say Here I am!
        MarkerOptions options = new MarkerOptions()
                .position(venuesLatLng)
                .title("Here I am!");
        // We add our marker that we created
        mMap.addMarker(options);
        // Here we set the camera to zoom in on our location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(venuesLatLng,15));
        foursquareService = retrofit.create(FoursquareService.class);


       // venuesLatLng = new LatLng(venuesLatitude, venuesLongitude);
        double latitude = venuesLatLng.latitude;
        double precision = Math.pow(10, 6);
        double newLatitude = (precision*latitude)/precision;

        double longitude = venuesLatLng.longitude;
        double newLongitutde = (precision*longitude)/precision;

        venuesLatLng = new LatLng(latitude,longitude);

        String newLat = venuesLatLng.latitude+","+venuesLatLng.longitude;

        foursquareService.getAllVenues(Constants.FOURSQUARE_CLIENT_KEY,
                Constants.FOURSQUARE_CLIENT_SECRET, formatedDate, newLat).enqueue(new Callback<List<FoursquarePlaces>>() {
            @Override
            public void onResponse(Call<List<FoursquarePlaces>> call, Response<List<FoursquarePlaces>> response) {
                for (FoursquarePlaces place : response.body()) {
                    // I get the error her\
                    venuesLatitude = Integer.parseInt(place.getLatitude());
                    venuesLongitude = Integer.parseInt(place.getLongitude());
                    venuesMarker.position(venuesLatLng);
                    mMap.addMarker(venuesMarker);
                }
            }
            @Override
            public void onFailure(Call<List<FoursquarePlaces>> call, Throwable t) {

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

}
