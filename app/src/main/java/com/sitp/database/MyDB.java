package com.sitp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MyDB {
    private static MyDB instance = new MyDB();

    private static MyDBOpenHelper dbOpenHelper;
    private SQLiteDatabase database;

    private AtomicInteger mOpenCounter = new AtomicInteger();

    public static MyDB getInstance(Context context){
        dbOpenHelper = MyDBOpenHelper.getInstance(context);
        if(instance == null){
            synchronized (MyDB.class){
                if(instance == null){
                    instance = new MyDB();
                }
            }
        }
        return instance;
    }

    private MyDB(){
    }

    private SQLiteDatabase getSQLiteDatabase(){
        if(mOpenCounter.incrementAndGet() == 1 || database == null){
            database = dbOpenHelper.getWritableDatabase();
        }
        return database;
    }

    private void closeSQLiteDatabase(){
        if(mOpenCounter.decrementAndGet() == 0){
            database.close();
        }
    }

    public void insertItem(String label, String salt, String encryptedSessionKey, String encryptedMessage){
        ContentValues values = new ContentValues();
        values.put("label",label);
        values.put("salt",salt);
        values.put("encryptedSessionKey",encryptedSessionKey);
        values.put("encryptedMessage",encryptedMessage);
        database = getSQLiteDatabase();
        try{
            database.insert("items",null,values);
        }catch (Exception e){
            Log.e("SQLERROR","In SQLDA: " + e.getMessage() + "insertItem");
        }finally {
            closeSQLiteDatabase();
        }
    }

    public HashMap<String,String> queryItem(String label){
        HashMap<String,String> hashMap = new HashMap<>();
        database = getSQLiteDatabase();
        String sql = "SELECT salt,encryptedSessionKey,encryptedMessage FROM items WHERE label = '" + label + "'";
        Cursor cursor = database.rawQuery(sql,null);
        if(cursor.getCount() == 0){
            return null;
        }
        cursor.moveToFirst();
        String salt = cursor.getString(cursor.getColumnIndex("salt"));
        String encryptedSessionKey = cursor.getString(cursor.getColumnIndex("encryptedSessionKey"));
        String encryptedMessage = cursor.getString(cursor.getColumnIndex("encryptedMessage"));
        hashMap.put("salt",salt);
        hashMap.put("encryptedSessionKey",encryptedSessionKey);
        hashMap.put("encryptedMessage",encryptedMessage);
        cursor.close();
        return hashMap;
    }

    public void updateItem(String label,String salt,String encryptedSessionKey,String encryptedMessage){
        database = getSQLiteDatabase();
        try{
            database.execSQL("UPDATE items SET " +
                    "salt = '" + salt + "'," +
                    "encryptedSessionKey = '" + encryptedSessionKey + "'," +
                    "encryptedMessage = '" + encryptedMessage + "' WHERE label = '" + label + "'");
        }catch (Exception e){
            Log.e("SQLERROR","In SQLDA: " + e.getMessage() + "updateItem");
        }finally {
            closeSQLiteDatabase();
        }
    }

    public void deleteItem(String label){
        database = getSQLiteDatabase();
        try{
            database.execSQL("DELETE FROM items WHERE label = '" + label + "'");
        }catch (Exception e){
            Log.e("SQLERROR","In SQLDA: " + e.getMessage() + "clearTable");
        }finally {
            closeSQLiteDatabase();
        }
    }
}
