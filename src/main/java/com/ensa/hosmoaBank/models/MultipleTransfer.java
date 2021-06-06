package com.ensa.hosmoaBank.models;



import javax.persistence.*;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "multipletransfers")
public class MultipleTransfer {

    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private int numBeneficiaries;
	

}
