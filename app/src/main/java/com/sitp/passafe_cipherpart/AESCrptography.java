package com.sitp.passafe_cipherpart;

import java.security.SecureRandom;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;

public class AESCrptography {

	 /*public static void main(String[] args) {
		// TODO Auto-generated method stub

		String content="hello";
		String key="aaaaaaaa";
		String iv="abcdefghijklmnop";

		System.out.println("加密前："+byteToHexString(content.getBytes()));
		byte[ ] encrypted=AES_CBC_Encrypt(content.getBytes(), key.getBytes(), iv.getBytes());
		System.out.println("加密后："+byteToHexString(encrypted));
		byte[ ] decrypted=AES_CBC_Decrypt(encrypted, key.getBytes(), iv.getBytes());
		System.out.println("解密后："+byteToHexString(decrypted));
	}*/


	public static byte[] AES_CBC_Encrypt(byte[] content, byte[] keyBytes, byte[] iv){

		try{
			KeyGenerator keyGenerator=KeyGenerator.getInstance("AES");
			keyGenerator.init(128, new SecureRandom(keyBytes));
			SecretKey key=keyGenerator.generateKey();
			Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
			//Cipher cipher=Cipher.getInstance("AES/CBC/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
			byte[] result=cipher.doFinal(content);
			return result;
		}catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("exception:"+e.toString());
		}
		return null;
	}

	public static byte[] AES_CBC_Decrypt(byte[] content, byte[] keyBytes, byte[] iv){

		try{
			KeyGenerator keyGenerator=KeyGenerator.getInstance("AES");
			keyGenerator.init(128, new SecureRandom(keyBytes));//key长可设为128，192，256位，这里只能设为128
			SecretKey key=keyGenerator.generateKey();
			Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
			//Cipher cipher=Cipher.getInstance("AES/CBC/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
			byte[] result=cipher.doFinal(content);
			return result;
		}catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("exception:"+e.toString());
		}
		return null;
	}


}