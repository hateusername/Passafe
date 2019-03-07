package com.sitp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class MyDBOpenHelper extends SQLiteOpenHelper {

    private volatile static MyDBOpenHelper instance;

    public static MyDBOpenHelper getInstance(Context context){
        if(instance == null){
            synchronized (MyDBOpenHelper.class){
                if(instance == null){
                    instance = new MyDBOpenHelper(context,"passafe.db",null,1);
                }
            }
        }
        return instance;
    }

    private MyDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS items(" +
                "label TEXT PRIMARY KEY," +
                "salt TEXT NOT NULL," +
                "encryptedSessionKey TEXT NOT NULL," +
                "encryptedMessage TEXT NOT NULL)" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
