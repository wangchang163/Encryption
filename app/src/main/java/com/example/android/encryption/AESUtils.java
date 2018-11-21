package com.example.android.encryption;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES 加密工具类，适配Android7.0，相对老的加密工具，提升了安全性
 */
public class AESUtils {
	/**
	 * iv大小(位)
	 **/
	private static final int IV_SIZE = 16;
	/**
	 * 密钥大小(位)
	 **/
	private static final int KEY_SIZE = 32;
	/**
	 * iv文件名
	 **/
	private static final String IV_FILE_NAME = "AES_IV";
	/**
	 * salt文件名
	 **/
	private static final String SALT_FILE_NAME = "AES_SALT";
	// iv文件和salt文件位于外置储存卡的Android/data/com.example.name(你的App的包名)/files/Download目录下，文件名如上面所示

	/**
	 * 生成一个安全的密钥
	 *
	 * @param password       生成密钥的seed
	 * @param keySizeInBytes 密钥大小(位)
	 * @return 密钥
	 */
	private static SecretKey deriveKeySecurely(Context context, String password, int keySizeInBytes) {
		// Use this to derive the key from the password:
		KeySpec keySpec = new PBEKeySpec(password.toCharArray(), retrieveSalt(context),
				100 /* iterationCount */, keySizeInBytes * 8 /* key size in bits */);
		try {
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
			return new SecretKeySpec(keyBytes, "AES");
		} catch (Exception e) {
			throw new RuntimeException("Deal with exceptions properly!", e);
		}
	}

	private static byte[] retrieveIv(Context context) {
		byte[] iv = new byte[IV_SIZE];
		// Ideally your data should have been encrypted with a random iv. This creates a random iv
		// if not present, in order to encrypt our mock data.
		readFromFileOrCreateRandom(context, IV_FILE_NAME, iv);
		return iv;
	}

	private static byte[] retrieveSalt(Context context) {
		// Salt must be at least the same size as the key.
		byte[] salt = new byte[KEY_SIZE];
		// Create a random salt if encrypting for the first time, and save it for future use.
		readFromFileOrCreateRandom(context, SALT_FILE_NAME, salt);
		return salt;
	}

	private static void readFromFileOrCreateRandom(Context context, String fileName, byte[] bytes) {
		if (fileExists(context, fileName)) {
			readBytesFromFile(context, fileName, bytes);
			return;
		}
		SecureRandom sr = new SecureRandom();
		sr.nextBytes(bytes);
		writeToFile(context, fileName, bytes);
	}

	private static boolean fileExists(Context context, String fileName) {
		File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
		return file.exists();
	}

	@SuppressWarnings("unused")
	private static void removeFile(Context context, String fileName) {
		File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
		//noinspection ResultOfMethodCallIgnored
		file.delete();
	}

	private static void writeToFile(Context context, String fileName, byte[] bytes) {
		File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);

		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(bytes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Couldn't write to " + fileName, e);
		}
	}

	private static void readBytesFromFile(Context context, String fileName, byte[] bytes) {
		File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);

		try {
			FileInputStream fis = new FileInputStream(file);
			int numBytes = 0;
			while (numBytes < bytes.length) {
				int n = fis.read(bytes, numBytes, bytes.length - numBytes);
				if (n <= 0) {
					throw new RuntimeException("Couldn't read from " + fileName);
				}
				numBytes += n;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Couldn't read from " + fileName, e);
		}
	}

	private static byte[] encryptOrDecrypt(
            byte[] data, SecretKey key, byte[] iv, boolean isEncrypt) {
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING");
			cipher.init(isEncrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, key,
					new IvParameterSpec(iv));
			return cipher.doFinal(data);
		} catch (GeneralSecurityException e) {
			throw new RuntimeException("This is unconceivable!", e);
		}
	}

	private static byte[] encryptData(byte[] data, byte[] iv, SecretKey key) {
		return encryptOrDecrypt(data, key, iv, true);
	}

	private static byte[] decryptData(byte[] data, byte[] iv, SecretKey key) {
		return encryptOrDecrypt(data, key, iv, false);
	}

	// 本工具类唯一提供的两个公共方法，用来加密和加密数据

	/**
	 * 加密数据
	 *
	 * @param data     待加密的数据
	 * @param password 密钥
	 * @return 已加密的数据
	 */
	public static byte[] encrypt(Context context, byte[] data, String password) {
		return encryptData(data, retrieveIv(context), deriveKeySecurely(context, password, KEY_SIZE));
	}

	/**
	 * 解密数据
	 *
	 * @param data     待解密的数据
	 * @param password 密钥
	 * @return 已解密的数据
	 */
	public static byte[] decrypt(Context context, byte[] data, String password) {
		return decryptData(data, retrieveIv(context), deriveKeySecurely(context, password, KEY_SIZE));
	}
}