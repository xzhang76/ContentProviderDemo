package com.demo.contentproviderdemo.client;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.demo.contentproviderdemo.R;

import java.util.ArrayList;
import java.util.List;

public class BookRecyclerViewAdapter extends RecyclerView.Adapter<BookRecyclerViewAdapter.BookViewHolder>{

    private List<Book> mBooks;

    public BookRecyclerViewAdapter(List<Book> books) {
        if (mBooks == null) {
            mBooks = new ArrayList<>();
        } else {
            mBooks.clear();
        }
        if (books != null) {
            mBooks.addAll(books);
        }
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item_layout, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        holder.mBookName.setText(mBooks.get(position).mBookName);
        holder.mBookAuthor.setText(mBooks.get(position).mBookAuthor);

    }

    @Override
    public int getItemCount() {
        return null != mBooks ? mBooks.size() : 0;
    }

    public void updateBooks(List<Book> books) {
        if (mBooks == null) {
            mBooks = new ArrayList<>();
        } else {
            mBooks.clear();
        }
        if (books != null) {
            mBooks.addAll(books);
        }
        notifyDataSetChanged();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {

        public TextView mBookName;
        public TextView mBookAuthor;

        public BookViewHolder(View itemView) {
            super(itemView);
            mBookName = (TextView) itemView.findViewById(R.id.book_name);
            mBookAuthor = (TextView) itemView.findViewById(R.id.book_author);
        }
    }
}
