package com.ensa.hosmoaBank.models;

import java.util.*;

import javax.persistence.*;

import lombok.*;

@Getter @Setter
@Entity
@Table(name = "multiple_transfers")
@AllArgsConstructor
@NoArgsConstructor
public class MultipleTransfer extends Transfer{

	private static final long serialVersionUID = 1L;
    
	private int numBeneficiaries;
	
	private Collection<MultipleTransferBeneficiary> multipleTransferBeneficiaries;

}
