package com.example.android.earthquakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by nicolaslacaze on 03/09/16.
 */
public final class Utils {

    //Log tag defined below for future Log to console.
    private static final String LOG_TAG = Utils.class.getName();


    public static ArrayList<Quake> fetchEarthquakeData(String requestUrl) {

        URL url = createUrl(requestUrl);

        String JsonResponse = "";

        try {
            JsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the http request to USGS API", e);
        }

        return extractDataFromJson(JsonResponse);
    }

    private static URL createUrl(String requestUrl) {
        URL url;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error while creating the URL", e);
            return null;
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        //Stores server code response.
        int httpResponse;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            //Get USGS server response code.
            httpResponse = urlConnection.getResponseCode();

            if (httpResponse == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "HTTP response code is" + httpResponse);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error making the http request", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();

            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static ArrayList<Quake> extractDataFromJson(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Quake> earthquakes = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // build up a list of Earthquake objects with the corresponding data.

            JSONObject baseJsonResponse = new JSONObject(jsonString);
            JSONArray features = baseJsonResponse.getJSONArray("features");
            for (int i = 0; i < features.length(); i++) {
                JSONObject earthquake = features.getJSONObject(i);
                JSONObject properties = earthquake.getJSONObject("properties");

                double magnitude = properties.getDouble("mag");
                String place = properties.optString("place");
                long time = properties.getLong("time");
                String url = properties.getString("url");

                Quake quake = new Quake(magnitude, place, time, url);
                earthquakes.add(quake);

            }
            // Return the list of earthquakes
            return earthquakes;

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }
        return null;
    }
}
