package com.mysite.irms.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter @Setter
public class EnvelopeTransferRequest {
	private Long envelopeId;
	private String receiverUsername;
}
