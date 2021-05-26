package com.ensa.hosmoaBank.models;

import java.math.*;
import java.util.*;


import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import lombok.*;

@NoArgsConstructor 
@AllArgsConstructor
@Data

public class Account {
	
	  @Id
	  @GeneratedValue(generator = "cn-generator")
	  @GenericGenerator(name = "cn-generator", strategy = "//our utilities") 
	  @Column(name = "number_account") 
	  private String numberAccount;
	  
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
	  
	  private Collection<Transfer> transfers;
	  
	  private boolean deleted;
	 

}
