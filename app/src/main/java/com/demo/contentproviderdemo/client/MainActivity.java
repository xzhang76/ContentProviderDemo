package com.demo.contentproviderdemo.client;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.demo.contentproviderdemo.R;
import com.demo.contentproviderdemo.provider.BookProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private BookRecyclerViewAdapter mAdapter;
    private List<Book> mBooks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.add_book).setOnClickListener(this);
        findViewById(R.id.get_books).setOnClickListener(this);

        mAdapter = new BookRecyclerViewAdapter(mBooks);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_books);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.add_book) {
            ContentValues values = new ContentValues();
            int index = new Random().nextInt(100);
            values.put("_id", index);
            values.put("name", "Android开发艺术探索" + index);
            values.put("author", "ryg");
            getContentResolver().insert(BookProvider.INSERT_URI, values);

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
}
