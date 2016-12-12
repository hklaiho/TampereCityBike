package fi.tamk.ratboyz.tamperecitybike.utils;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

/**
 * Fetches data in Json format, then creates and adds markers on a map.
 */
public class BikeParkDataFetcher extends AsyncTask<Void, Void, String> {

    // Tampere Open Data URL.
    private static final String API_URL = "http://opendata.navici.com/tampere/opendata/ows?service=WFS&version=2.0.0&request=GetFeature&typeName=opendata:PYORAPARKIT_VIEW&outputFormat=json&srsName=EPSG:4326";
    // InputStream encoding.
    private static final String ENCODING = "UTF-8";
    // Map onto which markers are added.
    private GoogleMap map;

    public BikeParkDataFetcher(GoogleMap map) {
        this.map = map;
    }

    @Override
    protected String doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        InputStream in = null;
        URL url;
        StringBuilder content = new StringBuilder();

        try {
            url = new URL(API_URL);
            // Establish a connection
            urlConnection = (HttpURLConnection) url.openConnection();
            // Content stream
            in = new BufferedInputStream(urlConnection.getInputStream());
            // Prepare to read stream
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, ENCODING));
            // Result content String.
            String inputStr;
            // Parse stream
            while ((inputStr = streamReader.readLine()) != null)
                content.append(inputStr);

        } catch ( IOException e) {
            e.printStackTrace();
            // Early exit if URL / JSON is malformed, or if an error occurs
            // while reading the stream.
            return null;

        } finally {
            // Close the stream before closing the connection
            // as per Javadoc.
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (urlConnection != null)
                urlConnection.disconnect();

        }

        return content.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            // Array containing the "interesting" bit of the response body.
            JSONArray resultArray = new JSONObject(result).getJSONArray("features");
            // Cache the length for marginal performance boost.
            int length = resultArray.length();
            JSONObject entry;

            for(int i = 0; i < length; i++) {
                // TODO Decoded result object class to simplify code reading.
                entry = resultArray.getJSONObject(i);
                JSONArray coordinates = entry.getJSONObject("geometry").getJSONArray("coordinates");
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(coordinates.getDouble(1), coordinates.getDouble(0))));

            }

        } catch (JSONException e) {
            e.printStackTrace(System.out);
        }
    }
}
