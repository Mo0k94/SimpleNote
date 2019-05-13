package com.example.ictko.SimpleNote;

import java.io.Serializable;

/**
 * Created by user on 2018-01-01.
 */

public class Memo implements Serializable {

    private long id;
    private String title;
    private String contents;
    private String date;
    private String img_uri;

    public Memo(String title, String contents, String date) {
        //this.id = id;
        this.title = title;
        this.contents = contents;
        this.date = date;
        //this.img_uri = img_uri;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImg_uri() {
        return img_uri;
    }

    public void setImg_uri(String img_uri) {
        this.img_uri = img_uri;
    }

    @Override
    public String toString() {
        return "Memo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", date='" + date + '\'' +
                ", img_uri='" + img_uri + '\'' +
                '}';
    }
}

