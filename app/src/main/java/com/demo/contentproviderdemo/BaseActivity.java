package com.demo.contentproviderdemo;

import android.content.ContentUris;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.demo.contentproviderdemo.client.Book;
import com.demo.contentproviderdemo.provider.BookProvider;

import java.lang.ref.WeakReference;

public abstract class BaseActivity extends AppCompatActivity {

    protected MyHandler mHandler = new MyHandler(this);
    protected BookContentObserver mContentObserver;

    protected static final int MSG_CONTENT_UPDATE = 0x0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContentObserver = new BookContentObserver(mHandler);
    }

    protected static class MyHandler extends Handler {

        private WeakReference<BaseActivity> mActivity;

        MyHandler(BaseActivity activity) {
            this.mActivity = new WeakReference<>(activity);

        }

        @Override
        public void handleMessage(Message msg) {
            BaseActivity activity = mActivity.get();
            activity.handleMessage(msg);
            super.handleMessage(msg);
        }
    }

    protected abstract void handleMessage(Message message);

    protected class BookContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public BookContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            long id = ContentUris.parseId(uri);
            Uri queryUri = ContentUris.withAppendedId(BookProvider.QUERY_ITEM_URI, id);
            Cursor bookCursor = getContentResolver().query(queryUri,
                    new String[]{BookProvider.KEY_ID, BookProvider.KEY_NAME, BookProvider.KEY_AUTHOR}, null, null, null);
            if (bookCursor != null) {
                while (bookCursor.moveToNext()) {
                    Book book = new Book(bookCursor.getString(1), bookCursor.getString(2));
                    Message msg = Message.obtain();
                    msg.what = MSG_CONTENT_UPDATE;
                    msg.obj = book;
                    mHandler.handleMessage(msg);
                }
                bookCursor.close();
            }
        }
    }
}
