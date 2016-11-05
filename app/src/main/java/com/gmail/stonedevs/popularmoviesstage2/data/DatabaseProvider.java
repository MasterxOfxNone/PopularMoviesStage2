/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gmail.stonedevs.popularmoviesstage2.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.gmail.stonedevs.popularmoviesstage2.R;


/**
 * Public class that provides uri manipulation for easier communication with an SQLiteDatabase
 */
public class DatabaseProvider extends ContentProvider {
    private UriMatcher mUriMatcher;
    private DatabaseHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID = 101;

    private UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher( UriMatcher.NO_MATCH );
        final String authority = DatabaseContract.CONTENT_AUTHORITY;

        matcher.addURI( authority, DatabaseContract.PATH_MOVIE, MOVIE );
        matcher.addURI( authority, DatabaseContract.PATH_MOVIE + "/#", MOVIE_WITH_ID );

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper( getContext() );
        mUriMatcher = buildUriMatcher();
        return true;
    }

    @Override
    public String getType( @NonNull Uri uri ) {
        final int match = mUriMatcher.match( uri );

        switch ( match ) {
            case MOVIE:
                return DatabaseContract.MovieEntry.CONTENT_DIR_TYPE;

            case MOVIE_WITH_ID:
                return DatabaseContract.MovieEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException(
                        String.format( getContext().getString( R.string.error_unknown_uri ), uri ) );
        }
    }

    @Override
    public Cursor query( @NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                         String sortOrder ) {

        Cursor returnCursor;

        switch ( mUriMatcher.match( uri ) ) {
            case MOVIE:
                break;

            case MOVIE_WITH_ID:
                selection = DatabaseContract.MovieEntry.SQL_QUERY_WITH_ID;
                selectionArgs = new String[]{ Long.toString( DatabaseContract.MovieEntry.getIdfromUri( uri ) ) };
                break;

            default:
                throw new UnsupportedOperationException(
                        String.format( getContext().getString( R.string.error_unknown_uri ), uri ) );
        }

        returnCursor = mOpenHelper.getReadableDatabase().query(
                DatabaseContract.MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        returnCursor.setNotificationUri( getContext().getContentResolver(), uri );
        return returnCursor;
    }

    @Override
    public Uri insert( @NonNull Uri uri, ContentValues values ) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = mUriMatcher.match( uri );
        Uri returnUri;

        switch ( match ) {
            case MOVIE: {
                long _id = db.insert( DatabaseContract.MovieEntry.TABLE_NAME, null, values );
                if ( _id > 0 )
                    returnUri = DatabaseContract.MovieEntry.buildUriWithId( _id );
                else
                    throw new android.database.SQLException(
                            String.format( getContext().getString( R.string.error_sqlexception_row ), uri ) );
                break;
            }

            default:
                throw new UnsupportedOperationException(
                        String.format( getContext().getString( R.string.error_unknown_uri ), uri ) );
        }

        getContext().getContentResolver().notifyChange( uri, null );
        return returnUri;
    }

    @Override
    public int delete( @NonNull Uri uri, String selection, String[] selectionArgs ) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = mUriMatcher.match( uri );

        int rowsDeleted;

        // Setting selection to 1 makes delete method return the number of rows deleted
        if ( null == selection ) selection = "1";

        switch ( match ) {
            case MOVIE:
                break;

            case MOVIE_WITH_ID:
                selection = DatabaseContract.MovieEntry.SQL_QUERY_WITH_ID;
                selectionArgs = new String[]{ Long.toString( DatabaseContract.MovieEntry.getIdfromUri( uri ) ) };
                break;

            default:
                throw new UnsupportedOperationException(
                        String.format( getContext().getString( R.string.error_unknown_uri ), uri ) );
        }

        rowsDeleted = db.delete(
                DatabaseContract.MovieEntry.TABLE_NAME,
                selection,
                selectionArgs
        );

        if ( rowsDeleted > 0 ) getContext().getContentResolver().notifyChange( uri, null );

        return rowsDeleted;
    }

    @Override
    public int update( @NonNull Uri uri, ContentValues contentValues, String s, String[] strings ) {
        return 0;
    }
}