
package com.example.ictko.SimpleNote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MemoDBHelper extends SQLiteOpenHelper {

    private static MemoDBHelper sInstance;

    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "Memo.db";

/* private static final String SQL_CREATE_ENTRIES = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT,%s TEXT, %s TEXT,%s TEXT)",
                MemoContract.MemoEntry.TABLE_NAME,
                MemoContract.MemoEntry._ID,
                MemoContract.MemoEntry.COLUMN_NAME_TITLE,
                MemoContract.MemoEntry.COLUMN_NAME_CONTENTS,
                MemoContract.MemoEntry.COLUMN_NAME_DATE);*/


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MemoContract.MemoEntry.TABLE_NAME;

    public static MemoDBHelper getInstance(Context context) {
        if(sInstance == null) {
            sInstance = new MemoDBHelper(context);
        }
        return sInstance;
    }
    //생성자
    public MemoDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //DB를 처음으로 사용할 때
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MemoContract.SQL_CREATE_MEMO_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);*/
        if( oldVersion < newVersion){
            db.execSQL("ALTER TABLE " + MemoContract.MemoEntry.TABLE_NAME + " ADD " + MemoContract.MemoEntry.COLUMN_NAME_IMAGE + " TEXT");
        }
    }
}

