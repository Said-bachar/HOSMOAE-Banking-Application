package com.ensa.hosmoaBank.models;

import javax.persistence.*;

import lombok.*;

@NoArgsConstructor 
@AllArgsConstructor
@Data
@Entity
@Table(name = "beneficiaries")
public class Beneficiary{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "first_name")
    private String fistName;
	@Column(name = "last_name")
    private String lastName;
    @OneToOne( fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    private Account account;
	  
}
