package com.sitp.cipherpart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;

import com.sitp.httpspart.HttpsManager;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class PasswordStore {

    //private String encryptedPassword;
    //private String keystring;
    public static final String DES = "DES";
    public static PasswordStore instance;
    private SharedPreferences sp;

    private PasswordStore() {
    }

    public static PasswordStore getInstance() {
        if (instance == null) {
            instance = new PasswordStore();
        }
        return instance;
    }

    //第一次创建用户调用该方法，将password加密后的字符串保存，将密钥发给服务器
    public void set(final Context context, final String UID, final String password) {
        //store encryptedPassword
        //and encryptedUID which is encrypted by password
        //send keystring to server
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AesCbcWithIntegrity.SecretKeys keys = AesCbcWithIntegrity.generateKey();
                    final String keystring = keys.toString();
                    AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = AesCbcWithIntegrity.encrypt(password, keys);
                    final String encryptedPassword = cipherTextIvMac.toString();
                    byte[] encryptedUIDbyte = encrypt(UID,password);
                    sp = context.getSharedPreferences("User", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("encryptedPassword", encryptedPassword);
                    editor.putString("encryptedUID", new String(Base64.encode(encryptedUIDbyte, Base64.NO_WRAP), "ISO8859-1"));
                    editor.putString("UID", UID);
                    editor.commit();

                    HttpsManager httpsManager = HttpsManager.getInstance();
                    httpsManager.sendKeystring(context, UID, keystring);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    //指纹验证通过后调用
    public String getPassword(final Context context) throws UnsupportedEncodingException, GeneralSecurityException {

        sp = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        String encryptedPassword = sp.getString("encryptedPassword", "");
        String UID = sp.getString("UID", null);
        System.out.println("!!!here:" + encryptedPassword);
        String keystring = "";

        HttpsManager httpsManager = HttpsManager.getInstance();
        try {
            keystring = httpsManager.queryKeystring(context, UID);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("!!!exception:" + e.getMessage());
        }

        System.out.println("!!!passwordStore:get password's key:" + keystring);

        AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = new AesCbcWithIntegrity.CipherTextIvMac(encryptedPassword);
        return AesCbcWithIntegrity.decryptString(cipherTextIvMac, AesCbcWithIntegrity.keys(keystring));
    }

    //使用密码验证时调用
    public boolean verifyPassword(final Context context, final String password) throws Exception {
        sp = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        String UID = sp.getString("UID", null);
        String encryptedUID = sp.getString("encryptedUID", null);
        byte[] bs = Base64.decode(encryptedUID.getBytes("ISO8859-1"), Base64.NO_WRAP);
        String iv = md5(password).substring(0, 16);
        byte[] decryptedBS = decrypt(bs, password);
        if(decryptedBS == null){
            return false;
        }
        String ans = new String(decryptedBS, "ISO8859-1");
        if (UID.equals(ans)) {
            return true;
        } else {
            return false;
        }
//        ExecutorService threadPool = Executors.newSingleThreadExecutor();
//        Future<Boolean> future = threadPool.submit(new Callable<Boolean>() {
//            @Override
//            public Boolean call() throws Exception {
//
//            }
//        });
//
//        return future.get();
    }

    public static String md5(String plainText) {
        //定义一个字节数组
        byte[] secretBytes = null;
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            //对字符串进行加密
            md.update(plainText.getBytes());
            //获得加密后的数据
            secretBytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }
        //将加密后的数据转换为16进制数字
        String md5code = new BigInteger(1, secretBytes).toString(16);// 16进制数字
        // 如果生成数字未满32位，需要前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

    private static byte[] decrypt(byte[] mEncrypt, String key) throws Exception {
        if(key.length() < 8){
            return null;
        }
        DESKeySpec keySpec = new DESKeySpec(key.getBytes());
        SecretKey secret = SecretKeyFactory.getInstance(DES).generateSecret(keySpec);
        Cipher cipher = Cipher.getInstance(DES);
        cipher.init(Cipher.DECRYPT_MODE, secret);
        byte[] bs = null;
        try{
            bs = cipher.doFinal(mEncrypt);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return bs;
        }
    }


    private static byte[] encrypt(String input, String key) throws Exception{

//密匙规范--把自己想变成密匙的字符串规范成密匙对象
        DESKeySpec keySpec = new DESKeySpec(key.getBytes());
//通过密匙工厂获取密匙
        SecretKey secretKey = SecretKeyFactory.getInstance(DES).generateSecret(keySpec);

//获取加密对象
        Cipher cipher = Cipher.getInstance(DES);
//初始化对象
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//进行加密
        byte[] bs = cipher.doFinal(input.getBytes());
        System.out.println("加密后:"+new String(bs));
        return bs;
    }

}
