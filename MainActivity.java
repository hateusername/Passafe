package com.example.lenovo.sqliteexample_ciphertextstoredatabase;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.lenovo.sqliteexample_ciphertextstoredatabase.CiphertextStoreDatabaseOpenHelper.DATABASE_NAME;
import static com.example.lenovo.sqliteexample_ciphertextstoredatabase.CiphertextStoreDatabaseOpenHelper.DATABASE_VERSION;

public class MainActivity extends AppCompatActivity {

    private EditText mEtItem;
    private EditText mEtCiphertext;
    private Button mBtnInsert;
    private Button mBtnDelete;
    private Button mBtnUpdate;
    private Button mBtnQuery;
    private TextView mTvContent;
    private String string_item;
    private String string_ciphertext;
    private String string_content;
    private CiphertextStoreDatabase ciphertextStoreDatabase;
    private CiphertextStoreDatabaseOpenHelper ciphertextStoreDatabaseOpenHelper;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEtItem=findViewById(R.id.et_item);
        mEtCiphertext=findViewById(R.id.et_ciphertext);
        mBtnInsert=findViewById(R.id.btn_insert);
        mBtnDelete=findViewById(R.id.btn_delete);
        mBtnUpdate=findViewById(R.id.btn_update);
        mBtnQuery=findViewById(R.id.btn_query);
        mTvContent=findViewById(R.id.tv_content);
        string_item="";
        string_ciphertext="";
        string_content="";
        ciphertextStoreDatabase=new CiphertextStoreDatabase(MainActivity.this,DATABASE_NAME,DATABASE_VERSION);

        mBtnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                string_item=mEtItem.getText().toString();
                string_ciphertext=mEtCiphertext.getText().toString();

                /*ciphertextStoreDatabaseOpenHelper=new CiphertextStoreDatabaseOpenHelper(MainActivity.this,DATABASE_NAME,null,DATABASE_VERSION);
                sqLiteDatabase = ciphertextStoreDatabaseOpenHelper.getReadableDatabase();
                sqLiteDatabase.execSQL("insert into ciphertext_store(item,ciphertext) values(?,?)",new String[]{string_item,string_ciphertext});
                */

                ciphertextStoreDatabase.insertItem(string_item,string_ciphertext);
            }
        });
        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                string_item=mEtItem.getText().toString();
                ciphertextStoreDatabase.deleteItem(string_item);
            }
        });
        mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                string_item=mEtItem.getText().toString();
                string_ciphertext=mEtCiphertext.getText().toString();
                ciphertextStoreDatabase.updateItem(string_item,string_ciphertext);
            }
        });
        mBtnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                string_item=mEtItem.getText().toString();
                string_ciphertext=ciphertextStoreDatabase.getCiphertext(string_item);
                mEtCiphertext.setText(string_ciphertext);
            }
        });

    }

}
