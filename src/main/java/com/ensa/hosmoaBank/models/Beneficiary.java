package com.ensa.hosmoaBank.models;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Entity
@NoArgsConstructor
public class Beneficiary {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String lastName;
	private String firstName;
	private String accountNumber;
	
	@OneToMany(mappedBy = "beneficiary", fetch = FetchType.LAZY,  cascade={CascadeType.REMOVE})
	@JsonIgnoreProperties({"multipletransfer", "beneficiary"})
    private Collection<MultipleTransferBeneficiary> multipletransferbeneficiary;
}
