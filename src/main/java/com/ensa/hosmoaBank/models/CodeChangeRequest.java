package com.ensa.hosmoaBank.models;

//import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.CreditCardNumber;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeChangeRequest {
   
	//@NotNull(message = "Enter num of account")
	@CreditCardNumber
	String accountNumber;
	
    //@NotNull(message = "your key secret")
	String keySecret;
	
    //     @NotNull
    //     @Digits(integer=8, fraction=0, message = "8 chars")
    @Pattern(regexp="[\\d]{8}", message = "key must contains exactly 8 chars")
	String newKeySecret;
	
	String newKeySecretConf;
}
