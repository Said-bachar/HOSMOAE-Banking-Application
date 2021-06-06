package com.ensa.hosmoaBank.models;

import java.io.Serializable;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.Positive;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.ensa.hosmoaBank.enumerations.TransferStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "transfers")
@SQLDelete(sql = "UPDATE transfers SET deleted=true WHERE id_virement=?")
@Where(clause = "deleted = false")
//@Inheritance (strategy = InheritanceType.JOINED)
public class Transfer implements Serializable {

	
	    private static final long serialVersionUID = 1L;
	  
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "id_virement", unique = true)
	    private Long id ;

	    private String notes;

	    private boolean deleted;

//	    @NotNull
	    @JsonIgnore
	    private int codeVerification; // for verify transaction before send it

//	    @NotNull
	    @Positive
	    private double montant;

	    @CreationTimestamp
	    private Date dateDeVirement;

	    @Enumerated(EnumType.STRING)
	    private TransferStatus status;

	    // triggered at begining of transaction : generate default values for Transfer
	    @PrePersist
	    void beforeInsert() {
	        System.out.println("SETTING DEFAULT VALUES FOR VIREMENT");
	        status = TransferStatus.UNCONFIRMED;
	        codeVerification = new Random().nextInt(90000000) + 10000000;
	    }


	    @ManyToOne
	    @JoinColumn(name = "uuid_compte", nullable = false) //Relation : * Transfers <-------- 1 Account
	    @JsonIgnoreProperties({"transfers", "requests", "client"})
	    private Account account;

	    @OneToOne
	    @JoinColumn(name = "compte_destinataire") // To discuss
	    @JsonIgnoreProperties({"virements", "recharges", "client", "dateDeCreation", "dernierOperation", "intitule", "solde", "dateUpdate", "statut" })
	    private Account destAccount;  //Relation Transfer 1,1 ---->1,1 Account : Send 1 Transfer To  one Account
	    
	  
	 

}
