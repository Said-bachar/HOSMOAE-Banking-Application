package com.ensa.hosmoaBank.models;

import java.util.*;

import javax.persistence.*;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "multipletransfers")
public class MultipleTransfer extends Transfer{

	private static final long serialVersionUID = 1L;
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private int numBeneficiaries;
	@ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
	@JoinTable(name = "MultipleTransferBeneficiary", joinColumns = @JoinColumn(name = "multiple_transfer_id"), inverseJoinColumns = @JoinColumn(name = "beneficiary_id"))
    private Collection<Beneficiary> beneficiaries;

}
