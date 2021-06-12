package com.ensa.hosmoaBank.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Entity
@NoArgsConstructor
public class MultipleTransferBeneficiary {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private float amount;
	
	@ManyToOne
    @JoinColumn(name = "id_beneficiary")
	private Beneficiary beneficiary;
	@ManyToOne
    @JoinColumn(name = "id_multipletransfer")
	private MultipleTransfer multipletransfer;

}
