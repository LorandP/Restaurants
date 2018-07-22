package com.example.hermes.restaurantsnearme;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hermes on 21/06/2017.
 */

public class NearbyPlaces extends ListActivity {
    private ArrayList myVenueList;
    private ArrayAdapter myAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_nearby_places);

        new Foursquare().execute();
    }
    private class Foursquare extends AsyncTask{

        String temp;
        @Override
        protected Object doInBackground(Object[] params) {
            temp = makeCall("https://api.foursquare.com/v2/venues/search?client_id=");

            return "";
        }

        @Override
        protected void onPreExecute() {
            // We can create a progress bar here
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (temp == null){
                // We have an error to call
            }else{
                // everyhing went right
                // parseFoursquare venues searc result
              //  myVenueList = (ArrayList)parseFoursuare(temp);
                List listTitle = new ArrayList<>();
                for (int index = 0; index <myVenueList.size();index++){
                    //make a list of the venus that are loaded in the list
                    //show the name, the category and the city
                    listTitle.add(index, myVenueList.get(index)+", "+myVenueList);
                }
                //set the results to the list
                //and show them in the xml
                myAdapter = new ArrayAdapter(NearbyPlaces.this, R.layout.list_of_nearby_places, listTitle);
                setListAdapter(myAdapter);
            }
        }
        public String makeCall(String url){
            return "";
        }
    }

}
