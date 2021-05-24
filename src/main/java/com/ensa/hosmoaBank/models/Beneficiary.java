package com.ensa.hosmoaBank.models;

import java.util.*;

import javax.persistence.*;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "beneficiaries")	
public class Beneficiary {
     
	  @Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
	  @Column(name = "id")
	  private Long id;
	  @Column(name = "first_name")
	  private String firstName;
	  @Column(name = "last_name")
	  private String lastName;
	  
	  // /!\ To discuss
	  private Collection<MultipleTransferBeneficiary> multipleTransferBeneficiaries;
	  private Collection<Client> clients;
	  
}
