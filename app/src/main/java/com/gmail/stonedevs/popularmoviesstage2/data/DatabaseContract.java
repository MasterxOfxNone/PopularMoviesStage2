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

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import com.gmail.stonedevs.popularmoviesstage2.BuildConfig;

public class DatabaseContract {

    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID + ".contentProvider";

    public static final Uri BASE_CONTENT_URI = Uri.parse( "content://" + CONTENT_AUTHORITY );

    public static final String PATH_MOVIE = "movie";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_REVIEW = "review";

    /* Inner class that defines the table contents of the movie table */
    public static final class MovieEntry implements BaseColumns {

        // URI used to easily communicate the ContentProvider with the SQLiteDatabase
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath( PATH_MOVIE ).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        // Name of table, duh.
        public static final String TABLE_NAME = "movie";

        // Column that contains the movie's title
        public static final String COLUMN_TITLE = "title";

        // Column that contains the movie's poster URL
        public static final String COLUMN_POSTER_PATH = "poster_path";

        // Column that contains the movie's synopsis
        public static final String COLUMN_OVERVIEW = "overview";

        // Column that contains the movie's release date
        public static final String COLUMN_RELEASE_DATE = "release_date";

        // Column that contains the movie's user rating
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        // SQL Column Helpers
        public static final String SQL_COLUMN_ID = TABLE_NAME + "." + _ID;

        // SQL Query Helpers
        public static final String SQL_QUERY_WITH_ID = SQL_COLUMN_ID + " = ?";

        // Returns a base URI that includes _id; used with inserting, deleting, etc.
        public static Uri buildUriWithId( long _id ) {
            return ContentUris.withAppendedId( CONTENT_URI, _id );
        }

        // Returns the _id parsed from a given URI
        public static long getIdfromUri( Uri uri ) {
            return ContentUris.parseId( uri );
        }
    }

    /* Inner class that defines the table contents of the movie trailers table */
    public static final class TrailerEntry implements BaseColumns {

        // URI used to easily communicate the ContentProvider with the SQLiteDatabase
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath( PATH_TRAILER ).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        // Name of table, duh.
        public static final String TABLE_NAME = "video";

        // Column that contains the movie video's title (name)
        public static final String COLUMN_NAME = "name";

        // Column that contains the movie video's hosting site name
        public static final String COLUMN_SITE_NAME = "site";

        // Column that contains the movie video's hosting site key
        public static final String COLUMN_SITE_KEY = "key";

        // Column that contains the foreign key tied to a specific movie id
        public static final String COLUMN_MOVIE_KEY = "movie_id";
    }

    /* Inner class that defines the table contents of the movie reviews table */
    public static final class ReviewEntry implements BaseColumns {

        // URI used to easily communicate the ContentProvider with the SQLiteDatabase
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath( PATH_REVIEW ).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        // Name of table, duh.
        public static final String TABLE_NAME = "review";

        // Column that contains the movie review's author
        public static final String COLUMN_AUTHOR = "author";

        // Column that contains the movie review's review content
        public static final String COLUMN_CONTENT = "content";

        // Column that contains the foreign key tied to a specific movie id
        public static final String COLUMN_MOVIE_KEY = "movie_id";
    }

}
