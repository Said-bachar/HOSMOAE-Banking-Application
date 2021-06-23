package com.ensa.hosmoaBank.models;

import java.util.Collection;
import java.util.Date;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.CreditCardNumber;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RechargeRequest {
	
	//@NotNull(message = "Veuillez entrer le nº de compte")
    @CreditCardNumber
    String accountNumber;

    float amount;
    String operator;

//    @Pattern(regexp = "^\\+(?:[0-9] ?){6,14}[0-9]$", message = "Le nº de télephone est invalide.")
    String phoneNumber;

//    @NotNull(message = "Veuillez entrer le code secret de compte")
    @Pattern(regexp="[\\d]{8}", message = "Le code secret doit contenir exactement 8 chiffres")
    String keySecret;

}
