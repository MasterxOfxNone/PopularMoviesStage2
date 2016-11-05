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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helps create, upgrade, and modify an SQLiteDatabase
 */
class DatabaseHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = DatabaseProvider.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "themovie.db";

    DatabaseHelper( Context context ) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate( SQLiteDatabase sqLiteDatabase ) {
        // _ID provided by TheMovieDB's API
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + DatabaseContract.MovieEntry.TABLE_NAME + " ( " +
                DatabaseContract.MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                DatabaseContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                DatabaseContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL );";

        sqLiteDatabase.execSQL( SQL_CREATE_MOVIE_TABLE );
    }

    @Override
    public void onUpgrade( SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion ) {
        sqLiteDatabase.execSQL( "DROP TABLE IF EXISTS " + DatabaseContract.MovieEntry.TABLE_NAME );
        onCreate( sqLiteDatabase );
    }
}
