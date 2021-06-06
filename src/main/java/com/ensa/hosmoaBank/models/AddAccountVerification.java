package com.ensa.hosmoaBank.models;

import lombok.Data;

@Data
public class AddAccountVerification {
	
    private String agentPassword;
    private String entitled;
    private double balance;

}
