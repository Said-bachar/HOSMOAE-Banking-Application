package com.ensa.hosmoaBank.models;

import java.util.Collection;

import javax.persistence.*;

@Entity
public class Agent extends User{
    
	@ManyToOne @JoinColumn(name = "id_admin", nullable = false)
	private Admin admin;
	@ManyToOne @JoinColumn(name = "id_agency")
	private Agency agency;
	@OneToMany(mappedBy = "agent", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE}) 
	Collection<Client> clients;
	
	
}
