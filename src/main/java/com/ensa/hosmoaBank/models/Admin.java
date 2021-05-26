package com.ensa.hosmoaBank.models;

import java.util.*;

import javax.persistence.*;
import lombok.*;

 

@Entity
@Table(name = "admins")
@Getter
public class Admin extends User{
	
	  //Relation: 1 Admin ----> * Agencies
	  
	  @OneToMany(mappedBy = "admin", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE}) Collection<Agency> agencies; 
	  //Relation: 1 Admin ---->  * Agents
	  
	  @OneToMany(mappedBy = "admin", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE}) Collection<Agent> agents;
	 
      
}
