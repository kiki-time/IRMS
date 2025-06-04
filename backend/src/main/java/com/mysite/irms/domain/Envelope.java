package com.mysite.irms.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@Table(name="envelopes")
public class Envelope {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String fileName;
	
	// 최초 생성자
	@Column(nullable = false)
	private String originalOwner;
	
	// 현재 소유자 : 실제 열람/검증한 사람
	@Column(nullable = false)
	private String currentOwner;
	
	@Column(nullable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;
}
