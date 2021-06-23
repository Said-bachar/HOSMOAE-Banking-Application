package com.ensa.hosmoaBank.models;

import lombok.Data;

@Data
public class CodeValidationRequest {
	
	private Integer code;
    private String email;

}
