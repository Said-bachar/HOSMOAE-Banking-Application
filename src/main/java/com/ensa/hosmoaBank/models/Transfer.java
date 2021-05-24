package com.ensa.hosmoaBank.models;

import java.io.Serializable;
import java.math.*;
import java.util.*;
import javax.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transfers")
public class Transfer implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_transfer", unique = true)
	private Long id;
	
	@Column(name = "amount")
	private BigDecimal amount;
	@Column(name = "creation_date")
	private Date creationDate;
	@Column(name = "execution_date")
	private Date executionDate;
	
	//Relation : * Transfers ----> 1 Account
	@ManyToOne
	@JoinColumn(name = "id_client")
	private Account acount;
	@Column(name = "motif")
	private String motif;
	

}
