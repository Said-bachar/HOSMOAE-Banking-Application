package com.ensa.hosmoaBank.models;

import java.math.*;
import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "multiple_transfer_beneficiaries")
@Data
@NoArgsConstructor @AllArgsConstructor
public class MultipleTransferBeneficiary {
	
	private BigDecimal amount;
	private Beneficiary beneficiary;
	private MultipleTransfer multipleTransfer;

}
