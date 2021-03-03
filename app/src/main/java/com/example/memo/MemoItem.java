package com.example.memo;

import android.content.Context;

import java.util.ArrayList;

public class MemoItem {
    int _id;
    String title;
    String contents;

    public MemoItem(int _id, String title, String contents) {
        this._id = _id;
        this.title = title;
        this.contents = contents;
    }


    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
