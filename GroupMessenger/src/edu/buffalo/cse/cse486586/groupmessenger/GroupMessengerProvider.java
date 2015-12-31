package edu.buffalo.cse.cse486586.groupmessenger;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

/**
 * GroupMessengerProvider is a key-value table. Once again, please note that we do not implement
 * full support for SQL as a usual ContentProvider does. We re-purpose ContentProvider's interface
 * to use it as a key-value table.
 * 
 * Please read:
 * 
 * http://developer.android.com/guide/topics/providers/content-providers.html
 * http://developer.android.com/reference/android/content/ContentProvider.html
 * 
 * before you start to get yourself familiarized with ContentProvider.
 * 
 * There are two methods you need to implement---insert() and query(). Others are optional and
 * will not be tested.
 * 
 * @author stevko
 *
 */
public class GroupMessengerProvider extends ContentProvider {
    // SQLite database name
    private static final String SQL_DB_NAME = "DB";
    
    // Column names
    public static final String FIRST_COL_NAME = "key";
    public static final String SECOND_COL_NAME = "value";
    
    // SQLite CREATE TABLE
    private static final String SQL_CREATE_TABLE = "CREATE TABLE " + SQL_DB_NAME + "("
            + FIRST_COL_NAME + " TEXT NOT NULL UNIQUE, " + SECOND_COL_NAME + " TEXT NOT NULL)";
    
    // Database helper
    private DatabaseHelper mDbHelper;
    
    // Database
    private SQLiteDatabase mDb;
    
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // You do not need to implement this.
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /*
         * You need to implement this method. Note that values will have two columns (a key
         * column and a value column) and one row that contains the actual (key, value) pair to be
         * inserted.
         * 
         * For actual storage, you can use any option. If you know how to use SQL, then you can use
         * SQLite. But this is not a requirement. You can use other storage options, such as the
         * internal storage option that I used in PA1. If you want to use that option, please
         * take a look at the code for PA1.
         */
        mDb = mDbHelper.getWritableDatabase();
        long id = mDb.replace(SQL_DB_NAME, null, values);
        uri = ContentUris.withAppendedId(uri, id);
        Log.v("insert", values.toString());
        return uri;
    }

    @Override
    public boolean onCreate() {
        // If you need to perform any one-time initialization task, please do it here.
        mDbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        /*
         * You need to implement this method. Note that you need to return a Cursor object
         * with the right format. If the formatting is not correct, then it is not going to work.
         * 
         * If you use SQLite, whatever is returned from SQLite is a Cursor object. However, you
         * still need to be careful because the formatting might still be incorrect.
         * 
         * If you use a file storage option, then it is your job to build a Cursor * object. I
         * recommend building a MatrixCursor described at:
         * http://developer.android.com/reference/android/database/MatrixCursor.html
         */
        mDb = mDbHelper.getReadableDatabase();
        String sql = "SELECT * FROM " + SQL_DB_NAME + " WHERE " + FIRST_COL_NAME + "=\""
                + selection + "\"";
        Cursor cursor = mDb.rawQuery(sql, null);
        Log.v("query", selection);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }
    
    /** Implements a simple SQLiteOpenHelper which will create and manage our database. */
    protected static final class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, SQL_DB_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase mDb) {
            mDb.execSQL(SQL_CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase mDb, int oldVersion, int newVersion) {
            // Auto-generated method stub
        }
    }
}
