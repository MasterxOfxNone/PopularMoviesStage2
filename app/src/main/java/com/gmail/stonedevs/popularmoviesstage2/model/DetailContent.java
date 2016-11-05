package com.gmail.stonedevs.popularmoviesstage2.model;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.gmail.stonedevs.popularmoviesstage2.BuildConfig;
import com.gmail.stonedevs.popularmoviesstage2.R;
import com.gmail.stonedevs.popularmoviesstage2.util.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class DetailContent {

    public static DetailItem fetchDetails( Context context, MovieContent.MovieItem item ) throws ExecutionException, InterruptedException {
        return new FetchDetailTask( context, item ).execute().get();
    }

    /**
     * A movie item representing a piece of content used to display its details.
     */
    public static class DetailItem extends MovieContent.MovieItem {

        public final Double userRating;
        public final String overview;
        public final String releaseDate;
        public final Integer runTime;

        DetailItem( String id, String title, String posterPath, Boolean favorite,
                    Double userRating, String overview, String releaseDate, Integer runTime ) {
            super( id, title, posterPath, favorite );

            this.userRating = userRating;
            this.overview = overview;
            this.releaseDate = releaseDate;
            this.runTime = runTime;
        }
    }

    /**
     * AsyncTask that fetches a movie's detailed information from TheMovieDB.org's API.
     */
    private static class FetchDetailTask extends AsyncTask<Void, Void, DetailItem> {
        private final String LOG_TAG = FetchDetailTask.class.getSimpleName();

        private MovieContent.MovieItem mItem;

        private Context mContext;

        FetchDetailTask( Context context, MovieContent.MovieItem item ) {
            mContext = context;
            mItem = item;
        }

        @Override
        protected DetailItem doInBackground( Void... params ) {
            try {
                final String BASE_URL = getContext().getString( R.string.tmdb_base_url );
                final String PARAM_API_KEY = getContext().getString( R.string.tmdb_param_api_key );

                String apiKey = BuildConfig.TMDB_API_KEY;

                Uri uri = Uri.parse( BASE_URL ).buildUpon()
                        .appendPath( mItem.id )
                        .appendQueryParameter( PARAM_API_KEY, apiKey )
                        .build();

                String jsonString = JsonUtil.retrieveJsonString( uri );

                final String RELEASE_DATE = getContext().getString( R.string.tmdb_json_release_date );
                final String RUNTIME = getContext().getString( R.string.tmdb_json_runtime );
                final String USER_RATING = getContext().getString( R.string.tmdb_json_user_rating );
                final String OVERVIEW = getContext().getString( R.string.tmdb_json_overview );

                JSONObject jsonObject = new JSONObject( jsonString );

                String releaseDate = jsonObject.getString( RELEASE_DATE );
                Integer runTime = jsonObject.getInt( RUNTIME );
                Double userRating = jsonObject.getDouble( USER_RATING );
                String overview = jsonObject.getString( OVERVIEW );

                return new DetailItem( mItem.id, mItem.title, mItem.posterPath, mItem.favorite,
                        userRating, overview, releaseDate, runTime );
            } catch ( JSONException e ) {
                e.printStackTrace();
            }

            return null;
        }

        private String getId() {
            return mItem.id;
        }

        private Context getContext() {
            return mContext;
        }
    }
}