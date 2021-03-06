package com.ensa.hosmoaBank.models;

 
import java.util.Collection;
import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@SQLDelete(sql = "UPDATE request SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class Request {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String nom;
    private String prenom;

    private Boolean accepted;

    private boolean deleted;

    @ManyToOne 
    @JoinColumn(name = "client_id")
    //@JsonIgnoreProperties({ "accounts", "notifications", "agent", "agency","picture" })
    @JsonIgnore
    private Client client;

    @UpdateTimestamp
    private Date timestamp;

    @PrePersist
    @PreUpdate
    public void beforeInsert () {
        accepted = false;
    }

}
