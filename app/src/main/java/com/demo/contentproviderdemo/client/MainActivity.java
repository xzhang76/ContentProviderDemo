package com.demo.contentproviderdemo.client;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.contentproviderdemo.BaseActivity;
import com.demo.contentproviderdemo.R;
import com.demo.contentproviderdemo.provider.BookProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private BookRecyclerViewAdapter mAdapter;
    private List<Book> mBooks = new ArrayList<>();

    private TextView mNewBookName;
    private TextView mNewBookAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.add_book).setOnClickListener(this);
        findViewById(R.id.get_books).setOnClickListener(this);

        mNewBookName = (TextView) findViewById(R.id.last_book_name);
        mNewBookAuthor = (TextView) findViewById(R.id.last_book_author);

        mAdapter = new BookRecyclerViewAdapter(mBooks);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_books);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        getContentResolver().registerContentObserver(BookProvider.BOOK_CONTENT_URI, true, mContentObserver);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.add_book) {
            int index = 0;
            Cursor bookCursor = getContentResolver().query(BookProvider.QUERY_ALL_URI, new String[]{BookProvider.KEY_ID, BookProvider.KEY_NAME, BookProvider.KEY_AUTHOR}, null, null, null);
            if (bookCursor != null) {
                index = bookCursor.getCount();
                bookCursor.close();
            }
            ContentValues values = new ContentValues();
            values.put("_id", index);
            values.put("name", "Android开发艺术探索" + index);
            values.put("author", "ryg");
            getContentResolver().insert(BookProvider.BOOK_CONTENT_URI, values);

        } else if (id == R.id.get_books) {
            if (mBooks == null) {
                mBooks = new ArrayList<>();
            } else {
                mBooks.clear();
            }
            Cursor bookCursor = getContentResolver().query(BookProvider.QUERY_ALL_URI, new String[]{BookProvider.KEY_ID, BookProvider.KEY_NAME, BookProvider.KEY_AUTHOR}, null, null, null);
            if (bookCursor != null) {
                while (bookCursor.moveToNext()) {
                    Book book = new Book(bookCursor.getString(1), bookCursor.getString(2));
                    mBooks.add(book);
                }
                bookCursor.close();
            }
            mAdapter.updateBooks(mBooks);
        }
    }

    @Override
    protected void handleMessage(Message message) {
        switch (message.what) {
            case MSG_CONTENT_UPDATE:
                Toast.makeText(this, "新书到了！", Toast.LENGTH_SHORT).show();
                if (message.obj != null && message.obj instanceof Book) {
                    Book newBook = (Book) message.obj;
                    mNewBookName.setText("new book: " + newBook.mBookName);
                    mNewBookAuthor.setText(newBook.mBookAuthor);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mContentObserver);

    }
}
