package com.ensa.hosmoaBank.models;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.ManyToAny;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
	
	@ManyToOne
	@JoinColumn(name = "id_client")
    @JsonIgnoreProperties({"beneficiary"})
	@JsonIgnore
    private Client client;
	
}
