package com.ensa.hosmoaBank.models;

import lombok.Data;

@Data
public class ChangeClientDataRequest {
	
	String agentPassword;
	User user;

}
