package com.sitp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.sitp.passafe_cipherpart.PasswordManager;
import com.sitp.passafe_cipherpart.PasswordStore;
import com.sitp.passafe_cipherpart.R;
import com.sitp.passafe_httpspart.HttpsManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class MainActivity extends AppCompatActivity {

    PasswordStore passwordStore;
    String password;
    String UID = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //String password = "root1234";

        passwordStore = PasswordStore.getInstance();
        //password="user_password";
        //runAddThread();
        runQueryThread();

        /*PasswordManager passwordManager = PasswordManager.getInstance();
        try {
            passwordManager.createPasswordAuto("aaa", password, 10, true, true, true, true);
            String str = passwordManager.getPassword("aaa", password);
            boolean verifyResult = passwordManager.verifyPassword("aaa",password);
            if(verifyResult == true){
                System.out.println("验证成功！"+str);
            }else {
                System.out.println("验证失败！"+str);
            }
            System.out.println("解密后："+str);
            System.out.println("修改密码：");
            passwordManager.changePasswordManual("aaa",password,"mymessage");
            str = passwordManager.getPassword("aaa", password);
            verifyResult = passwordManager.verifyPassword("aaa",password);
            if(verifyResult == true){
                System.out.println("验证成功！"+str);
            }else {
                System.out.println("验证失败！"+str);
            }
            System.out.println("解密后："+str);
            passwordManager.deletePassword("aaa",password);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void runAddThread(){
        new Thread(){
            @Override
            public void run(){
                passwordStore.set(getApplicationContext(),UID,password);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"add successfully!",Toast.LENGTH_LONG).show();
                    }
                });
            }
        }.start();
    }

    private void runQueryThread(){
        new Thread(){
            @Override
            public void run(){
                try {
                    password = passwordStore.getPassword(getApplicationContext(),UID);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"password:"+password,Toast.LENGTH_LONG).show();
                    }
                });
            }
        }.start();
    }

}
