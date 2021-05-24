package com.ensa.hosmoaBank.models;

import java.math.*;
import java.util.*;


import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import lombok.*;

@NoArgsConstructor 
@AllArgsConstructor
@Data
@Entity
@Table(name = "accounts")
public class Account {
	
	@Id
	@GeneratedValue(generator = "cn-generator")
	@GenericGenerator(name = "cn-generator", strategy = "//our utilities") // we need to generate this id using somthing ..
	@Column(name = "number_account")
	private String numberAccount;
	@Column(name = "entitled")
	private String entitled;
	@Column(name = "account_balance")
	private BigDecimal accountBalance;
	@Column(name = "solde")
	private double solde;
	@Column(name = "update_date")
	private Date updateDate;
	@Column(name = "last_operation")
	private Date lastOperation;
	
	// Relation : Every Account ---> 1 Client
	@JoinColumn(name = "id_client")
	private Client client;
	
	private Collection<Transfer> transfers;
	
	private boolean deleted;

}
