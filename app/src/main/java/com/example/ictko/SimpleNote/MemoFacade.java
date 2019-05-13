package com.example.ictko.SimpleNote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MemoFacade {

    private MemoDBHelper mDbHelper;

    public MemoFacade(Context context) {
        mDbHelper = new MemoDBHelper(context);
    }

    /*
     * 메모를 추가(삽입)한다
     * title 제목
     * content 내용
     * date 날짜
     * return 추가된 row  의 id, 만약 에러가 발생되면 -1
     * */
    public long insert(String title, String contents, String date, String imageUri) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // 이거 한줄로도 됨
        // db.execSQL (title, content , date) ("INSERT INTO memo VALUES ('" + title + "', '" + content + "' + '" + date + "')");
        ContentValues values = new ContentValues();
        values.put(MemoContract.MemoEntry.COLUMN_NAME_TITLE, title);
        values.put(MemoContract.MemoEntry.COLUMN_NAME_CONTENTS, contents);
        values.put(MemoContract.MemoEntry.COLUMN_NAME_DATE, date);
        if(imageUri != null){
            values.put(MemoContract.MemoEntry.COLUMN_NAME_IMAGE,imageUri);
        }

        long newRowId = db.insert(MemoContract.MemoEntry.TABLE_NAME, null, values);

        return newRowId;
    }


    /*
     * 전체 메모 리스트
     * @return 전체 메모
     * */
    public List<Memo> getMemoList(String selection,
                                  String[] selectionArgs,
                                  String groupBy,
                                  String having,
                                  String orderBy) {

        ArrayList<Memo> memoArrayList = new ArrayList<>();
        // DB에서 읽어오기
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // ORDER BY _ID DESC  같음
        String order = MemoContract.MemoEntry._ID + " DESC";

        Cursor c = db.query(
                MemoContract.MemoEntry.TABLE_NAME,      // table name
                null,                           //columns to return 리턴할 컬럼들
                selection,                             //columns for the Where clause 조건
                selectionArgs,                           //values for the
                groupBy,                                //don`t group the rows
                having,                                  //don`t filter by row group
                orderBy == null ? order : orderBy        // sort order
        );

        if (c != null) {
            // 커서를 Memo로 변환
            //c.moveToFirst(); 커서를 처음 항목으로 이동
            while (c.moveToNext()) {
                String title = c.getString(
                        c.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_TITLE));
                String contents = c.getString(
                        c.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_CONTENTS));
                String date = c.getString(
                        c.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_DATE));
                long id = c.getLong(
                        c.getColumnIndexOrThrow(MemoContract.MemoEntry._ID));
                String imageUri = c.getString(
                        c.getColumnIndexOrThrow(
                                MemoContract.MemoEntry.COLUMN_NAME_IMAGE
                        )
                );
                Memo memo = new Memo(title, contents, date);
                memo.setId(id);
                memo.setImg_uri(imageUri);
                memoArrayList.add(memo);
                Log.d("LOG", "getMemoList : " + memo);
            }
            // 커서 닫기
            c.close();
        }
        return memoArrayList;
    }

    /**
     * 전체 메모 리스트
     *
     * @return 전체 메모
     */
    public List<Memo> getMemoList() {
        return getMemoList(null, null, null, null, null);
    }


    /*
     * 메모 삭제
     * param id 삭제할 메모 id
     * return 삭제된 행의수
     * */
    public int delete(long id) {
        //Define 'where' part of query
        //TODO 방법1
       /* String selection = MemoContract.MemoEntry._ID + " = ?";
        //Specify arguments in placeholder order;
        String[] selectionArgs = { String.valueOf(id) };
        // Issue SQL statement.
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int deleted = db.delete(MemoContract.MemoEntry.TABLE_NAME,
                        selection,
                        selectionArgs);*/
        //TODO 방법2
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int deleted = db.delete(MemoContract.MemoEntry.TABLE_NAME,
                "_id=" + id,
                null);
        return deleted;
    }

    public int update(long id, String title, String contents, String date, String imageUri) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(MemoContract.MemoEntry.COLUMN_NAME_TITLE, title);
        values.put(MemoContract.MemoEntry.COLUMN_NAME_CONTENTS, contents);
        values.put(MemoContract.MemoEntry.COLUMN_NAME_DATE, date);
        if(imageUri != null){
            values.put(MemoContract.MemoEntry.COLUMN_NAME_IMAGE,imageUri);
        }
        int count = db.update(
                MemoContract.MemoEntry.TABLE_NAME,
                values,
                MemoContract.MemoEntry._ID + " = " + id,
                null);
        return count;
    }

}
