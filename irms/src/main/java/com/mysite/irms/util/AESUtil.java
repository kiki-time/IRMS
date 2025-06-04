package com.mysite.irms.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {
	
	// AES대칭키 생성
	public static SecretKey generateKey() throws Exception {
		KeyGenerator generator = KeyGenerator.getInstance("AES");
		generator.init(256); // 128/192/256 bits
		return generator.generateKey();
	}

	// AES 대칭키로 암호화해 base64 문자열로 반환
	public static String encrypt(String data, SecretKey key) throws Exception {
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encrypted = cipher.doFinal(data.getBytes());
		return Base64.getEncoder().encodeToString(encrypted);
	}

	// base64로 인코딩된 AES 암호문을 복호화해서 평문으로 반환
	public static String decrypt(String base64Encrypted, SecretKey key) throws Exception {
		byte[] encryptedBytes = Base64.getDecoder().decode(base64Encrypted);
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decrypted = cipher.doFinal(encryptedBytes);
		return new String(decrypted);
	}

	// AES secretkey base64문자열로 반환
	public static String encodeKeyToBase64(SecretKey key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}

	// base64 문자열에서 aes secretkey 객체 복원.
	public static SecretKey decodeKeyFromBase64(String base64Key) {
		byte[] decodedKey = Base64.getDecoder().decode(base64Key);
		return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
	}
}
