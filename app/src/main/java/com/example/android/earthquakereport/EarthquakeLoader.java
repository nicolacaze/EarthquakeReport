package com.example.android.earthquakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * + * Loads a list of earthquakes by using an AsyncTask to perform the
 * + * network request to the given URL.
 * +
 */
public class EarthquakeLoader extends AsyncTaskLoader<List<Quake>> {

    /**
     * Tag for log messages
     */
    private final String LOG_TAG = EarthquakeLoader.class.getName();

    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link EarthquakeLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public EarthquakeLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Quake> loadInBackground() {

        if (mUrl == null) {
            return null;
        }

        List<Quake> earthquakes = Utils.fetchEarthquakeData(mUrl);
        return earthquakes;
    }
}
