package com.ensa.hosmoaBank.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MultipleTransferBeneficiaryRequest {
	float amount;
	Long id_beneficiary;
}