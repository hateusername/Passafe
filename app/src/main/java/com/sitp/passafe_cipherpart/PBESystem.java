package com.sitp.passafe_cipherpart;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.MotionEvent;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.MessageDigest;

public class PBESystem {

	private String encryptedSessionKey;//KEK加密后的会话密钥
	private String encryptedMessage;//会话密钥加密的消息
    String KEK;
    String CEK;
	private final byte[] IV = new byte[] { 1, 2, 3, 4, 5, 6, 7,
            8, 9, 10, 11, 12, 13, 14, 15, 16 };
	
	private static ByteBuffer buffer = ByteBuffer.allocate(8);
	
	
	public void PBEencryption(String password,String message,String salt) throws GeneralSecurityException, UnsupportedEncodingException {

		//Step 1 : generate KEK
        String HMACresult = BCrypt.hashpw(password,salt);
        byte [] hashInput = HMACresult.getBytes();
        MessageDigest sha = MessageDigest.getInstance("sha-256");
        sha.update(hashInput);
        byte [] hashOutput = sha.digest();
        String KEK_sha256_base64 = Base64.encodeToString(hashOutput, Base64.DEFAULT);
		String KEK_md5_base64 = Base64.encodeToString(md5(hashInput),Base64.DEFAULT);
		KEK = KEK_md5_base64 + ":" + KEK_sha256_base64;

        //Step 2 : generate CEK and encrypt it
        AesCbcWithIntegrity.SecretKeys keys = AesCbcWithIntegrity.generateKey();
		CEK = keys.toString();
        AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac =
                AesCbcWithIntegrity.encrypt(CEK,AesCbcWithIntegrity.keys(KEK));
        encryptedSessionKey = cipherTextIvMac.toString();

        //Step 3 : use CEK to encrypt message
        cipherTextIvMac = AesCbcWithIntegrity.encrypt(message,keys);
        encryptedMessage = cipherTextIvMac.toString();


    }
	
	public String PBEdecryption(String password, String salt, String encryptedSessionKey, String encryptedMessage)
            throws GeneralSecurityException, UnsupportedEncodingException {
        //Step 1 : re-generate KEK
        String HMACresult = BCrypt.hashpw(password,salt);
        byte [] hashInput = HMACresult.getBytes();
        MessageDigest sha = MessageDigest.getInstance("sha-256");
        sha.update(hashInput);
        byte [] hashOutput = sha.digest();
		String KEK_sha256_base64 = Base64.encodeToString(hashOutput, Base64.DEFAULT);
		String KEK_md5_base64 = Base64.encodeToString(md5(hashInput),Base64.DEFAULT);
		KEK = KEK_md5_base64 + ":" + KEK_sha256_base64;

        //Step 2 : decrypt encryptedSessionKey to CEK
        AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = new AesCbcWithIntegrity.CipherTextIvMac(encryptedSessionKey);
        CEK = AesCbcWithIntegrity.decryptString(cipherTextIvMac,AesCbcWithIntegrity.keys(KEK));

        //Step 3 : decrypt encryptedMessage to message
        cipherTextIvMac = new AesCbcWithIntegrity.CipherTextIvMac(encryptedMessage);
        String message = AesCbcWithIntegrity.decryptString(cipherTextIvMac,AesCbcWithIntegrity.keys(CEK));
        return message;
	}
	
	public String getEncryptedSessionKey() {
		return encryptedSessionKey;
	}
	
	public String getEncryptedMessage() {
		return encryptedMessage;
	}
	
	public byte[] md5(byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest mDigest = null;
		mDigest = MessageDigest.getInstance("MD5");
		byte[] digest = mDigest.digest(bytes);
		return digest;
	}
	
	public static byte[] longToBytes(long x) {
		buffer.clear();
		buffer.putLong(0,x);
		return buffer.array();
	}
	
	public static long bytesToLong(byte[] bytes) {
		System.out.println(bytes.length);
		buffer.clear();
		buffer.put(bytes,0,bytes.length);
		buffer.flip();
		return buffer.getLong();
	}
	
	public static String byteToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length);
        String sTemp;
        for (int i = 0; i < bytes.length; i++) {
            sTemp = Integer.toHexString(0xFF & bytes[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }
	
}
