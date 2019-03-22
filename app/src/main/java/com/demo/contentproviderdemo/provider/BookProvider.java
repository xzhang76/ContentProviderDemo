package com.demo.contentproviderdemo.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
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

    public static final Uri BOOK_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/book");
    public static final Uri USER_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/user");

    public static final int BOOK_URI_CODE = 0;
    public static final int USER_URI_CODE = 1;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //uri和code之间建立对应关系
    static {
        sUriMatcher.addURI(AUTHORITY, "book", BOOK_URI_CODE);
        sUriMatcher.addURI(AUTHORITY, "user", USER_URI_CODE);
    }

    private SQLiteDatabase mDbDataBase;

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate(), current thread: " + Thread.currentThread());
        initProviderData();
        return false;
    }

    private void initProviderData() {
        mDbDataBase = new DbOpenHelper(getContext()).getWritableDatabase();
        mDbDataBase.execSQL("delete from " + DbOpenHelper.BOOK_TABLE_NAME);
        mDbDataBase.execSQL("delete from " + DbOpenHelper.USER_TABLE_NAME);

        //添加几条数据
        mDbDataBase.execSQL("insert into book values(3,'Android');");
        mDbDataBase.execSQL("insert into book values(4,'SQLite');");
        mDbDataBase.execSQL("insert into book values(5,'IOS');");
        mDbDataBase.execSQL("insert into user values(1,'xzhang',0);");
        mDbDataBase.execSQL("insert into user values(2,'james',1);");
        mDbDataBase.execSQL("insert into user values(3,'stephon',0);");
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.d(TAG, "query(), current thread: " + Thread.currentThread());
        String tableName = getTabName(uri);
        if (null == tableName) {
            throw new IllegalArgumentException("Unsupported uri: " + uri);
        }
        return mDbDataBase.query(tableName, projection, selection, selectionArgs, null, sortOrder, null, null);
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
        String tableName = getTabName(uri);
        if (tableName == null) {
            throw new IllegalArgumentException("Unsupported uri: " + uri);
        }
        mDbDataBase.insert(tableName, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "delete()");
        String tableName = getTabName(uri);
        if (tableName == null) {
            throw new IllegalArgumentException("Unsupported uri: " + uri);
        }
        int count = mDbDataBase.delete(tableName, selection, selectionArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "update()");
        String tableName = getTabName(uri);
        if (tableName == null) {
            throw new IllegalArgumentException("Unsupported uri: " + uri);
        }
        int row = mDbDataBase.update(tableName, values, selection, selectionArgs);
        if (row > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return row;
    }

    private String getTabName(Uri uri) {
        String tableName = null;
        switch (sUriMatcher.match(uri)) {
            case BOOK_URI_CODE:
                tableName = DbOpenHelper.BOOK_TABLE_NAME;
                break;
            case USER_URI_CODE:
                tableName = DbOpenHelper.USER_TABLE_NAME;
                break;
            default:
                break;
        }
        return tableName;
    }
}
