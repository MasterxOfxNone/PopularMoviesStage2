package com.gmail.stonedevs.popularmoviesstage2.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.gmail.stonedevs.popularmoviesstage2.R;

public class PrefsUtil {

    /**
     * Gets settings info for Movie Search Mode
     *
     * @return String value of current/default Movie Search Mode (popular/top_rated)
     */
    public static String getPrefsMovieSearchMode( Context context ) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( context );

        return prefs.getString( context.getString( R.string.prefs_search_mode_key ), context.getString( R.string.prefs_search_mode_default_value ) );
    }

    /**
     * Sets settings info for Movie Search Mode
     *
     * @param searchMode String value of requested Movie Search Mode (popular/top_rated)
     */
    public static void setPrefsMovieSearchMode( Context context, String searchMode ) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( context );

        prefs.edit()
                .putString( context.getString( R.string.prefs_search_mode_key ), searchMode )
                .apply();
    }
}
