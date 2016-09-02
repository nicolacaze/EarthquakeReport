package com.example.android.earthquakereport;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.earthquakereport.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class QuakeAdapter extends ArrayAdapter<Quake> {

    private static final String LOCATION_SEPARATOR = "of";

    //Define public constructor
    public QuakeAdapter(Activity context, ArrayList<Quake> earthquakes) {
        super(context, 0, earthquakes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.quake_layout, parent, false);
        }

        //Get the current item position
        final Quake currentQuake = getItem(position);

        //Set the accurate formatted magnitude to the TextView layout.
        TextView magnitude = (TextView) listItemView.findViewById(R.id.magnitude);
        String formattedMagnitude = formatMagnitude(currentQuake.getQuakeMagnitude());
        magnitude.setText(formattedMagnitude);

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitude.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(currentQuake.getQuakeMagnitude());

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

        //Get the current Quake location full String.
        String originalLocation = currentQuake.getQuakeLocation();

        //Select the accurate location offset from the corresponding TextView.
        TextView locationOffset = (TextView) listItemView.findViewById(R.id.location_offset);

        //Select the accurate primary location from the corresponding TextView.
        TextView primaryLocation = (TextView) listItemView.findViewById(R.id.primary_location);

        if(originalLocation.contains(LOCATION_SEPARATOR)) {
            //Set the first half of the location to locationOffset TextView.
            locationOffset.setText(getFirstHalf(originalLocation));

            //Set second half of the location to primaryLocation.
            primaryLocation.setText(getSecondHalf(originalLocation));

        } else {

            locationOffset.setText("Near the");
            primaryLocation.setText(originalLocation);
        }


        //Create a new Date object from the time in milliseconds.
        Date dateObject = new Date(currentQuake.getQuakeTime());

        //Find the accurate TextView in the earthquake_activity layout.
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);

        //Format the date String.
        String formattedDate = formatDate(dateObject);

        //Set the new formattedDate to the date TextView.
        dateView.setText(formattedDate);

        //Find the accurate TextView in the earthquake_activity layout.
        TextView timeView = (TextView) listItemView.findViewById(R.id.time);

        //Format the date String.
        String formattedTime = formatTime(dateObject);

        //Set the new formattedDate to the date TextView.
        timeView.setText(formattedTime);

        return listItemView;
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    /** These two helper methods, take an String as input and return separately, the first half and
     * second half.
     * @param input
     * @return firstHalf, secondHalf
     */
    private String getFirstHalf(String input) {
        String firstHalf;
        int charIndex = input.indexOf(LOCATION_SEPARATOR);
        firstHalf = input.substring(0, charIndex + 2);
        return firstHalf;
    }

    private String getSecondHalf(String input) {
        String secondHalf;
        int charIndex = input.indexOf(LOCATION_SEPARATOR);
        secondHalf = input.substring(charIndex + 3);
        return  secondHalf;
    }

    //Help formatting the double magnitude into a String of one decimal.
    private String formatMagnitude(double magnitude) {
        DecimalFormat formatter = new DecimalFormat("0.0");
        return formatter.format(magnitude);
    }

    //Help defining the correct color background for the magnitude background.
    private int getMagnitudeColor(double mag) {
        int magnitudeColorResourceId;
        int y = (int)Math.floor(mag);
        switch(y) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }
}