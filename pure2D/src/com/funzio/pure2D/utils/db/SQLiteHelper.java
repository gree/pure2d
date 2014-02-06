/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
/**
 * 
 */
package com.funzio.pure2D.utils.db;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {
    protected static final String TAG = SQLiteHelper.class.getSimpleName();

    protected SQLiteDatabase mDb;
    protected final Context mContext;
    protected String mSystemPath;

    /**
     * Constructor Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * 
     * @param context
     */
    public SQLiteHelper(final Context context, final String dbName) {
        super(context, dbName, null, 1);
        mContext = context;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    public boolean checkAndCreate(final String assetPath, final String systemPath) {
        Log.v(TAG, "checkAndCreate()");
        mSystemPath = systemPath;

        if (isExisting(systemPath)) {
            Log.v(TAG, systemPath + " already exists!");
            return true;
        } else {

            // By calling this method and empty database will be created into the default system path
            // of your application so we are gonna be able to overwrite that database with our database.
            getReadableDatabase();

            try {
                copy(assetPath, systemPath);
            } catch (IOException e) {
                Log.e(TAG, "Error copying database! " + assetPath);
                return false;
            }
        }

        return true;
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * 
     * @return true if it exists, false if it doesn't
     */
    public static boolean isExisting(final String systemPath) {
        Log.v(TAG, "isExisting(): " + systemPath);

        SQLiteDatabase db = null;

        try {
            db = SQLiteDatabase.openDatabase(systemPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            Log.w(TAG, e.getMessage());
        }

        if (db != null) {
            db.close();
        }

        return db != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the system folder, from where it can be accessed and handled. This is done by transfering bytestream.
     */
    protected void copy(final String assetPath, final String systemPath) throws IOException {
        Log.v(TAG, "copy(): " + assetPath + " -> " + systemPath);

        // Open your local db as the input stream
        final InputStream myInput = mContext.getAssets().open(assetPath);

        // Open the empty db as the output stream
        final OutputStream myOutput = new FileOutputStream(systemPath);

        // transfer bytes from the inputfile to the outputfile
        final byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void open(final String systemPath) throws SQLException {
        Log.v(TAG, "open(): " + systemPath);
        mSystemPath = systemPath;

        // Open the database
        mDb = SQLiteDatabase.openDatabase(mSystemPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if (mDb != null) {
            mDb.close();
        }

        super.close();

    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        Log.v(TAG, "onCreate(): " + db);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {

    }
}
