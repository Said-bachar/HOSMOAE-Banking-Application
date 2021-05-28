package com.ensa.hosmoaBank.models;

import java.util.Collection;

import javax.persistence.*;

import lombok.*;

@NoArgsConstructor 
@AllArgsConstructor
@Data
@Entity
@Table(name = "beneficiaries")
public class Beneficiary{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "first_name")
    private String fistName;
	@Column(name = "last_name")
    private String lastName;
    private Long account_number;
    @ManyToOne @JoinColumn(name = "id_client", nullable = false)
	private Client client;
    @ManyToMany(mappedBy = "beneficiaries")
    private Collection<MultipleTransfer> multipletransfers;
    
}
