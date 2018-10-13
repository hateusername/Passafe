package com.sitp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String UID = "user";

        String password = "root123";
        //String password = "";

        PasswordStore passwordStore = PasswordStore.getInstance();

        passwordStore.set(this,UID,password);
        try {
            password = passwordStore.getPassword(this,UID);
        } catch (Exception e){
            e.printStackTrace();
        }

        System.out.println(password);

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
}
