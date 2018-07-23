package com.example.hermes.restaurantsnearme;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
/**
 * Created by Hermes on 30/06/2017.
 * <p>
 * This fragment will display all the information associated with a venue.
 */

public class VenueInfoPage extends Fragment {
    UpdateTextViewsListener mSetTextForTextView;
    private TextView venueAddress;
    private TextView venueTitle;
    private TextView venueCity;
    private TextView venueCountry;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    interface UpdateTextViewsListener {
        void setTextForTextViews(TextView venueTitle, TextView venueAddress, TextView venueCity,
                                 TextView venueCountry);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mSetTextForTextView = (UpdateTextViewsListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement SettingTheTextForTheTextViews");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.venu_info_page, container, false);

        venueAddress = (TextView) viewGroup.findViewById(R.id.venue_address);
        venueTitle = (TextView) viewGroup.findViewById(R.id.venue_title);
        venueCity = (TextView) viewGroup.findViewById(R.id.venue_city);
        venueCountry = (TextView) viewGroup.findViewById(R.id.venue_country);

        mSetTextForTextView.setTextForTextViews(venueTitle, venueAddress, venueCity, venueCountry);
        return viewGroup;
    }

    /*
    * This method is used to run the setTextForTextViews method which will update the textView's
    * text with the newly selected venue details because we create only one instance of this fragment
     * that we want to update instead of creating a new one all the time we show it in frame layout.
    * */

    public void refreshTextViews(){
        mSetTextForTextView.setTextForTextViews(venueTitle, venueAddress, venueCity, venueCountry);
    }
}
