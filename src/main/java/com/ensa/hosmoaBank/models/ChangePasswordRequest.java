package com.ensa.hosmoaBank.models;

import lombok.Data;

@Data
public class ChangePasswordRequest {
	private String oldPassword;
	private String newPassword;
	private String confirmedPassword;

}
