package com.example.lenovo.sqliteexample_ciphertextstoredatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CiphertextStoreDatabaseOpenHelper extends SQLiteOpenHelper {

    //数据库名称
    public static final String DATABASE_NAME="ciphertext_store.db";
    //数据库版本号
    public static final int DATABASE_VERSION=1;


    //数据库SQL语句 添加一个表
    public CiphertextStoreDatabaseOpenHelper(Context context, String database_name, CursorFactory factory, int database_version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // TODO Auto-generated method stub
        /*String sql = "create table " + TABLE_NAME
                + "("
                + ITEM + " varchar(20) primary key autoincrement, "
                + CIPHERTEXT + " varchar(20) "
                + ")";
        sqLiteDatabase.execSQL(sql);
        */
        sqLiteDatabase.execSQL("create table ciphertext_store(item varchar(20)primary key,ciphertext varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        System.out.println("---版本更新---"+oldVersion+"--->"+newVersion);
    }

}


