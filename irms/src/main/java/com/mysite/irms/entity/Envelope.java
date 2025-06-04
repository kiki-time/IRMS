package com.mysite.irms.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Envelope {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String originalFileName;
	private String storedFileName;
	private String uploaderUsername; // 처음 업로드한 사람
	private String receiverUsername; // 전달받은 사람
	
	private LocalDateTime uploadTime;
	
	@Lob
	private String signature; // 전자서명
	
	@Column(name="aes_key_encrypted", length=1024)
	private String aesKeyEncrypted; //rsa로 암호화된 aes키
}
