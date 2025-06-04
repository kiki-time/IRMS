package com.mysite.irms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mysite.irms.domain.Envelope;

public interface EnvelopeRepository extends JpaRepository<Envelope, Long>{
	List<Envelope> findByCurrentOwner(String currentOwner);
	List<Envelope> findByOriginalOwner(String originalOwner);
	
	Optional<Envelope> findByFileName(String fileName);
}


