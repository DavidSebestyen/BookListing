package com.davids.android.booklisting;



/**
 * Created by krypt on 14/11/2016.
 */

public class Book {
    private String mTitle;
    private String mAuthor;
    private String mUrl;

    public Book (String title, String author, String url){
        mTitle = title;
        mAuthor = author;
        mUrl = url;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getUrl() {
        return mUrl;
    }
}
