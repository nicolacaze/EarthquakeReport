package com.example.android.earthquakereport;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.earthquakereport.R;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EarthquakeActivity extends AppCompatActivity {

    private static final String LOG_TAG = EarthquakeActivity.class.getName();

    String USGSRequestUrl = "http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=" + getDate();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake);

        TextView updateCall = (TextView) findViewById(R.id.update);
        updateCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuakeAsyncTask task = new QuakeAsyncTask();
                task.execute();
            }
        });
    }

    private void updateUi(ArrayList<Quake> earthquakes) {

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // Create a new {@link ArrayAdapter} of earthquakes
        final QuakeAdapter adapter = new QuakeAdapter(this, earthquakes);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(adapter);

        //Set OnClickListener on the ListView.
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                Quake currentQuake = adapter.getItem(position);

                //Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentQuake.getQuakeUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
    }

    //This method returns the actual date to format properly the server request for data.
    private String getDate() {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        dateFormatter.setLenient(false);
        Date today = new Date();
        return dateFormatter.format(today);
    }

    private class QuakeAsyncTask extends AsyncTask<URL, Void, ArrayList<Quake>> {

        @Override
        protected ArrayList<Quake> doInBackground(URL... urls) {

            URL url = createUrl(USGSRequestUrl);

            String JsonResponse = "";

            try {
                JsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem making the http request to USGS API", e);
            }

            return extractDataFromJson(JsonResponse);
        }

        /**
         * Update the screen with the given earthquake (which was the result of the
         * {@link QuakeAsyncTask}).
         */
        @Override
        protected void onPostExecute(ArrayList<Quake> earthquakes) {
            if (earthquakes == null) {
                return;
            }
            updateUi(earthquakes);
        }


        private URL createUrl(String requestUrl) {
            URL url;
            try {
                url = new URL(requestUrl);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error while creating the URL", e);
                return null;
            }
            return url;
        }

        private String makeHttpRequest(URL url) throws IOException {
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

        private String readFromStream(InputStream inputStream) throws IOException {
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

        private ArrayList<Quake> extractDataFromJson(String jsonString) {
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
}