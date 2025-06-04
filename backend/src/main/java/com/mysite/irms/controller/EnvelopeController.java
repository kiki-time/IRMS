package com.mysite.irms.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mysite.irms.service.EnvelopeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/envelope")
@CrossOrigin(origins="http://localhost:3000", allowCredentials = "true")
public class EnvelopeController {
	private final EnvelopeService envelopeService;
	
	@PostMapping("/upload")
	public ResponseEntity<?> handleUpload(@RequestParam("logFile") MultipartFile logFile,
									@RequestParam("privateKey") MultipartFile privateKey,
									HttpSession session,
									HttpServletRequest request){
		String username = (String) session.getAttribute("username");
		if(username==null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
		}
		
		if(!logFile.getOriginalFilename().endsWith(".log")|| 
				!privateKey.getOriginalFilename().endsWith(".key")) {
			return ResponseEntity.badRequest().body(Map.of("message", "파일 형식이 올바르지 않습니다."));
		}
		
		try {
			String result = envelopeService.createEnvelope(logFile, privateKey, username);
			return ResponseEntity.ok(Map.of("message", "전자봉투 생성 완료: "+result));
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
					Map.of("message", "생성 실패: "+ e.getMessage()));
		}
	}
	
	@PostMapping("/transfer")
	public ResponseEntity<?> transferEnvelope(@RequestBody Map<String, String> payload,
											HttpSession session){
		String username = (String) session.getAttribute("username");
		if(username==null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
		}
		String fileName = payload.get("fileName");
		String targetUsername = payload.get("targetUsername");
		
		if (fileName == null || targetUsername ==null) {
			return ResponseEntity.badRequest()
					.body(Map.of("message", "파라미터 누락"));
		}
		try {
			envelopeService.transferEnvelopeByFilename(fileName, targetUsername);
			return ResponseEntity.ok(Map.of("message", "전자봉투가 성공적으로 전달되었습니다."));
		} catch (Exception e){
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
	}
}
