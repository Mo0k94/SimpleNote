
package com.example.ictko.SimpleNote;

import android.provider.BaseColumns;

//메모장의 스키마(구조)
public final class MemoContract {


    /* CREATE TABLE memo
    * (
    *   _id INTEGER PRIMARY KEY AUTOINCREMENT,
    *   title TEXT,
    *   contents TEXT,
    *   date TEXT
    * );
    * */

    public static final String SQL_CREATE_MEMO_TABLE =
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT,%s TEXT);",
                    MemoEntry.TABLE_NAME,
                    MemoEntry._ID,
                    MemoEntry.COLUMN_NAME_TITLE,
                    MemoEntry.COLUMN_NAME_CONTENTS,
                    MemoEntry.COLUMN_NAME_DATE,
                    MemoEntry.COLUMN_NAME_IMAGE);

    private MemoContract() {

    }
    public static class MemoEntry implements BaseColumns {
        public static final String TABLE_NAME = "memo";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CONTENTS = "contents";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_IMAGE = "image";
    }
}

