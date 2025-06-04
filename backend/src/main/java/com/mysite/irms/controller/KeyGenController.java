package com.mysite.irms.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins="http://localhost:3000", allowCredentials = "true")
public class KeyGenController {
	@GetMapping("/keygen")
	@ResponseBody
	public ResponseEntity<Resource> generateKey(HttpSession session) throws Exception{
		try {
			String username = (String) session.getAttribute("username");
			if(username==null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
			
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(2048);
			KeyPair pair = generator.generateKeyPair();
			
			Path userDir = Paths.get("uploads", username);
			Files.createDirectories(userDir);
			
			Path privatePath = userDir.resolve("private.key");
			Path publicPath = userDir.resolve("public.key");
			
			Files.write(privatePath, Base64.getEncoder().encode(pair.getPrivate().getEncoded()));
			Files.write(publicPath, Base64.getEncoder().encode(pair.getPublic().getEncoded()));
			Path zipPath = userDir.resolve("rsa_keys.zip");
			try(ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))){
				zos.putNextEntry(new ZipEntry("private.key"));
				zos.write(Files.readAllBytes(privatePath));
				zos.closeEntry();
				
				zos.putNextEntry(new ZipEntry("public.key"));
				zos.write(Files.readAllBytes(publicPath));
				zos.closeEntry();
			}
			
			Resource resource = new UrlResource(zipPath.toUri());
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"rsa_keys.zip\"")
					.body(resource);
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
