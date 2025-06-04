package com.mysite.irms.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mysite.irms.domain.Envelope;
import com.mysite.irms.repository.EnvelopeRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins="http://localhost:3000", allowCredentials = "true")
public class MypageController {
	private static final String BASE_UPLOAD_PATH = "C:/Users/lnsnd/Documents/DWU/IRMS_envelopeDir";
	
	private final Path uploadRoot = Paths.get(BASE_UPLOAD_PATH);
	private final EnvelopeRepository envelopeRepository;
	
	@GetMapping("/api/mypage")
	@ResponseBody
	public ResponseEntity<?> myPageFiles(HttpSession session) throws IOException{
		String username = (String) session.getAttribute("username");
		if(username==null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		// 전자봉투 조회
		List<Envelope> received = envelopeRepository.findByCurrentOwner(username);
		List<Envelope> sent = envelopeRepository.findByOriginalOwner(username);
		
		// 파일 조회
//		Path userDir = uploadRoot.resolve(username);
//		List<String> actualFiles = Files.exists(userDir)
//				? Files.list(userDir)
//						.filter(Files::isRegularFile)
//						.map(p-> p.getFileName().toString())
//						.sorted()
//						.collect(Collectors.toList())
//						:List.of();
		
		return ResponseEntity.ok(Map.of(
				"username", username, 
				"received", received.stream().map(Envelope::getFileName).toList(), 
				"sent", sent.stream().filter(e -> !e.getCurrentOwner().equals(username))
						.map(Envelope::getFileName).toList()));
	}
	
	@GetMapping("/download/{fileName:.+}")
	public ResponseEntity<Resource> download(@PathVariable String fileName, HttpSession session) throws IOException{
		String username = (String) session.getAttribute("username");
		if(username==null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		Optional<Envelope> envelopeOpt = envelopeRepository.findByFileName(fileName);
		if(envelopeOpt.isEmpty() || !envelopeOpt.get().getCurrentOwner().equals(username)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		Path file = uploadRoot.resolve(envelopeOpt.get().getCurrentOwner()).resolve(fileName);
		if(!Files.exists(file)) {
			return ResponseEntity.notFound().build();
		}
		
		Resource res = new UrlResource(file.toUri());
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
				.body(res);
	}
}
