package com.gmail.stonedevs.popularmoviesstage2;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.stonedevs.popularmoviesstage2.model.TrailerContent;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Custom RecyclerViewAdapter class that presents a list of TrailerItem ViewHolders.
 */
class TrailerRecyclerViewAdapter
        extends RecyclerView.Adapter<TrailerRecyclerViewAdapter.ViewHolder> {

    private final List<TrailerContent.TrailerItem> mValues;

    private final Context mContext;

    TrailerRecyclerViewAdapter( Context context, List<TrailerContent.TrailerItem> items ) {
        mContext = context;
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext() )
                .inflate( R.layout.movie_trailer_content, parent, false );
        return new ViewHolder( view );
    }

    @Override
    public void onBindViewHolder( final ViewHolder holder, int position ) {
        holder.mItem = mValues.get( position );

        Picasso.with( getContext() )
                .load( String.format( getContext().getString( R.string.youtube_movie_trailer_url_format ), holder.mItem.siteKey ) )
                .into( holder.mThumbnailImage );
        holder.mThumbnailImage.setContentDescription( holder.mItem.name );
        holder.mName.setText( holder.mItem.name );
        holder.mOrigin.setText( holder.mItem.siteName );

        holder.mView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                // http://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
                Intent appIntent = new Intent( Intent.ACTION_VIEW,
                        Uri.parse( getContext().getString( R.string.youtube_app_intent_url ) + holder.mItem.siteKey ) );
                Intent webIntent = new Intent( Intent.ACTION_VIEW,
                        Uri.parse( getContext().getString( R.string.youtube_web_intent_url ) + holder.mItem.siteKey ) );

                try {
                    getContext().startActivity( appIntent );
                } catch ( ActivityNotFoundException ex ) {
                    getContext().startActivity( webIntent );
                }
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

        final ImageView mThumbnailImage;
        final TextView mName;
        final TextView mOrigin;

        TrailerContent.TrailerItem mItem;

        ViewHolder( View view ) {
            super( view );
            mView = view;
            mThumbnailImage = ( ImageView ) view.findViewById( R.id.trailer_thumbnail_image );
            mName = ( TextView ) view.findViewById( R.id.trailer_name );
            mOrigin = ( TextView ) view.findViewById( R.id.trailer_origin );
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mName + "'";
        }
    }
}