package com.ensa.hosmoaBank.models;

import java.util.Collection;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.CreditCardNumber;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MultipleTransferRequest {
	
	@CreditCardNumber
	  String acountNumber;
	  float amount;
	  
	  @Pattern(regexp="[\\d]{8}", message = "Le code secret doit contenir exactement 8 chiffres")
	  String secretKey;
	  
	  Collection<MultipleTransferBeneficiary> multipletransfersbeneficiaries;

}
