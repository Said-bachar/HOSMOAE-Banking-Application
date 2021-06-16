package com.ensa.hosmoaBank.models;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@SQLDelete(sql = "UPDATE agency SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class Agency implements Serializable {
	
	private static final long serialVersionUID = 1L;

		@Id 
	    @GeneratedValue(strategy = GenerationType.IDENTITY) 
	    private Long id;

	    @CreationTimestamp
	    private Date creationDate;

	    @UpdateTimestamp
	    private Date updateDate;

	    private boolean deleted;

        //@NotNull
	    private String  agencyWording; //libellé agence

	    @ManyToOne
	    @JsonIgnoreProperties({"agencies"})
	    private City city;


	    @ManyToOne
	    @JoinColumn(name = "id_admin", nullable = false) // Relation :  every Agency ---> 1 Admin
	    @JsonIgnore
	    private Admin admin;

	    @OneToMany(mappedBy = "agency", fetch = FetchType.LAZY,  cascade={CascadeType.REMOVE}) // Relation : every agency ---> 0..* Agent
	    @JsonIgnore
	    private Collection<Agent> agents;

	    @OneToMany(mappedBy = "agency", fetch = FetchType.LAZY,  cascade={CascadeType.REMOVE})// Relation : every agency --> * Client
        //@JsonIgnore
	    @JsonIgnoreProperties({"agent","agency"})
	    private Collection<Client> clients;


	    //Just for test
	    public Agency(City city, String libelle, Admin admin) {
	        this.city          = city;
	        this.agencyWording = libelle;
	        this.admin         = admin;
	    }
	

}
