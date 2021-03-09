package com.example.memo;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class MemoItem {
    int _id;
    Drawable picture; // 사진의 경로
    String contents;
    String date; // 글 저장된 날짜

    public MemoItem(int _id, Drawable picture, String contents, String date) {
        this._id = _id;
        this.picture = picture;
        this.contents = contents;
        this.date = date;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public Drawable getPicture() {
        return picture;
    }

    public void setPicture(Drawable picture) {
        this.picture = picture;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
