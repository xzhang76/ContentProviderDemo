package com.demo.contentproviderdemo.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.demo.contentproviderdemo.db.DbOpenHelper;

public class BookProvider extends ContentProvider {
    private final static String TAG = "BookProvider";

    public static final String AUTHORITY = "com.demo.contentproviderdemo.provider";

    public static final Uri QUERY_ITEM_URI = Uri.parse("content://" + AUTHORITY + "/book");

    public static final int BOOK_URI_CODE = 0;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //uri和code之间建立对应关系
    static {
        sUriMatcher.addURI(AUTHORITY, "book", BOOK_URI_CODE);
    }

    private DbOpenHelper mDbHelper;

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate(), current thread: " + Thread.currentThread());
        initProviderData();
        return false;
    }

    private void initProviderData() {
        mDbHelper = new DbOpenHelper(getContext());
        SQLiteDatabase writableDatabase = mDbHelper.getWritableDatabase();
        writableDatabase.execSQL("delete from " + DbOpenHelper.BOOK_TABLE_NAME);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.d(TAG, "query(), current thread: " + Thread.currentThread());
        SQLiteDatabase readableDatabase = mDbHelper.getReadableDatabase();
        if (readableDatabase.isOpen()) {
            return readableDatabase.query(DbOpenHelper.BOOK_TABLE_NAME, projection, selection, selectionArgs, null, sortOrder, null, null);
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        Log.d(TAG, "getType(), current thread: " + Thread.currentThread());
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.d(TAG, "insert()");
        SQLiteDatabase writableDatabase = mDbHelper.getWritableDatabase();
        if (writableDatabase.isOpen()) {
            writableDatabase.insert(DbOpenHelper.BOOK_TABLE_NAME, null, values);
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "delete()");
        SQLiteDatabase writableDatabase = mDbHelper.getWritableDatabase();
        int count = 0;
        if (writableDatabase.isOpen()) {
            count = writableDatabase.delete(DbOpenHelper.BOOK_TABLE_NAME, selection, selectionArgs);
            if (count > 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "update()");
        int row = 0;
        SQLiteDatabase writableDatabase = mDbHelper.getWritableDatabase();
        if (writableDatabase.isOpen()) {
            row = writableDatabase.update(DbOpenHelper.BOOK_TABLE_NAME, values, selection, selectionArgs);
            if (row > 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
        return row;
    }

}
