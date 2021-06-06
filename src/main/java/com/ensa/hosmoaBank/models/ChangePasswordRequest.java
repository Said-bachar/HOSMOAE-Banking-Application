package com.ensa.hosmoaBank.models;

import lombok.Data;

@Data
class ChangePasswordRequest {
	private String oldPassword;
	private String newPassword;
	private String confirmedPassword;

}
