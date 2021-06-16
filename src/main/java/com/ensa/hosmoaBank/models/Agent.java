package com.ensa.hosmoaBank.models;

import java.util.Collection;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.*;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
@ToString
@JsonPropertyOrder({ "user" })
public class Agent {
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_admin") // Relation : every Agent ------< 1 Admin
	//@NotNull
	//@JsonIgnore
    @JsonIgnoreProperties({"agents", "agencies"})
    private Admin admin;

    @ManyToOne
    @JoinColumn(name = "id_agency") // Relation : 1 Agent ---->  1 Agency
	//@NotNull
    @JsonIgnoreProperties({"admin", "agents", "clients"})
	//@JsonIgnore
    private Agency agency;

    @OneToMany(mappedBy = "agent", fetch = FetchType.EAGER,  cascade={CascadeType.REMOVE})// Relation : every Agent ---->0..* clients
    @JsonIgnoreProperties({"agent","agency"})
	//@JsonIgnore
    private Collection<Client> clients;

    @OneToOne // pour la relation : un admin a un compte user pour la auth
    @JoinColumn(name = "id_user")
	//@JsonIgnore
    @JsonIgnoreProperties({"agent","id"})
    @JsonUnwrapped
    private User user;

    //Just for test

    public Agent(User user ,Admin admin , Agency agency) {
        this.user=user;
        this.admin=admin;
        this.agency=agency;
    }

	
	
}
