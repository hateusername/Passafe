package com.sitp.passafe_cipherpart;

import android.content.Context;
import android.content.SharedPreferences;

import com.sitp.MainActivity;
import com.sitp.passafe_httpspart.HttpsManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class PasswordStore {

    private String storeLocation = "";
    private String encryptedPassword;
    private String keystring;
    public static PasswordStore instance;
    private SharedPreferences sp;

    private PasswordStore(){
    }

    public static PasswordStore getInstance(){
        if(instance == null){
            instance = new PasswordStore();
        }
        return instance;
    }

    //第一次创建用户调用该方法，将password加密后的字符串保存，将密钥发给服务器
    public void set(final Context context, final String UID, String password) {
        try {
            AesCbcWithIntegrity.SecretKeys keys = AesCbcWithIntegrity.generateKey();
            keystring = keys.toString();
            AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac =
                    AesCbcWithIntegrity.encrypt(password, keys);
            encryptedPassword = cipherTextIvMac.toString();

            //todo: store encryptedPassword
            sp = context.getSharedPreferences("User",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("encryptedPassword",encryptedPassword);
            editor.commit();

            //send keystring to server
            new Thread(new Runnable() {
                @Override
                public void run() {

                    HttpsManager httpsManager = HttpsManager.getInstance();
                    try {
                        httpsManager.sendKeystring(context,UID,keystring);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getPassword(final Context context, final String UID) throws UnsupportedEncodingException, GeneralSecurityException {

        sp = context.getSharedPreferences("User",Context.MODE_PRIVATE);
        encryptedPassword = sp.getString("encryptedPassword","");
        System.out.println("!!!here:"+encryptedPassword);

        HttpsManager httpsManager = HttpsManager.getInstance();
        try {
            keystring = httpsManager.queryKeystring(context,UID);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("!!!exception:"+e.getMessage());
        }

        System.out.println("!!!passwordStore:getPassword:"+keystring);

        //get keystring from server
        /*Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                HttpsManager httpsManager = HttpsManager.getInstance();
                try {
                    keystring = httpsManager.queryKeystring(context,UID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/

        AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = new AesCbcWithIntegrity.CipherTextIvMac(encryptedPassword);
        return AesCbcWithIntegrity.decryptString(cipherTextIvMac,AesCbcWithIntegrity.keys(keystring));
    }

}
