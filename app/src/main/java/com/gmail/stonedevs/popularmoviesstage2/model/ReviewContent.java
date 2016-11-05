package com.gmail.stonedevs.popularmoviesstage2.model;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.gmail.stonedevs.popularmoviesstage2.BuildConfig;
import com.gmail.stonedevs.popularmoviesstage2.R;
import com.gmail.stonedevs.popularmoviesstage2.util.JsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ReviewContent {

    private static final List<ReviewItem> ITEMS = new ArrayList<>();

    private static final Map<String, ReviewItem> ITEM_MAP = new HashMap<>();

    public static List<ReviewItem> fetchItems( Context context, String id ) throws ExecutionException, InterruptedException {
        return new FetchReviewTask( context, id ).execute().get();
    }

    private static void addItem( ReviewItem item ) {
        ITEMS.add( item );
        ITEM_MAP.put( item.id, item );
    }

    private static void clearItems() {
        ITEMS.clear();
        ITEM_MAP.clear();
    }

    private static List<ReviewItem> getItems() {
        return ITEMS;
    }

    public static ReviewItem getItem( String key ) {
        return ITEM_MAP.get( key );
    }

    /**
     * A movie review item representing a piece of content used in a list of movie reviews.
     */
    public static class ReviewItem {
        public final String id;
        public final String author;
        public final String content;
        public final String url;

        ReviewItem( String id, String author, String content, String url ) {
            this.id = id;
            this.author = author;
            this.content = content;
            this.url = url;
        }
    }

    /**
     * AsyncTask that fetches a list of movie reviews from TheMovieDB.org's API.
     */
    private static class FetchReviewTask extends AsyncTask<Void, Void, List<ReviewItem>> {
        private final String LOG_TAG = FetchReviewTask.class.getSimpleName();

        private String mId;

        private Context mContext;

        FetchReviewTask( Context context, String id ) {
            mContext = context;
            mId = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            clearItems();
        }

        @Override
        protected List<ReviewItem> doInBackground( Void... params ) {
            try {
                final String BASE_URL = getContext().getString( R.string.tmdb_base_url );
                final String PARAM_API_KEY = getContext().getString( R.string.tmdb_param_api_key );

                String apiKey = BuildConfig.TMDB_API_KEY;

                Uri uri = Uri.parse( BASE_URL ).buildUpon()
                        .appendPath( getId() )
                        .appendPath( getContext().getString( R.string.tmdb_param_reviews ) )
                        .appendQueryParameter( PARAM_API_KEY, apiKey )
                        .build();

                String jsonString = JsonUtil.retrieveJsonString( uri );

                final String ID = getContext().getString( R.string.tmdb_json_id );
                final String AUTHOR = getContext().getString( R.string.tmdb_json_author );
                final String CONTENT = getContext().getString( R.string.tmdb_json_content );
                final String URL = getContext().getString( R.string.tmdb_json_url );

                final String RESULTS = getContext().getString( R.string.tmdb_json_results );

                JSONObject jsonObjectAsPage = new JSONObject( jsonString );
                JSONArray jsonArrayWithResults = null;
                jsonArrayWithResults = jsonObjectAsPage.getJSONArray( RESULTS );

                for ( int i = 0; i < jsonArrayWithResults.length(); i++ ) {
                    JSONObject jsonObjectAsElement = jsonArrayWithResults.getJSONObject( i );

                    String id = jsonObjectAsElement.getString( ID );
                    String author = jsonObjectAsElement.getString( AUTHOR );
                    String content = jsonObjectAsElement.getString( CONTENT );
                    String url = jsonObjectAsElement.getString( URL );

                    addItem( new ReviewItem( id, author, content, url ) );
                }

                return getItems();
            } catch ( JSONException e ) {
                e.printStackTrace();
            }

            return null;
        }

        private String getId() {
            return mId;
        }

        private Context getContext() {
            return mContext;
        }
    }
}