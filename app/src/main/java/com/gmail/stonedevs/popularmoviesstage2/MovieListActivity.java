package com.gmail.stonedevs.popularmoviesstage2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gmail.stonedevs.popularmoviesstage2.data.DatabaseContract;
import com.gmail.stonedevs.popularmoviesstage2.model.MovieContent;
import com.gmail.stonedevs.popularmoviesstage2.util.PrefsUtil;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity
        implements MovieDetailFragment.OnFavoriteListener {

    /**
     * Whether or not the activity is in Dual Pane mode. (Tablets vs Phones)
     */
    private boolean mTwoPane;

    /**
     * Position of currently activated item, used to help RecyclerView determine which item in list
     * is selected so it can update its view accordingly.
     */
    private int mActivatedPosition = -1;

    /**
     * Main RecyclerView object, this now allows the OnFavoriteListener callback to update an item
     * in the list depending upon its updated favorite setting.
     */
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_movie_list );

        Toolbar toolbar = ( Toolbar ) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        toolbar.setTitle( getTitle() );

        if ( findViewById( R.id.movie_detail_container ) != null ) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        setupRecyclerView( ( RecyclerView ) findViewById( R.id.movie_list ) );
        updateRecyclerViewAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate( R.menu.menu_main, menu );

        String prefsSearchMode = PrefsUtil.getPrefsMovieSearchMode( getApplicationContext() );
        String prefsSearchModePopular = getString( R.string.prefs_search_mode_value_popular );
        String prefsSearchModeTopRated = getString( R.string.prefs_search_mode_value_top_rated );
        String prefsSearchModeFavorites = getString( R.string.prefs_search_mode_value_favorites );

        // Set Movie Search Mode menu item check state
        if ( prefsSearchMode.equals( prefsSearchModePopular ) ) {
            menu.findItem( R.id.action_search_mode_popular ).setChecked( true );
        } else if ( prefsSearchMode.equals( prefsSearchModeTopRated ) ) {
            menu.findItem( R.id.action_search_mode_top_rated ).setChecked( true );
        } else if ( prefsSearchMode.equals( prefsSearchModeFavorites ) ) {
            menu.findItem( R.id.action_search_mode_favorites ).setChecked( true );
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        switch ( item.getItemId() ) {
            case R.id.action_search_mode_popular:
                if ( !PrefsUtil.getPrefsMovieSearchMode( getApplicationContext() ).equals( getString( R.string.prefs_search_mode_value_popular ) ) ) {
                    item.setChecked( true );
                    PrefsUtil.setPrefsMovieSearchMode( getApplicationContext(), getString( R.string.prefs_search_mode_value_popular ) );
                    updateRecyclerViewAdapter();
                    return false;
                }
                break;

            case R.id.action_search_mode_top_rated:
                if ( !PrefsUtil.getPrefsMovieSearchMode( getApplicationContext() ).equals( getString( R.string.prefs_search_mode_value_top_rated ) ) ) {
                    item.setChecked( true );
                    PrefsUtil.setPrefsMovieSearchMode( getApplicationContext(), getString( R.string.prefs_search_mode_value_top_rated ) );
                    updateRecyclerViewAdapter();
                    return false;
                }
                break;

            case R.id.action_search_mode_favorites:
                if ( !PrefsUtil.getPrefsMovieSearchMode( getApplicationContext() ).equals( getString( R.string.prefs_search_mode_value_favorites ) ) ) {
                    item.setChecked( true );
                    PrefsUtil.setPrefsMovieSearchMode( getApplicationContext(), getString( R.string.prefs_search_mode_value_favorites ) );
                    updateRecyclerViewAdapter();
                    return false;
                }
                break;
        }

        return super.onOptionsItemSelected( item );

    }

    /**
     * Helper method that sets up the main RecyclerView with a GridLayout and saves it for future
     * use.
     *
     * @param recyclerView Initial RecyclerView sent by OnCreate
     */
    private void setupRecyclerView( @NonNull RecyclerView recyclerView ) {
        // Determine how many columns to display by Single or Dual Pane mode.
        int numColumns = mTwoPane ? 3 : 2;

        recyclerView.setLayoutManager( new GridLayoutManager( this, numColumns ) );

        mRecyclerView = recyclerView;
    }

    /**
     * Helper method that allows an initial data fetch as well as when search_mode was changed in
     * SharedPrefs.
     */
    private void updateRecyclerViewAdapter() {
        try {
            mRecyclerView.setAdapter(
                    new MovieItemRecyclerViewAdapter(
                            MovieContent.fetchItems( getApplicationContext() ) ) );

            // If Adapter has 1 or more item, select first item, otherwise do not select any.
            if ( mRecyclerView.getAdapter().getItemCount() > 0 ) {
                mActivatedPosition = 0;
            } else {
                mActivatedPosition = -1;

                if ( mTwoPane ) {
                    removeFragment();
                }

                Toast.makeText( getApplicationContext(),
                        getString( R.string.toast_empty_movie_list ),
                        Toast.LENGTH_SHORT ).show();
            }
        } catch ( ExecutionException | InterruptedException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Callback method used to notify MainActivity of a change in selected Item's favorite status.
     */
    @Override
    public void updateIsFavorite() {
        if ( mTwoPane && mActivatedPosition >= 0 ) {
            mRecyclerView.getAdapter().notifyItemChanged( mActivatedPosition );
        }
    }

    /**
     * Custom RecyclerViewAdapter class that presents a list of MovieItem ViewHolders.
     */
    public class MovieItemRecyclerViewAdapter
            extends RecyclerView.Adapter<MovieItemRecyclerViewAdapter.ViewHolder> {

        private final List<MovieContent.MovieItem> mValues;

        MovieItemRecyclerViewAdapter( List<MovieContent.MovieItem> items ) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
            View view = LayoutInflater.from( parent.getContext() )
                    .inflate( R.layout.movie_list_content, parent, false );
            return new ViewHolder( view );
        }

        @Override
        public void onBindViewHolder( final ViewHolder holder, final int position ) {
            holder.mItem = mValues.get( position );

            Picasso.with( getApplicationContext() )
                    .load( getString( R.string.tmdb_image_base_url ) + holder.mItem.posterPath )
                    .into( holder.mPosterImage );
            holder.mPosterImage.setContentDescription( mValues.get( position ).title );

            if ( mTwoPane ) {
                if ( holder.getAdapterPosition() == mActivatedPosition ) {
                    holder.mSelected.setPadding( 8, 8, 8, 8 );

                    replaceFragment( holder.mItem.id );
                } else {
                    holder.mSelected.setPadding( 0, 0, 0, 0 );
                }
            }

            holder.mView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    if ( holder.getAdapterPosition() != mActivatedPosition ) {
                        // Clear selection drawable from previously selected
                        notifyItemChanged( mActivatedPosition );

                        // Save new selection
                        mActivatedPosition = holder.getAdapterPosition();

                        // Update selection drawable of currently selected
                        notifyItemChanged( mActivatedPosition );
                    }

                    if ( mTwoPane ) {
                        replaceFragment( holder.mItem.id );
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent( context, MovieDetailActivity.class );
                        intent.putExtra( MovieDetailFragment.ARG_ITEM_ID, holder.mItem.id );

                        context.startActivity( intent );
                    }
                }
            } );

            holder.mFavorite.setChecked( holder.mItem.favorite );
            holder.mFavorite.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View view ) {
                    holder.mItem.toggleFavorite();

                    // If now a favorite, insert it into database of Favorite Movies.
                    if ( holder.mItem.favorite ) {
                        ContentValues values = new ContentValues();
                        values.put( DatabaseContract.MovieEntry._ID, holder.mItem.id );
                        values.put( DatabaseContract.MovieEntry.COLUMN_TITLE, holder.mItem.title );
                        values.put( DatabaseContract.MovieEntry.COLUMN_POSTER_PATH, holder.mItem.posterPath );

                        getContentResolver().insert( DatabaseContract.MovieEntry.CONTENT_URI, values );

                        Toast.makeText( getApplicationContext(),
                                String.format( getString( R.string.format_added_favorite ), holder.mItem.title ),
                                Toast.LENGTH_SHORT ).show();
                    }

                    // If no longer a favorite, delete it from database.
                    else {
                        getContentResolver().delete(
                                DatabaseContract.MovieEntry.buildUriWithId( Long.parseLong( holder.mItem.id ) ),
                                null, null );

                        Toast.makeText( getApplicationContext(),
                                String.format( getString( R.string.format_removed_favorite ), holder.mItem.title ),
                                Toast.LENGTH_SHORT ).show();
                    }

                    // If we're in Dual Pane mode, update DetailFragment with new Favorite setting.
                    if ( mTwoPane ) {
                        ( ( MovieDetailFragment ) getSupportFragmentManager().
                                findFragmentById( R.id.movie_detail_container ) )
                                .updateIsFavorite();
                    }
                }
            } );
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final View mView;

            final ImageView mPosterImage;
            final CheckBox mFavorite;
            final RelativeLayout mSelected;

            MovieContent.MovieItem mItem;

            ViewHolder( View view ) {
                super( view );
                mView = view;
                mPosterImage = ( ImageView ) view.findViewById( R.id.poster_image );
                mFavorite = ( CheckBox ) view.findViewById( R.id.favorite );
                mSelected = ( RelativeLayout ) view.findViewById( R.id.selected );
            }
        }
    }

    /**
     * Helper method that replaces a fragment in the manager with a new one.
     *
     * @param id Item ID used to pull data from TheMovieDB.org's API.
     */
    private void replaceFragment( String id ) {
        Bundle arguments = new Bundle();
        arguments.putString( MovieDetailFragment.ARG_ITEM_ID, id );
        arguments.putBoolean( MovieDetailFragment.ARG_TWO_PANE, true );

        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments( arguments );

        getSupportFragmentManager().beginTransaction()
                .replace( R.id.movie_detail_container, fragment )
                .commit();
    }

    /**
     * Method that will remove a fragment in the case of an empty Movie List.
     */
    private void removeFragment() {
        MovieDetailFragment fragment =
                ( MovieDetailFragment ) getSupportFragmentManager().findFragmentById( R.id.movie_detail_container );

        if ( fragment != null ) {
            getSupportFragmentManager().beginTransaction().remove( fragment ).commit();
        }
    }
}