package com.sitp.cipherpart;

//import android.net.PskKeyManager;
//import android.util.Log;

import android.content.Context;

import com.sitp.database.MyDB;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.HashMap;

public class PasswordManager {

    public static PasswordManager instance;
    private static MyDB myDB;
	private PBESystem pbeSystem;
//	private String salt;
//	private String encryptedSessionKey;//KEK加密后的会话密钥
//	private String encryptedMessage;//会话密钥加密的消息
//    private String verificationFile;
//    private String verificationSessionKey;
	
	private PasswordManager(){
	    pbeSystem = new PBESystem();
	}

	public static PasswordManager getInstance(Context context){
	    if(instance == null){
	        instance = new PasswordManager();
	        myDB = MyDB.getInstance(context);
        }
        return instance;
    }

//	private void generateVerificationFile(String label,String password,String salt) throws GeneralSecurityException, UnsupportedEncodingException {
//        pbeSystem.PBEencryption(password, label,salt);
//        verificationFile = pbeSystem.getEncryptedMessage();
//        verificationSessionKey = pbeSystem.getEncryptedSessionKey();
//    }

	public void createPasswordAuto(String label,String password,int length,boolean hasLowerAlphabet,
			boolean hasUpperAlphabet,boolean hasNumber,boolean hasSymbol) throws GeneralSecurityException, UnsupportedEncodingException {
		String message = RandomStr.randomStr(length, hasLowerAlphabet, hasUpperAlphabet, hasNumber, hasSymbol);
        String salt = BCrypt.gensalt();
        //generateVerificationFile(label,password,salt);
		pbeSystem.PBEencryption(password, message,salt);
        String encryptedMessage = pbeSystem.getEncryptedMessage();
        String encryptedSessionKey = pbeSystem.getEncryptedSessionKey();
        myDB.insertItem(label,salt,encryptedSessionKey,encryptedMessage);
	}

	public void createPasswordManual(String label,String password,String message) throws GeneralSecurityException, UnsupportedEncodingException {
        String salt = BCrypt.gensalt();
        //generateVerificationFile(label,password,salt);
        pbeSystem.PBEencryption(password, message,salt);
        String encryptedMessage = pbeSystem.getEncryptedMessage();
        String encryptedSessionKey = pbeSystem.getEncryptedSessionKey();
        myDB.insertItem(label,salt,encryptedSessionKey,encryptedMessage);
    }
	
	public String getPassword(String label,String password) throws GeneralSecurityException, UnsupportedEncodingException {
        HashMap<String,String> hashMap = myDB.queryItem(label);
        if(hashMap == null){
            return null;
        }
        String salt = hashMap.get("salt");
        String encryptedSessionKey = hashMap.get("encryptedSessionKey");
        String encryptedMessage = hashMap.get("encryptedMessage");
		return pbeSystem.PBEdecryption(password,salt,encryptedSessionKey,encryptedMessage);
	}
	
	public void changePasswordAuto(String label,String password,int length,boolean hasLowerAlphabet,
			boolean hasUpperAlphabet,boolean hasNumber,boolean hasSymbol) throws GeneralSecurityException, UnsupportedEncodingException {
		String message = RandomStr.randomStr(length, hasLowerAlphabet, hasUpperAlphabet, hasNumber, hasSymbol);
        String salt = BCrypt.gensalt();
        //generateVerificationFile(label,password,salt);
		pbeSystem.PBEencryption(password, message,salt);
		String encryptedMessage = pbeSystem.getEncryptedMessage();
		String encryptedSessionKey = pbeSystem.getEncryptedSessionKey();
		myDB.updateItem(label,salt,encryptedSessionKey,encryptedMessage);
	}

	public void changePasswordManual(String label,String password,String message) throws GeneralSecurityException, UnsupportedEncodingException {
        String salt = BCrypt.gensalt();
        //generateVerificationFile(label,password,salt);
        pbeSystem.PBEencryption(password, message,salt);
        String encryptedMessage = pbeSystem.getEncryptedMessage();
        String encryptedSessionKey = pbeSystem.getEncryptedSessionKey();
        myDB.updateItem(label,salt,encryptedSessionKey,encryptedMessage);
    }

//    public boolean verifyPassword(String label,String password) throws GeneralSecurityException, UnsupportedEncodingException {
//        String decryptedFile = pbeSystem.PBEdecryption(password,salt,verificationSessionKey,verificationFile);
//        if(decryptedFile.equals(label)){
//            return true;
//        }else{
//            return false;
//        }
//    }
	
	public void deletePassword(String label) throws GeneralSecurityException, UnsupportedEncodingException {
//		boolean verifyResult = verifyPassword(label,password);
//        if (verifyResult == true){
//
//        }else{
//
//        }
        myDB.deleteItem(label);
	}

}
