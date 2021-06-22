package com.ensa.hosmoaBank.models;



import java.util.Collection;
import java.util.Date;
import java.util.Random;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import com.ensa.hosmoaBank.enumerations.MultipleTransferStatus;
import com.ensa.hosmoaBank.enumerations.TransferStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
public class MultipleTransfer{

    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private int numBeneficiaries;
	
	 @CreationTimestamp
	    private Date dateDeVirement;

	    @Enumerated(EnumType.STRING)
	    private MultipleTransferStatus status;
	    @JsonIgnore
	    private int codeVerification; 

	    // triggered at begining of transaction : generate default values for Transfer
	    @PrePersist
	    void beforeInsert() {
	        System.out.println("SETTING DEFAULT VALUES FOR VIREMENT");
	        status = MultipleTransferStatus.UNCONFIRMED;
	        codeVerification = new Random().nextInt(90000000) + 10000000;
	    }


	    @ManyToOne
	    @JoinColumn(name = "uuid_compte", nullable = false) //Relation : * Transfers <-------- 1 Account
	    @JsonIgnoreProperties({"transfers", "requests", "client"})
	    private Account account;
	
	@OneToMany(mappedBy = "multipletransfer", fetch = FetchType.EAGER,  cascade={CascadeType.REMOVE})
	@JsonIgnoreProperties({"multipletransfer", "beneficiary"})
    private Collection<MultipleTransferBeneficiary> multipletransferbeneficiary;
	

}
