package com.sitp.passafe_cipherpart;

import android.net.PskKeyManager;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

public class PasswordManager {

    public static PasswordManager instance;
	private PBESystem pbeSystem;
	private String salt;
	private String encryptedSessionKey;//KEK加密后的会话密钥
	private String encryptedMessage;//会话密钥加密的消息
    private String vertificationFile;
    private String vertificationSessionKey;
	
	private PasswordManager(){

	    pbeSystem = new PBESystem();

	    //todo:connect DB
	}

	public static PasswordManager getInstance(){
	    if(instance == null){
	        instance = new PasswordManager();
        }
        return instance;
    }

	private void generateVerificationFile(String label,String password,String salt) throws GeneralSecurityException, UnsupportedEncodingException {
        pbeSystem.PBEencryption(password, label,salt);
        vertificationFile = pbeSystem.getEncryptedMessage();
        vertificationSessionKey = pbeSystem.getEncryptedSessionKey();
    }

	public void createPasswordAuto(String label,String password,int length,boolean hasLowerAlphabet,
			boolean hasUpperAlphabet,boolean hasNumber,boolean hasSymbol) throws GeneralSecurityException, UnsupportedEncodingException {
		String message = RandomStr.randomStr(length, hasLowerAlphabet, hasUpperAlphabet, hasNumber, hasSymbol);
        salt = BCrypt.gensalt();
        generateVerificationFile(label,password,salt);
		pbeSystem.PBEencryption(password, message,salt);
		encryptedMessage = pbeSystem.getEncryptedMessage();
		encryptedSessionKey = pbeSystem.getEncryptedSessionKey();
	}

	public void createPasswordManual(String label,String password,String message) throws GeneralSecurityException, UnsupportedEncodingException {
        salt = BCrypt.gensalt();
        generateVerificationFile(label,password,salt);
        pbeSystem.PBEencryption(password, message,salt);
        encryptedMessage = pbeSystem.getEncryptedMessage();
        encryptedSessionKey = pbeSystem.getEncryptedSessionKey();
    }
	
	public String getPassword(String label,String password) throws GeneralSecurityException, UnsupportedEncodingException {
		//long salt = 0;
		//byte[] encryptedSessionKey = null,encryptedMessage = null;
		//TODO:
		//查询数据库得到以上三项
		return pbeSystem.PBEdecryption(password,salt,encryptedSessionKey,encryptedMessage);
	}
	
	public void changePasswordAuto(String label,String password,int length,boolean hasLowerAlphabet,
			boolean hasUpperAlphabet,boolean hasNumber,boolean hasSymbol) throws GeneralSecurityException, UnsupportedEncodingException {
		String message = RandomStr.randomStr(length, hasLowerAlphabet, hasUpperAlphabet, hasNumber, hasSymbol);
        salt = BCrypt.gensalt();
        generateVerificationFile(label,password,salt);
		pbeSystem.PBEencryption(password, message,salt);
		encryptedMessage = pbeSystem.getEncryptedMessage();
		encryptedSessionKey = pbeSystem.getEncryptedSessionKey();
		//TODO:
		//只需修改数据库相关记录即可
	}

	public void changePasswordManual(String label,String password,String message) throws GeneralSecurityException, UnsupportedEncodingException {
        salt = BCrypt.gensalt();
        generateVerificationFile(label,password,salt);
        pbeSystem.PBEencryption(password, message,salt);
        encryptedMessage = pbeSystem.getEncryptedMessage();
        encryptedSessionKey = pbeSystem.getEncryptedSessionKey();
    }

    public boolean verifyPassword(String label,String password) throws GeneralSecurityException, UnsupportedEncodingException {
        String decryptedFile = pbeSystem.PBEdecryption(password,salt,vertificationSessionKey,vertificationFile);
        if(decryptedFile.equals(label)){
            return true;
        }else{
            return false;
        }
    }
	
	public void deletePassword(String label,String password) throws GeneralSecurityException, UnsupportedEncodingException {
		boolean verifyResult = verifyPassword(label,password);
        if (verifyResult == true){
            //TODO
        }else{
            //TODO
        }
	}

	public String getEncryptedMessage(){
        return encryptedMessage;
    }

}
