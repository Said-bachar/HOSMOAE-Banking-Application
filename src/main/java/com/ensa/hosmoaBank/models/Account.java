package com.ensa.hosmoaBank.models;

import java.math.*;
import java.util.*;


import javax.persistence.*;

import lombok.*;

@NoArgsConstructor 
@AllArgsConstructor
@Data
@Entity
public class Account {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	  
	  @Column(name = "entitled") 
	  private String entitled;
	  
	  @Column(name = "account_balance") 
	  private BigDecimal accountBalance;
	 
	  
	  @Column(name = "update_date") 
	  private Date updateDate;
	  
	  @Column(name = "creation_date") 
	  private Date creationDate;
	  
	  @Column(name = "last_operation") 
	  private Date lastOperation;
	  
	  
	  
	  // Relation : Every Account ---> 1 Client
	  
	  @ManyToOne 
	  @JoinColumn(name = "id_client")
	  private Client client;
	  
	  @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
	  private Collection<Transfer> transfers;
	  
	  private boolean deleted;
	 

}
