package com.mysite.irms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.irms.entity.Envelope;

public interface EnvelopeRepository extends JpaRepository<Envelope, Long>{
	List<Envelope> findByUploaderUsername(String username);
	List<Envelope> findByReceiverUsername(String username);
	List<Envelope> findByUploaderUsernameAndReceiverUsernameIsNotNull(String username); // 보낸 봉투
	List<Envelope> findByUploaderUsernameAndReceiverUsernameIsNull(String username); // 생성한 봉투
}
