package com.gmail.stonedevs.popularmoviesstage2.model;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.gmail.stonedevs.popularmoviesstage2.BuildConfig;
import com.gmail.stonedevs.popularmoviesstage2.R;
import com.gmail.stonedevs.popularmoviesstage2.util.JsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class TrailerContent {

    private static final List<TrailerItem> ITEMS = new ArrayList<>();

    private static final Map<String, TrailerItem> ITEM_MAP = new HashMap<>();

    public static List<TrailerItem> fetchItems( Context context, String id ) throws ExecutionException, InterruptedException {
        return new FetchTrailerTask( context, id ).execute().get();
    }

    private static void addItem( TrailerItem item ) {
        ITEMS.add( item );
        ITEM_MAP.put( item.id, item );
    }

    private static void clearItems() {
        ITEMS.clear();
        ITEM_MAP.clear();
    }

    private static List<TrailerItem> getItems() {
        return ITEMS;
    }

    public static TrailerItem getItem( String key ) {
        return ITEM_MAP.get( key );
    }

    /**
     * A movie trailer item representing a piece of content used in a list of movie trailers.
     */
    public static class TrailerItem {
        public final String id;
        public final String name;
        public final String siteKey;
        public final String siteName;

        TrailerItem( String id, String name, String siteKey, String siteName ) {
            this.id = id;
            this.name = name;
            this.siteKey = siteKey;
            this.siteName = siteName;
        }

        @Override
        public String toString() {
            return id + ", " + name + ", " + siteKey + ", " + siteName;
        }
    }

    /**
     * AsyncTask that fetches a list of movie trailers from TheMovieDB.org's API.
     */
    private static class FetchTrailerTask extends AsyncTask<Void, Void, List<TrailerItem>> {
        private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();

        private String mId;

        private Context mContext;

        FetchTrailerTask( Context context, String id ) {
            mContext = context;
            mId = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            clearItems();
        }

        @Override
        protected List<TrailerItem> doInBackground( Void... params ) {
            try {
                final String BASE_URL = getContext().getString( R.string.tmdb_base_url );
                final String PARAM_API_KEY = getContext().getString( R.string.tmdb_param_api_key );

                String apiKey = BuildConfig.TMDB_API_KEY;

                Uri uri = Uri.parse( BASE_URL ).buildUpon()
                        .appendPath( getId() )
                        .appendPath( getContext().getString( R.string.tmdb_param_videos ) )
                        .appendQueryParameter( PARAM_API_KEY, apiKey )
                        .build();

                String jsonString = JsonUtil.retrieveJsonString( uri );

                final String ID = getContext().getString( R.string.tmdb_json_id );
                final String NAME = getContext().getString( R.string.tmdb_json_name );
                final String SITE_NAME = getContext().getString( R.string.tmdb_json_site_name );
                final String SITE_KEY = getContext().getString( R.string.tmdb_json_site_key );

                final String RESULTS = getContext().getString( R.string.tmdb_json_results );

                JSONObject jsonObjectAsPage = new JSONObject( jsonString );
                JSONArray jsonArrayWithResults = jsonObjectAsPage.getJSONArray( RESULTS );

                for ( int i = 0; i < jsonArrayWithResults.length(); i++ ) {
                    JSONObject jsonObjectAsElement = jsonArrayWithResults.getJSONObject( i );

                    String id = jsonObjectAsElement.getString( ID );
                    String name = jsonObjectAsElement.getString( NAME );
                    String siteKey = jsonObjectAsElement.getString( SITE_KEY );
                    String siteName = jsonObjectAsElement.getString( SITE_NAME );

                    addItem( new TrailerItem( id, name, siteKey, siteName ) );
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