package com.example.ictko.SimpleNote.Utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;

public class MyUtils {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getRealPath(Context context, Uri uri){
        String strDocId = DocumentsContract.getDocumentId(uri);
        String[] strSplittedDocId = strDocId.split(":");
        String strId = strSplittedDocId[strSplittedDocId.length-1];

        Cursor crsCursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI ,
                new String[] {MediaStore.MediaColumns.DATA},
                "_id=?",
                new String[]{strId},
                null
        );
        crsCursor.moveToFirst();
        String filePath = crsCursor.getString(0);
        return filePath;
    }
}
