package com.ensa.hosmoaBank.models;

import java.util.*;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.*;

 

@Entity
@Table(name = "admins")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({ "user" })
public class Admin {
	
	@Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @OneToMany(mappedBy = "admin", fetch = FetchType.LAZY,  cascade={CascadeType.REMOVE}) // pour la relation : chaque admin a pls agents
    Collection<Agent> agents;

    @OneToMany(mappedBy = "admin", fetch = FetchType.LAZY,  cascade={CascadeType.REMOVE})// pour la relation : chaque admin a pls agences
    Collection<Agency> agences;

    @OneToOne // pour la relation : un admin a un compte user pour la auth
    @JoinColumn(name = "id_user")
    @JsonIgnoreProperties({"admin", "emailConfirmed", "dateUpdate", "dateOfCreation", "id" })
    @JsonUnwrapped
    private User user;

    //Just for test
    public Admin(User user) {
        this.user=user;
    }
	 
      
}
