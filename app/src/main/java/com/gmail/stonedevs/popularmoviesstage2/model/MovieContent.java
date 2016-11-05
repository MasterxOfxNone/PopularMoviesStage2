package com.gmail.stonedevs.popularmoviesstage2.model;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.gmail.stonedevs.popularmoviesstage2.BuildConfig;
import com.gmail.stonedevs.popularmoviesstage2.R;
import com.gmail.stonedevs.popularmoviesstage2.data.DatabaseContract;
import com.gmail.stonedevs.popularmoviesstage2.util.JsonUtil;
import com.gmail.stonedevs.popularmoviesstage2.util.PrefsUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MovieContent {

    private static final List<MovieItem> ITEMS = new ArrayList<>();

    private static final Map<String, MovieItem> ITEM_MAP = new HashMap<>();

    public static List<MovieItem> fetchItems( Context context ) throws ExecutionException, InterruptedException {
        return new FetchMovieTask( context ).execute().get();
    }

    private static void addItem( MovieItem item ) {
        ITEMS.add( item );
        ITEM_MAP.put( item.id, item );
    }

    private static void clearItems() {
        ITEMS.clear();
        ITEM_MAP.clear();
    }

    private static List<MovieItem> getItems() {
        return ITEMS;
    }

    public static MovieItem getItem( String key ) {
        return ITEM_MAP.get( key );
    }

    /**
     * A movie item representing a piece of content used in a list.
     */
    public static class MovieItem {
        public final String id;
        public final String title;
        public final String posterPath;
        public Boolean favorite;

        MovieItem( String id, String title, String posterPath, Boolean favorite ) {
            this.id = id;
            this.title = title;
            this.posterPath = posterPath;
            this.favorite = favorite;
        }

        public void toggleFavorite() {
            favorite = !favorite;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    /**
     * AsyncTask that fetches a movie's ID, Title, and Poster Path from TheMovieDB.org's API.
     */
    private static class FetchMovieTask extends AsyncTask<Void, Void, List<MovieItem>> {
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private Context mContext;

        FetchMovieTask( Context context ) {
            mContext = context;
        }

        // Before we fetch new data, let's clear the current items.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            clearItems();
        }

        @Override
        protected List<MovieItem> doInBackground( Void... params ) {

            final String ID = getContext().getString( R.string.tmdb_json_id );
            final String TITLE = getContext().getString( R.string.tmdb_json_title );
            final String POSTER_PATH = getContext().getString( R.string.tmdb_json_poster_url );

            // First, let's retrieve saved search_mode from SharedPreference helper method.
            String search_mode = PrefsUtil.getPrefsMovieSearchMode( getContext() );

            // Next, let's check to see if search_mode is equal to Favorites, if so, pull from database.
            if ( Objects.equals( search_mode, getContext().getString( R.string.prefs_search_mode_value_favorites ) ) ) {
                Cursor cursor = getContext().getContentResolver().query( DatabaseContract.MovieEntry.CONTENT_URI,
                        null, null, null, null );

                if ( cursor != null ) {
                    while ( cursor.moveToNext() ) {
                        long _id = cursor.getLong( cursor.getColumnIndex( DatabaseContract.MovieEntry._ID ) );
                        String posterPath = cursor.getString( cursor.getColumnIndex( DatabaseContract.MovieEntry.COLUMN_POSTER_PATH ) );
                        String title = cursor.getString( cursor.getColumnIndex( DatabaseContract.MovieEntry.COLUMN_TITLE ) );

                        addItem( new MovieItem( Long.toString( _id ), title, posterPath, true ) );
                    }
                    cursor.close();
                }
            }

            // If not, let's retrieve the jsonString directly from TheMovieDB.org and parse it.
            else {
                final String BASE_URL = getContext().getString( R.string.tmdb_base_url );
                final String PARAM_API_KEY = getContext().getString( R.string.tmdb_param_api_key );

                String apiKey = BuildConfig.TMDB_API_KEY;

                Uri uri = Uri.parse( BASE_URL ).buildUpon()
                        .appendPath( search_mode )
                        .appendQueryParameter( PARAM_API_KEY, apiKey )
                        .build();

                String jsonString = JsonUtil.retrieveJsonString( uri );

                try {
                    final String RESULTS = getContext().getString( R.string.tmdb_json_results );

                    JSONObject jsonObjectAsPage = new JSONObject( jsonString );
                    JSONArray jsonArrayWithResults = jsonObjectAsPage.getJSONArray( RESULTS );

                    for ( int i = 0; i < jsonArrayWithResults.length(); i++ ) {
                        JSONObject jsonObjectAsElement = jsonArrayWithResults.getJSONObject( i );

                        int id = jsonObjectAsElement.getInt( ID );
                        String title = jsonObjectAsElement.getString( TITLE );
                        String posterPath = jsonObjectAsElement.getString( POSTER_PATH );

                        Boolean favorite = checkIsFavorite( id );

                        addItem( new MovieItem( Integer.toString( id ), title, posterPath, favorite ) );
                    }
                } catch ( JSONException e ) {
                    e.printStackTrace();
                }
            }

            // Finally, let's return the list of Movie objects back to Adapter.
            return getItems();
        }

        /**
         * Helper method to quickly check if a movie has previously been set as a favorite.
         *
         * @param _id Id of movie item taken directly from TheMovieDB.org's API.
         * @return Whether or not a movie has been set as favorite.
         */
        private Boolean checkIsFavorite( long _id ) {
            Cursor cursor = getContext().getContentResolver().query( DatabaseContract.MovieEntry.buildUriWithId( _id ),
                    null, null, null, null );

            if ( cursor == null ) return false;

            boolean isValidated = cursor.moveToFirst();

            cursor.close();
            return isValidated;
        }

        private Context getContext() {
            return mContext;
        }
    }
}