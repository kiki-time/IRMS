package com.mysite.irms.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mysite.irms.dto.RegisterRequest;
import com.mysite.irms.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins="http://localhost:3000", allowCredentials = "true")
public class AuthController {
	private final UserService userService;
	
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestParam("username") String username, 
									@RequestParam("password") String password,
									@RequestParam("confirmPassword") String confirmPassword,
									@RequestParam("fullName") String fullName,
									@RequestParam("email") String email){
		try {
			RegisterRequest req = new RegisterRequest();
			req.setUsername(username);
			req.setPassword(password);
			req.setConfirmPassword(confirmPassword);
			req.setFullName(fullName);
			req.setEmail(email);
			
			userService.register(req);
			return ResponseEntity.ok(Map.of("message", "회원가입 성공"));
		}catch(IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session){
		try {
			userService.login(username, password);
			session.setAttribute("username", username);
			return ResponseEntity.ok(Map.of("message", "로그인 성공", "username", username));
		}catch(IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpSession session){
		session.invalidate();
		return ResponseEntity.ok(Map.of("message", "로그아웃 성공"));
	}
	
	@GetMapping("/me")
	public ResponseEntity<?> getCurrentUser(HttpSession session){
		String username = (String) session.getAttribute("username");
		if(username==null) {
			return ResponseEntity.status(401).body(Map.of("message", "로그인 필요"));
		}
		return ResponseEntity.ok(Map.of("username", username));
	}
}
