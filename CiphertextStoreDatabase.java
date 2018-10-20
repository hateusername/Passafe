package com.example.lenovo.sqliteexample_ciphertextstoredatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class CiphertextStoreDatabase {

    private SQLiteDatabase sqLiteDatabase;
    private CiphertextStoreDatabaseOpenHelper ciphertextStoreDatabaseOpenHelper;
    private static final String TABLE_NAME="ciphertext_store";
    private static final String ITEM="item";
    private static final String CIPHERTEXT="ciphertext";

    public CiphertextStoreDatabase(Context context, String database_name, int database_version){
        ciphertextStoreDatabaseOpenHelper=new CiphertextStoreDatabaseOpenHelper(context,database_name,null,database_version);
    };

    //插入条目成功则返回true，不成功（要输入的条目已存在，可以考虑调用updateItem函数）返回false
    public Boolean insertItem(String string_item, String string_ciphertext) {
        sqLiteDatabase = ciphertextStoreDatabaseOpenHelper.getReadableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(ITEM,string_item);
        contentValues.put(CIPHERTEXT,string_ciphertext);
        if(sqLiteDatabase.insert(TABLE_NAME,null,contentValues)==-1) {
            sqLiteDatabase.close();
            return false;
        }
        else{
            sqLiteDatabase.close();
            return true;
        }
    }

    public void deleteItem(String string_item) {
        sqLiteDatabase = ciphertextStoreDatabaseOpenHelper.getReadableDatabase();
        sqLiteDatabase.delete(TABLE_NAME, ITEM + " = ?", new String[]{string_item});
        sqLiteDatabase.close();
    }

    public void updateItem(String string_item, String string_ciphertext) {
        sqLiteDatabase = ciphertextStoreDatabaseOpenHelper.getReadableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(ITEM,string_item);
        contentValues.put(CIPHERTEXT,string_ciphertext);
        sqLiteDatabase.update(TABLE_NAME, contentValues, ITEM + " = ?",new String[]{string_item});
        sqLiteDatabase.close();
    }

    public String getCiphertext(String string_item) {
        String string_ciphertext="";
        sqLiteDatabase = ciphertextStoreDatabaseOpenHelper.getReadableDatabase();
        try{
            Cursor cursor = sqLiteDatabase.query(TABLE_NAME, new String []{ITEM,CIPHERTEXT}, ITEM+"=?", new String[]{string_item}, null, null, null, null);// 注意空格！
            while(cursor.moveToNext())
            {
                string_ciphertext=cursor.getString(cursor.getColumnIndex(CIPHERTEXT));;
                break;
            }
            sqLiteDatabase.close();
        }catch(SQLiteException e){
            CreateTable();
        }
        return string_ciphertext;
    }

    private void CreateTable() {
        // TODO Auto-generated method stub
        try{
            sqLiteDatabase.execSQL("create table if not exists ciphertext_store(item varchar(20)primary key,ciphertext varchar(20))");
        }catch(SQLException ex){}
    }

}

