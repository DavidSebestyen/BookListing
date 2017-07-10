package com.davids.android.booklisting;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class BooksActivity extends AppCompatActivity implements LoaderCallbacks<List<Book>> {
    private BookAdapter mAdapter;

    private EditText filterText;

    private TextView mEmptyStateTextView;

    private static final int BOOK_LOADER_ID = 1;

    private static final String BOOKS_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?&maxResults=30&q=";

    private String searchKeyword = "";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(BOOK_LOADER_ID, null, BooksActivity.this);

        setContentView(R.layout.activity_books);

        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        ListView bookListView = (ListView) findViewById(R.id.list);

        bookListView.setAdapter(mAdapter);


        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Book currentBook = mAdapter.getItem(position);

                Uri bookUri = Uri.parse(currentBook.getUrl());

                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                startActivity(websiteIntent);
            }
        });

        Button searchButton = (Button) findViewById(R.id.search_button);

        filterText = (EditText) findViewById(R.id.editText);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
    }

    private void search(){

        if(isNetworkAvailable(this)){
            filterText.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    searchKeyword = s.toString().replace(' ', '+');

                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            getLoaderManager().restartLoader(BOOK_LOADER_ID, null, BooksActivity.this);
            if (filterText.getText().toString().isEmpty()) {
            Context context = getApplicationContext();
            CharSequence text = "You did not enter anything";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            return;
        }} else {
            Toast.makeText(BooksActivity.this, "No internet connectivity", Toast.LENGTH_SHORT).show();
        }

        }
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connMgr.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        return new BookLoader(this, BOOKS_REQUEST_URL + searchKeyword);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {

        mEmptyStateTextView.setText(R.string.no_books);
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        mAdapter.clear();

        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {

        mAdapter.clear();

    }
}