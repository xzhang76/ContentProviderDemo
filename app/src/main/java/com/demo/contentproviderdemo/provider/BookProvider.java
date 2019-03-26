package com.demo.contentproviderdemo.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
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

    private static final String PATH_QUERY_ITEM = "book/query/#";
    private static final String PATH_QUERY_ALL = "book/query/*";

    public static final Uri BOOK_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/book");
    public static final Uri QUERY_ITEM_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH_QUERY_ITEM);
    public static final Uri QUERY_ALL_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH_QUERY_ALL);

    private static final int BOOK_URI_CODE = 0;
    private static final int QUERY_ITEM_URI_CODE = 1;
    private static final int QUERY_ALL_URI_CODE = 2;

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_AUTHOR = "author";

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //uri和code之间建立对应关系
    static {
        sUriMatcher.addURI(AUTHORITY, "book", BOOK_URI_CODE);
        sUriMatcher.addURI(AUTHORITY, PATH_QUERY_ITEM, QUERY_ITEM_URI_CODE);
        sUriMatcher.addURI(AUTHORITY, PATH_QUERY_ALL, QUERY_ALL_URI_CODE);

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
        switch (sUriMatcher.match(uri)) {
            case QUERY_ALL_URI_CODE:
                if (readableDatabase.isOpen()) {
                    Cursor cursor = readableDatabase.query(DbOpenHelper.BOOK_TABLE_NAME, projection,
                            selection, selectionArgs, null, sortOrder, null, null);
                    cursor.setNotificationUri(getContext().getContentResolver(), uri);
                    return cursor;
                }
                break;
            case QUERY_ITEM_URI_CODE:
                if (readableDatabase.isOpen()) {
                    //单独传一个id进来
                    long id = ContentUris.parseId(uri);
                    Cursor cursor = readableDatabase.query(DbOpenHelper.BOOK_TABLE_NAME, projection,
                            KEY_ID + "=?", new String[]{id + ""}, null, sortOrder, null, null);
                    cursor.setNotificationUri(getContext().getContentResolver(), uri);
                    return cursor;
                }
                break;
            default:
                throw new IllegalArgumentException("query(), Unsupported uri: " + uri);
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        Log.d(TAG, "getType(), current thread: " + Thread.currentThread());
        String type = null;
        switch (sUriMatcher.match(uri)) {
            case QUERY_ITEM_URI_CODE:
                type = "vnd.android.cursor.item/book";
                break;
            case QUERY_ALL_URI_CODE:
                type = "vnd.android.cursor.dir/book";
                break;
            default:
                break;
        }
        return type;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.d(TAG, "insert()");
        SQLiteDatabase writableDatabase = mDbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case BOOK_URI_CODE:
                if (writableDatabase.isOpen()) {
                    long id = writableDatabase.insert(DbOpenHelper.BOOK_TABLE_NAME, null, values);
                    writableDatabase.close();
                    Uri newUri = ContentUris.withAppendedId(uri, id);
                    getContext().getContentResolver().notifyChange(newUri, null);
                    return newUri;
                }
                break;
            default:
                throw new IllegalArgumentException("insert(), Unsupported uri: " + uri);
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "delete()");
        SQLiteDatabase writableDatabase = mDbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case BOOK_URI_CODE:
                int count = 0;
                if (writableDatabase.isOpen()) {
                    count = writableDatabase.delete(DbOpenHelper.BOOK_TABLE_NAME, selection, selectionArgs);
                    writableDatabase.close();
                    if (count > 0) {
                        getContext().getContentResolver().notifyChange(uri, null);
                        return count;
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("insert(), Unsupported uri: " + uri);
        }
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "update()");
        SQLiteDatabase writableDatabase = mDbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case BOOK_URI_CODE:
                if (writableDatabase.isOpen()) {
                    int row = writableDatabase.update(DbOpenHelper.BOOK_TABLE_NAME, values, selection, selectionArgs);
                    writableDatabase.close();
                    if (row > 0) {
                        getContext().getContentResolver().notifyChange(uri, null);
                        return row;
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("insert(), Unsupported uri: " + uri);
        }
        return 0;
    }

}
