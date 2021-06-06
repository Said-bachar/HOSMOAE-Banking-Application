package com.ensa.hosmoaBank.models;

import java.util.*;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;

import org.hibernate.annotations.*;


import com.fasterxml.jackson.annotation.*;


import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonPropertyOrder({"user"})
@Table(name = "clients")
@SQLDelete(sql = "UPDATE clients SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class Client {
	
	    @Id 
	    @GeneratedValue(strategy = GenerationType.IDENTITY )
	    private Long id;
	  
	    private boolean deleted;

	    @ManyToOne
	    @JoinColumn(name = "id_agent") // pour la relation : chaque client a un seul agent
	    //@NotNull
	    @JsonIgnoreProperties({"clients", "agence", "admin",})
	    private Agent agent;

	    @ManyToOne
	    @JoinColumn(name = "id_agency") // pour la relation : chaque client a un seul agent
	    //@NotNull
	    @JsonIgnoreProperties({"clients"})
	    private Agency agency;

	    @OneToMany(mappedBy = "client",fetch = FetchType.LAZY,  cascade={CascadeType.REMOVE}) // Relation :every Client ----> 1..* Accounts
	    //@NotNull
	    @JsonIgnoreProperties({"client"})
	    private Collection<Account> accounts;

	    @OneToMany(mappedBy = "client",fetch = FetchType.LAZY,  cascade={CascadeType.REMOVE})  // Relation : every Client a ---> 0..* Notification
	    @JsonIgnore
	    private Collection<Notification> notifications;

	    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY,  cascade={CascadeType.REMOVE})
	    @JsonIgnoreProperties("client")
	    private Collection<Request> requests;

	    @OneToOne // Relation : 1 Admin --->  1 User for Auth...
	    @JoinColumn(name = "id_user")
	    @JsonIgnoreProperties({"id","client", "authorities", "username" , "admin"})
	    @JsonUnwrapped
	    private User user;

	    //Just for test
	    public Client(User user,Agent agent,Agency agency) {
	        this.user=user;
	        this.agent=agent;
	        this.agency=agency;
	    }
}
