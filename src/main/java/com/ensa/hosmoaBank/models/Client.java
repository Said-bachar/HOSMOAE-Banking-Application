package com.ensa.hosmoaBank.models;

import java.util.*;

import javax.persistence.*;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "clients")
public class Client {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_client")
	private Long id;
	@Column(name = "firts_name")
	private String firstName;
	@Column(name = "last_name")
	private String lastName;
	@Column(name = "agent")
	private Agent agent;
	
	private Collection<Beneficiary> beneficiaries;
	private Collection<Transfer> transfers;
	private Collection<Account> accounts;

}
