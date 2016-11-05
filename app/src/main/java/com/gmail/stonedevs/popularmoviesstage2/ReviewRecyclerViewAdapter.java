package com.gmail.stonedevs.popularmoviesstage2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.stonedevs.popularmoviesstage2.model.ReviewContent;

import java.util.List;

/**
 * Custom RecyclerViewAdapter class that presents a list of ReviewItem ViewHolders.
 */
class ReviewRecyclerViewAdapter
        extends RecyclerView.Adapter<ReviewRecyclerViewAdapter.ViewHolder> {
    private static final int MAX_LENGTH = 500;

    private final List<ReviewContent.ReviewItem> mValues;

    private final Context mContext;

    ReviewRecyclerViewAdapter( Context context, List<ReviewContent.ReviewItem> items ) {
        mContext = context;
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext() )
                .inflate( R.layout.movie_review_content, parent, false );
        return new ViewHolder( view );
    }

    @Override
    public void onBindViewHolder( final ViewHolder holder, int position ) {
        holder.mItem = mValues.get( position );

        holder.mAuthor.setText( holder.mItem.author );

        String content = holder.mItem.content;

        if ( content.length() > MAX_LENGTH ) {
            content = holder.mItem.content.substring( 0, MAX_LENGTH ) + getContext().getString( R.string.label_movie_reviews_read_review );
        }

        holder.mContent.setText( content );

        holder.mView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                Intent webIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( holder.mItem.url ) );
                getContext().startActivity( webIntent );
            }
        } );
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private Context getContext() {
        return mContext;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;

        final TextView mAuthor;
        final TextView mContent;

        ReviewContent.ReviewItem mItem;

        ViewHolder( View view ) {
            super( view );
            mView = view;
            mAuthor = ( TextView ) view.findViewById( R.id.review_author );
            mContent = ( TextView ) view.findViewById( R.id.review_content );
        }
    }
}