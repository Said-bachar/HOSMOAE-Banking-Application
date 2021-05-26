package com.ensa.hosmoaBank.models;

import java.util.Collection;

import javax.persistence.*;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "agencies")
public class Agency {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "agency_name")
	private String agencyName;
	
	@ManyToOne @JoinColumn(name = "id_city")
	private City city;
	
	@ManyToOne @JoinColumn(name = "id_admin", nullable = false)
	private Admin admin;
	
	@OneToMany(mappedBy = "agency", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
	Collection<Agent> agents;
	

}
