package com.mysite.irms.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mysite.irms.domain.Envelope;
import com.mysite.irms.repository.EnvelopeRepository;
import com.mysite.irms.repository.UserRepository;
import com.mysite.irms.util.LogWriter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnvelopeService {
	private final EnvelopeRepository envelopeRepository;
	private final UserRepository userRepository;
	private static final String BASE_UPLOAD_PATH = "C:/Users/lnsnd/Documents/DWU/IRMS_envelopeDir";
	
	public String createEnvelope(MultipartFile logFile, MultipartFile privateKeyFile, String username) throws IOException {
		Path userDir = Paths.get(BASE_UPLOAD_PATH, username);
		Files.createDirectories(userDir);
		
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		String zipName = "envelope_"+timestamp +".zip";
		Path zipPath = userDir.resolve(zipName);
		
		try {
			byte[] logBytes = logFile.getBytes();
			
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(256);
			SecretKey aesKey = keyGen.generateKey();
			Cipher aesCipher = Cipher.getInstance("AES");
			aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
			byte[] encryptedLog = aesCipher.doFinal(logBytes);
			
			MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
			byte[] hash = sha512.digest(logBytes);
			
			PrivateKey privateKey = loadPrivateKey(privateKeyFile.getBytes());
			
			Signature signature = Signature.getInstance("SHA512withRSA");
			signature.initSign(privateKey);
			signature.update(hash);
			byte[] signedHash = signature.sign();
			
			
			try(ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))){
				//암호화된 log
				zos.putNextEntry(new ZipEntry("encrypted_log.bin"));
				zos.write(encryptedLog);
				zos.closeEntry();
				// 서명 파일
				zos.putNextEntry(new ZipEntry("signature.sig"));
				zos.write(signedHash);
				zos.closeEntry();
				//AES파일
				zos.putNextEntry(new ZipEntry("aes.key"));
				zos.write(Base64.getEncoder().encode(aesKey.getEncoded()));
				zos.closeEntry();
			}
			LogWriter.writeCreateLog(username, logFile.getOriginalFilename(), zipName, true);
			
			
			Envelope envelope = new Envelope();
			envelope.setFileName(zipName);
			envelope.setOriginalOwner(username);
			envelope.setCurrentOwner(username);
			envelope.setCreatedAt(LocalDateTime.now());
			envelopeRepository.save(envelope);
			
			return zipName;
		}catch (GeneralSecurityException e) {
			e.printStackTrace();
			LogWriter.writeCreateLog(username, logFile.getOriginalFilename(), zipName, false);
			throw new IOException("전자봉투 생성 중 보안 오류 발생: " + e.getMessage(), e);
		}
	}
	
	private PrivateKey loadPrivateKey(byte[] keyBytes) throws GeneralSecurityException{
		String keyStr = new String(keyBytes).replaceAll("-----\\w+ PRIVATE KEY-----", "").replaceAll("\\s", "");
		byte[] decoded = Base64.getDecoder().decode(keyStr);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
	}
	
	public String verifyEnvelope(MultipartFile zipFile, 
			MultipartFile publicKeyFile, 
			MultipartFile aesKeyFile, 
			String username) throws Exception{
		Path tempDir = Files.createTempDirectory("verify_");
		Path zipPath = tempDir.resolve(zipFile.getOriginalFilename());
		zipFile.transferTo(zipPath.toFile());
		
		byte[] encryptedLog = null, signature=null, aesKeyBytes = null;
		
		try(ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipPath))){
			ZipEntry entry;
			while((entry = zis.getNextEntry()) != null ) {
				switch(entry.getName()) {
					case "encrypted_log.bin" -> encryptedLog = zis.readAllBytes();
					case "signature.sig" -> signature = zis.readAllBytes();
					case "aes.key" -> aesKeyBytes = Base64.getDecoder().decode(zis.readAllBytes());
				}
				zis.closeEntry();
			}
		}
		
		try {
			SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");
			Cipher aesCipher = Cipher.getInstance("AES");
			aesCipher.init(Cipher.DECRYPT_MODE,aesKey);
			byte[] decryptedLog = aesCipher.doFinal(encryptedLog);
			
			MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
			byte[] hash = sha512.digest(decryptedLog);
			
			PublicKey pubKey = loadPublicKey(publicKeyFile.getBytes());
			Signature sig = Signature.getInstance("SHA512withRSA");
			sig.initVerify(pubKey);
			sig.update(hash);
			
			boolean valid = sig.verify(signature);
			if(!valid) {
				throw new SecurityException("서명 검증 실패");
			}
			
			LogWriter.writeVerifyLog(username, zipFile.getOriginalFilename(), true);
			return new String(decryptedLog, StandardCharsets.UTF_8);
		}catch(Exception e) {
			e.printStackTrace();
			LogWriter.writeVerifyLog(username,zipFile.getOriginalFilename(), false);
			throw new IOException("검증 실패: "+ e.getMessage(), e);
		}
	}
	
	private PublicKey loadPublicKey(byte[] keyBytes) throws GeneralSecurityException{
		String keyStr = new String(keyBytes).replaceAll("-----\\w+ PUBLIC KEY-----", "").replaceAll("\\s", "");
		byte[] decoded = Base64.getDecoder().decode(keyStr);
		X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);
	}
	
	public void transferEnvelope(Long envelopeId, String newOwner) {
		Envelope envelope = envelopeRepository.findById(envelopeId)
				.orElseThrow(() -> new IllegalArgumentException("전자봉투가 존재하지 않습니다."));
		envelope.setCurrentOwner(newOwner);
		envelopeRepository.save(envelope);
	}
	
	public void transferEnvelopeByFilename(String fileName, String newOwner) {
		Envelope envelope = envelopeRepository.findByFileName(fileName)
				.orElseThrow(() -> new IllegalArgumentException("해당 파일명이 존재하지 않습니다."));
		
		if(!userRepository.findByUsername(newOwner).isPresent()) {
			throw new IllegalArgumentException("전달 대상 사용자가 존재하지 않습니다.");
		}		
		
		envelope.setCurrentOwner(newOwner);
		envelopeRepository.save(envelope);
	}
}
