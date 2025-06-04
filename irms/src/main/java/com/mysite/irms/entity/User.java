package com.mysite.irms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="users")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String username;
	
	@Column(nullable = false)
	private String password; // bcrypt 암호화
	
	@Column(nullable = false, unique = true)
	private String email;
	
	@Column(nullable = false)
	private String role = "USER"; // default 권한
	
	@Column(nullable = false)
	private String fullName;
}
