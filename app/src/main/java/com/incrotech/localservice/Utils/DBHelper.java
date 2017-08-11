package com.incrotech.localservice.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jeet on 4/8/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String Name ="LocalServices";
    private static final int version=1;

    public DBHelper(Context context) {
        super(context, Name, null, version);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_CONTACTS_TABLE = "CREATE TABLE Local_Services (Id TEXT,Title TEXT,Priority TEXT,Currency Text, Budget TEXT,Budget_amount Text, Description Text, File text )";
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
