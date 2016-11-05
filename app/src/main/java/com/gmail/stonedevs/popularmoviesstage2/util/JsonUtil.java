package com.gmail.stonedevs.popularmoviesstage2.util;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JsonUtil {
    private static final String LOG_TAG = JsonUtil.class.getSimpleName();

    /**
     * Method that talks to api website and downloads the resulting json file as a string
     *
     * @param builtUri Prebuilt uri, contains url plus api query parameters already
     * @return String object that contains the resulting json file
     */
    public static String retrieveJsonString( Uri builtUri ) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            String myURL = builtUri.toString();
            URL url = new URL( myURL );

            // Connect to TheMovieDB.org with GET request to retrieve a parsable response.
            urlConnection = ( HttpURLConnection ) url.openConnection();
            urlConnection.setRequestMethod( "GET" );
            urlConnection.connect();

            // Read response into a StringBuffer that we can use to easily send across app.
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            // If response was empty, there's nothing to we can do but return as empty.
            if ( inputStream == null ) return null;

            reader = new BufferedReader( new InputStreamReader( inputStream ) );

            String line;
            while ( ( line = reader.readLine() ) != null )
                buffer.append( line ).append( "\n" );

            // If StringBuffer is empty, there's nothing we can do but return as empty.
            if ( buffer.length() == 0 ) return null;

            // Return successful response as String in JSON format.
            return buffer.toString();
        } catch ( IOException e ) {
            Log.e( LOG_TAG, e.getMessage() );
            return null;
        } finally {
            if ( urlConnection != null ) {
                urlConnection.disconnect();
            }
            if ( reader != null ) {
                try {
                    reader.close();
                } catch ( final IOException e ) {
                    Log.e( LOG_TAG, e.getMessage() );
                }
            }
        }
    }
}
