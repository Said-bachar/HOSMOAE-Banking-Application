package com.ensa.hosmoaBank.models;

import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.ensa.hosmoaBank.enumerations.RechargeStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.*;

@Entity 
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@SQLDelete(sql = "UPDATE recharges SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Table(name = "recharges")
public class Recharge {
     
	@Id 
    @GeneratedValue(strategy = GenerationType.AUTO)//generation auto
    private Long id;

//    @NotNull
    private String operator ;

//    @NotNull
    private String numberPhone;

//    @NotNull
//    @Positive
    private double montant ;
    private RechargeStatus statut;

    @CreationTimestamp
    private Date dateDeRecharge;

    private boolean deleted;

    @ManyToOne
    @JoinColumn(name = "uuid_account", nullable = false) // Relation : Every Recharge ------> Account
    @JsonIgnoreProperties({"recharges", "transfers", "client"})
    private Account account;

}
