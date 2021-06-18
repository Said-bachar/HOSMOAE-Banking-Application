package com.ensa.hosmoaBank.models;


import java.util.Date;

import javax.validation.constraints.*;

import org.hibernate.validator.constraints.CreditCardNumber;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
	
  //@NotNull(message = "U need to enter your account number")
  @CreditCardNumber
  String acountNumber;
  float amount; //montant

  //String notes;

//  @NotNull(message = "Enter the account num of the des ")
  @CreditCardNumber
  String numeroCompteDest;

//  @NotNull(message = "Enter the key secret of ur account")
  //@Pattern(regexp="[\\d]{8}", message = "Le code secret doit contenir exactement 8 chiffres")
  String keySecret;


}
