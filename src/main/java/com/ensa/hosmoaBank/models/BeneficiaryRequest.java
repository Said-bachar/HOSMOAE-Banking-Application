package com.ensa.hosmoaBank.models;

import org.hibernate.validator.constraints.CreditCardNumber;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BeneficiaryRequest {
	@CreditCardNumber
	String accountNumber;
	String firstName;
	String lastName;
}
