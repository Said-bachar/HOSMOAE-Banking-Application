package com.ensa.hosmoaBank.models;

import java.util.*;

import javax.persistence.*;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Client extends User{
	
	
	  @Column(name = "firts_name") 
	  private String firstName;
	  
	  @Column(name = "last_name") 
	  private String lastName;
	  
	  @ManyToOne @JoinColumn(name = "id_agent")
	  private Agent agent;
	  
	  @OneToMany(mappedBy = "client", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
	  private Collection<Beneficiary> beneficiaries;
	  
	  @OneToMany(mappedBy = "client", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
	  private Collection<Transfer> transfers;
	  
	  @OneToMany(mappedBy = "client", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
	  private Collection<Account> accounts;
	 

}
