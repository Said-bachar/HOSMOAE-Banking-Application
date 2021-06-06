package com.ensa.hosmoaBank.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="Le token est invalide")
public class InvalidVerificationTokenException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;}
