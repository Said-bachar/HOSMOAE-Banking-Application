package com.ensa.hosmoaBank.models;

import java.io.Serializable;
import java.math.*;
import java.util.*;
import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Transfer implements Serializable {

	
	  private static final long serialVersionUID = 1L;
	  
	  @Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
	  @Column(name="id_transfer", unique = true) 
	  private Long id;
	  
	  @Column(name = "amount") 
	  private BigDecimal amount;
	  
	  @Column(name = "creation_date")
	  @CreationTimestamp
	  private Date creationDate;
	  
	  @Column(name = "execution_date")
	  @CreationTimestamp
	  private Date executionDate;
	  
	  @Column(name = "motif") 
	  private String motif;
	  
	  @ManyToOne
	  @JoinColumn(name = "")
	  private Beneficiary beneficiary;
	  
	  private Client client;
	  
	 

}
