package com.gmail.stonedevs.popularmoviesstage2;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.stonedevs.popularmoviesstage2.data.DatabaseContract;
import com.gmail.stonedevs.popularmoviesstage2.model.DetailContent;
import com.gmail.stonedevs.popularmoviesstage2.model.MovieContent;
import com.gmail.stonedevs.popularmoviesstage2.model.ReviewContent;
import com.gmail.stonedevs.popularmoviesstage2.model.TrailerContent;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {
    /**
     * Fragment argument representing the item ID that this fragment represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * Fragment argument representing whether or not app is in Dual Pane mode
     */
    public static final String ARG_TWO_PANE = "two_pane";

    /**
     * The content this fragment is presenting.
     */
    private MovieContent.MovieItem mItem;

    /**
     * Main View object, this now allows the updateIsFavorite helper method to update this item
     * depending upon its updated favorite setting.
     */
    private View mView;

    /**
     * Callback for MainActivity to update list if in Dual Pane mode.
     */
    private OnFavoriteListener mCallback;

    /**
     * Interface MainActivity implements to update list if in Dual Pane mode.
     */
    public interface OnFavoriteListener {
        void updateIsFavorite();
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );

        // If fragment is inside a Dual Pane screen, set up the callback for direct MainActivity
        // communication.
        if ( getArguments().containsKey( ARG_TWO_PANE ) ) {
            mCallback = ( OnFavoriteListener ) context;
        }
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        if ( getArguments().containsKey( ARG_ITEM_ID ) ) {
            mItem = MovieContent.getItem( getArguments().getString( ARG_ITEM_ID ) );
        }
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        View rootView = inflater.inflate( R.layout.movie_detail, container, false );

        if ( mItem != null ) {
            try {
                // First, let's start by retrieving the poster image tied to this movie.
                Picasso.with( getContext() )
                        .load( getString( R.string.tmdb_image_base_url_large ) + mItem.posterPath )
                        .into( ( ImageView ) rootView.findViewById( R.id.movie_poster ) );

                // Now, let's fetch detailed information from TheMovieDB.org about movie.
                DetailContent.DetailItem detailItem = DetailContent.fetchDetails( getContext(), mItem );

                // If we have a proper DetailItem object, let's fill our views with its data!
                if ( detailItem != null ) {
                    ( ( TextView ) rootView.findViewById( R.id.movie_title ) )
                            .setText( detailItem.title );

                    CheckBox favorite = ( CheckBox ) rootView.findViewById( R.id.favorite );
                    favorite.setChecked( mItem.favorite );
                    favorite.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick( View view ) {
                            mItem.toggleFavorite();

                            if ( mItem.favorite ) {
                                ContentValues values = new ContentValues();
                                values.put( DatabaseContract.MovieEntry._ID, mItem.id );
                                values.put( DatabaseContract.MovieEntry.COLUMN_TITLE, mItem.title );
                                values.put( DatabaseContract.MovieEntry.COLUMN_POSTER_PATH, mItem.posterPath );

                                getContext().getContentResolver().insert(
                                        DatabaseContract.MovieEntry.CONTENT_URI, values );

                                Toast.makeText( getContext(),
                                        String.format( getString( R.string.format_added_favorite ), mItem.title ),
                                        Toast.LENGTH_SHORT ).show();
                            } else {
                                getContext().getContentResolver().delete(
                                        DatabaseContract.MovieEntry.buildUriWithId( Long.parseLong( mItem.id ) ),
                                        null, null );

                                Toast.makeText( getContext(),
                                        String.format( getString( R.string.format_removed_favorite ), mItem.title ),
                                        Toast.LENGTH_SHORT ).show();
                            }

                            if ( getArguments().containsKey( ARG_TWO_PANE ) ) {
                                mCallback.updateIsFavorite();
                            }
                        }
                    } );

                    // http://stackoverflow.com/questions/11046053/how-to-format-date-string-in-java
                    Date date = new SimpleDateFormat( "yyyy-MM-dd", Locale.US ).parse( detailItem.releaseDate );
                    ( ( TextView ) rootView.findViewById( R.id.movie_release_date ) )
                            .setText( new SimpleDateFormat( "yyyy", Locale.US ).format( date ) );

                    ( ( TextView ) rootView.findViewById( R.id.movie_runtime ) )
                            .setText( String.format( getString( R.string.format_movie_runtime ), Integer.toString( detailItem.runTime ) ) );

                    ( ( TextView ) rootView.findViewById( R.id.movie_user_rating ) )
                            .setText( String.format( getString( R.string.format_movie_user_rating ), detailItem.userRating ) );

                    ( ( TextView ) rootView.findViewById( R.id.movie_plot_summary ) )
                            .setText( detailItem.overview );

                    // Now, let's fetch some movie trailers!
                    int trailerCount = setupRecyclerViewForTrailers( ( RecyclerView ) rootView.findViewById( R.id.movie_trailers ) );
                    if ( trailerCount > 0 ) {
                        ( ( TextView ) rootView.findViewById( R.id.movie_trailer_label ) )
                                .setText( String.format( getString( R.string.label_movie_trailers_format ), Integer.toString( trailerCount ) ) );
                    }

                    // Finally, let's fetch some movie reviews!
                    int reviewCount = setupRecyclerViewForReviews( ( RecyclerView ) rootView.findViewById( R.id.movie_reviews ) );
                    if ( reviewCount > 0 ) {
                        ( ( TextView ) rootView.findViewById( R.id.movie_review_label ) )
                                .setText( String.format( getString( R.string.label_movie_reviews_format ), Integer.toString( reviewCount ) ) );
                    }
                }
            } catch ( ParseException | ExecutionException | InterruptedException e ) {
                e.printStackTrace();
            }
        }

        // Setting mView to help allow the use of this view in the helper method updateIsFavorite().
        mView = rootView;

        return rootView;
    }

    /**
     * Helper method that allows MainActivity to update this fragment with any changes to whether
     * a movie has just been set as favorite or not.
     */
    public void updateIsFavorite() {
        ( ( CheckBox ) mView.findViewById( R.id.favorite ) ).setChecked( mItem.favorite );
    }

    /**
     * Sets up the Movie Trailer RecyclerView, then sets an adapter with fetched data from API.
     *
     * @param recyclerView View to set up.
     * @return Number of Trailers fetched from API.
     */
    private int setupRecyclerViewForTrailers( @NonNull RecyclerView recyclerView ) {
        recyclerView.setLayoutManager( new LinearLayoutManager( getContext() ) );

        try {
            recyclerView.setAdapter(
                    new TrailerRecyclerViewAdapter(
                            getContext(),
                            TrailerContent.fetchItems( getContext(), mItem.id ) )
            );

            return recyclerView.getAdapter().getItemCount();
        } catch ( ExecutionException | InterruptedException e ) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Sets up the Movie Review RecyclerView, then sets an adapter with fetched data from API.
     *
     * @param recyclerView View to set up.
     * @return Number of Reviews fetched from API.
     */
    private int setupRecyclerViewForReviews( @NonNull RecyclerView recyclerView ) {
        recyclerView.setLayoutManager( new LinearLayoutManager( getContext() ) );

        try {
            recyclerView.setAdapter(
                    new ReviewRecyclerViewAdapter(
                            getContext(),
                            ReviewContent.fetchItems( getContext(), mItem.id ) )
            );

            return recyclerView.getAdapter().getItemCount();
        } catch ( ExecutionException | InterruptedException e ) {
            e.printStackTrace();
        }

        return 0;
    }
}