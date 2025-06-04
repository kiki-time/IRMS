package com.mysite.irms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mysite.irms.security.JwtTokenProvider;
import com.mysite.irms.service.KeyService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/keys")
@RequiredArgsConstructor
public class KeyController {
	private final KeyService keyService;
	private final JwtTokenProvider jwtTokenProvider;

	@PostMapping("/generate")
	public ResponseEntity<?> generateKeys(HttpServletRequest request) {
		String token = jwtTokenProvider.resolveToken(request);
		if(token == null) {
			throw new RuntimeException("인증 토큰이 필요합니다.");
		}
		String username = jwtTokenProvider.getUsernameFromToken(token);

		try {
			keyService.generateKeyPairForUser(username);
			return ResponseEntity.ok("키쌍이 성공적으로 생성되었습니다.");
		} catch (IllegalStateException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("키 생성 중 오류 발생: " + e.getMessage());
		}
	}
}
