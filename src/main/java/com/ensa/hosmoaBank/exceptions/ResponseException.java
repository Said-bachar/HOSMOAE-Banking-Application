package com.ensa.hosmoaBank.exceptions;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.Date;

//@Data
@Getter
@Setter
public class ResponseException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HttpStatus status;
    private String[] errors;
    private Date timestamp;

    public ResponseException (HttpStatus status) {
        timestamp = new Date();
        this.status = status;
    }

    public ResponseException (HttpStatus status, String... errors) {
        timestamp = new Date();
        this.status = status;
        this.errors = errors;
    }

}