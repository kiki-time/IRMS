package com.mysite.irms.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mysite.irms.entity.KeyPairEntity;
import com.mysite.irms.entity.User;

public interface KeyRepository extends JpaRepository<KeyPairEntity, Long>{
	Optional<KeyPairEntity> findByUser(User user);
}
