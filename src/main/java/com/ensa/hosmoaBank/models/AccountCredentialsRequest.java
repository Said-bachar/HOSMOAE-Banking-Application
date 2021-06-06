package com.ensa.hosmoaBank.models;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.CreditCardNumber;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountCredentialsRequest {
	
//  @NotNull(message = "Enter number of account")
  @CreditCardNumber
  String accountNumber;

//  @NotNull(message = "Enter code key secret of account")
  @Pattern(regexp="[\\d]{8}", message = "Code must contains exactly 8 chars")
  String keySecret;

}
