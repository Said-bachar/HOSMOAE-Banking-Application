package com.ensa.hosmoaBank.models;

import java.util.*;

import javax.persistence.*;
import lombok.*;

@Data 
@NoArgsConstructor 
@AllArgsConstructor
@Entity
public class Admin {
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_admin")
    private Long id;
	
	//Relation: 1 Admin ----> * Agencies
	@OneToMany(mappedBy = "admin", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
	Collection<Agency> agencies;
	//Relation: 1 Admin ----> * Agents
	@OneToMany(mappedBy = "admin", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
	Collection<Agent> agents;
      
}
