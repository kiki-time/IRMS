package com.mysite.irms.controller;

import com.mysite.irms.dto.EnvelopeTransferRequest;
import com.mysite.irms.entity.Envelope;
import com.mysite.irms.repository.EnvelopeRepository;
import com.mysite.irms.security.JwtTokenProvider;
import com.mysite.irms.service.EnvelopeService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/envelopes")
@RequiredArgsConstructor
public class EnvelopeController {
	private final EnvelopeService envelopeService;
	private final EnvelopeRepository envelopeRepository;
	private final JwtTokenProvider jwtTokenProvider;

	@PostMapping("/upload")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, Authentication authentication) {
		try {
			String username = authentication.getName();
			System.out.println("인증된 사용자: "+username);
			
			envelopeService.saveEncryptedEnvelope(file, username);
			return ResponseEntity.ok("업로드 및 암호화 완료");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("요청 처리 중 오류 발생.");
		}
	}

	@GetMapping("/my")
	public ResponseEntity<List<Envelope>> getMyEnvelopes(HttpServletRequest request) {
		String username = extractUsernameFromRequest(request);
		List<Envelope> envelopes = envelopeRepository.findByUploaderUsername(username);
		return ResponseEntity.ok(envelopes);
	}
	
	private String extractUsernameFromRequest(HttpServletRequest request) {
		String token = jwtTokenProvider.resolveToken(request);
		return jwtTokenProvider.getUsername(token);
	}

	@GetMapping("/download/{id}")
	public ResponseEntity<?> downloadFile(@PathVariable("id") Long id, HttpServletRequest request) {
		try {
			String username = extractUsernameFromRequest(request);
			return envelopeService.downloadFileService(id, username);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("다운로드 실패: " + e.getMessage());
		}
	}

	@GetMapping("/verify/{id}")
	public ResponseEntity<?> verifyEnvelope(@PathVariable("id") Long id, HttpServletRequest request) {
		try {
			String username = extractUsernameFromRequest(request);
			String decrypted = envelopeService.verifyAndDecryptEnvelope(id, username);
			return ResponseEntity.ok().body(decrypted);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("검증 실패: " + e.getMessage());
		}
	}

	@PostMapping("/transfer")
	public ResponseEntity<?> transferEnvelope(@RequestBody EnvelopeTransferRequest request,
			HttpServletRequest httpReq) {
		try {
			String sender = extractUsernameFromRequest(httpReq);
			envelopeService.transferEnvelope(request, sender);
			return ResponseEntity.ok("전달 완료: " + request.getReceiverUsername());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("전달 실패: " + e.getMessage());
		}
	}

	@GetMapping("/created")
	public ResponseEntity<List<Envelope>> getCreatedEnvelopes(HttpServletRequest request) {
		String username = extractUsernameFromRequest(request);
		List<Envelope> envelopes = envelopeRepository.findByUploaderUsernameAndReceiverUsernameIsNull(username);
		return ResponseEntity.ok(envelopes);
	}
	
	@GetMapping("/received")
	public ResponseEntity<List<Envelope>> getReceivedEnvelopes(HttpServletRequest request) {
		String username = extractUsernameFromRequest(request);
		List<Envelope> envelopes = envelopeRepository.findByReceiverUsername(username);
		return ResponseEntity.ok(envelopes);
	}

	@GetMapping("/sent")
	public ResponseEntity<List<Envelope>> getSentEnvelopes(HttpServletRequest request) {
		String username = extractUsernameFromRequest(request);
		List<Envelope> envelopes = envelopeRepository.findByUploaderUsernameAndReceiverUsernameIsNotNull(username);
		return ResponseEntity.ok(envelopes);
	}

	@GetMapping("/search-user")
	public ResponseEntity<?> searchUser(@RequestParam("keyword") String keyword) {
		try {
			if (!keyword.matches("^[a-zA-Z0-9_]{2,20}$")) {
			    throw new IllegalArgumentException("검색어가 유효하지 않습니다.");
			}
			List<String> results = envelopeService.searchUserByKeyword(keyword);
			return ResponseEntity.ok(results);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("검색 실패: " + e.getMessage());
		}
	}

}
