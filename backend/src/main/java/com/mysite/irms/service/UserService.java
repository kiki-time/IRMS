package com.mysite.irms.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mysite.irms.domain.User;
import com.mysite.irms.repository.UserRepository;
import com.mysite.irms.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder encoder;
	
	public void register(RegisterRequest req) {
		if(userRepository.findByUsername(req.getUsername()).isPresent()) {
			throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
		}
		if (!req.getPassword().equals(req.getConfirmPassword())) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}
		
		if(!req.getEmail().matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
			throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
		}
		
		String password = req.getPassword();
		if (password.length() < 8 
				|| !password.matches(".*[A-Z].*") 
				|| !password.matches(".*[a-z].*")
				|| !password.matches(".*\\d.*") 
				|| !password.matches(".*[!@#$%^&*()].*")
				|| password.contains(req.getUsername())) {
			throw new IllegalArgumentException("비밀번호는 8자 이상, 대/소문자, 숫자, 특수문자를 포함해야 하며, 아이디를 포함할 수 없습니다.");
		}

		User user = User.builder()
				.username(req.getUsername())
				.password(encoder.encode(req.getPassword()))
				.fullName(req.getFullName())
				.email(req.getEmail())
				.build();
		userRepository.save(user);
	}
	
	public User login(String username, String password) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
		
		if(!encoder.matches(password, user.getPassword())) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}
		return user;
	}
}