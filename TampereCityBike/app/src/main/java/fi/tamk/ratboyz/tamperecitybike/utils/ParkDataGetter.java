package fi.tamk.ratboyz.tamperecitybike.utils;

import android.os.AsyncTask;
import android.os.SystemClock;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 */

public class ParkDataGetter extends AsyncTask<Void, Void, String> {
    private static final String apiUrl = "http://opendata.navici.com/tampere/opendata/ows?service=WFS&version=2.0.0&request=GetFeature&typeName=opendata:PYORAPARKIT_VIEW&outputFormat=json";

    @Override
    protected String doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        InputStream in = null;
        URL url;
        StringBuilder content = new StringBuilder();

        try {
            url = new URL(apiUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // Early exit if URL is malformed.
            return null;
        }

        try {
            // Establish a connection
            urlConnection = (HttpURLConnection) url.openConnection();
            // Content stream
            in = new BufferedInputStream(urlConnection.getInputStream());
            // Prepare to read stream
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            String inputStr;
            // Parse stream
            while ((inputStr = streamReader.readLine()) != null)
                content.append(inputStr);

        } catch (IOException e) {
            e.printStackTrace();
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
        // TODO Parse data
        System.out.print(result);
    }
}
