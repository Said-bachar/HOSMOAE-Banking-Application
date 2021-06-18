package com.ensa.hosmoaBank.models;


import java.util.*;


import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.validator.constraints.CreditCardNumber;

import com.ensa.hosmoaBank.enumerations.AccountStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.*;

@NoArgsConstructor 
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@SQLDelete(sql = "UPDATE account SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@JsonPropertyOrder({ "accountNumber" })
public class Account {
	
	  @Id
	  @GeneratedValue(generator = "cn-generator")
      @GenericGenerator(name = "cn-generator", strategy = "com.ensa.hosmoaBank.utilities.CreditCardNumberGenerator")
	  @CreditCardNumber
	  private String accountNumber;
	  
	  private String entitled;
	  
	  @Column(name = "account_balance") 
	  private double accountBalance;
	 
	  
	  @Column(name = "update_date") 
	  private Date updateDate;
	  
	  @Column(name = "creation_date") 
	  private Date creationDate;
	  
	  @Column(name = "last_operation") 
	  private Date lastOperation;
	  
	  @Enumerated(EnumType.STRING)
	  private AccountStatus statut;
	 
	  private boolean deleted;
	  
	  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	  private String keySecret;
	 
      @ManyToOne
      @JoinColumn(name = "id_client")
      @JsonIgnoreProperties({"accounts", "notifications"})
	  @JsonIgnore
      private Client client;
      
      private String raison;
      
      @OneToMany(mappedBy = "account",fetch = FetchType.LAZY,  cascade={CascadeType.REMOVE})
      @JsonIgnoreProperties({"account"})
      private Collection<Transfer> transfers; // Relation : * Account ---> 0..* Transfer
      
      @OneToMany(mappedBy = "account",fetch = FetchType.LAZY,  cascade={CascadeType.REMOVE})
      @JsonIgnoreProperties({"account"})
      private Collection<MultipleTransfer> multipletransfers; // Relation : * Account ---> 0..* MultipleTransfer

      @OneToMany(mappedBy = "account",fetch = FetchType.LAZY,  cascade={CascadeType.REMOVE})
      @JsonIgnoreProperties({"account"})
      private Collection<Recharge> recharges; 


      // triggered at begining of transaction : generate default values for Account
      @PrePersist
      void beforeInsert() {
          System.out.println("SETTING DEFAULT VALUES FOR COMPTE");
          
          statut = AccountStatus.ACTIVE;
          keySecret = String.valueOf(new Random().nextInt(90000000) + 10000000);
      }

      
      public String getNumeroAccountHidden() {
          // we will add condition on roles on this getter, for the moment let's hide the field on everyone.
          return new String(new char[8]).replace('\0', '*').concat(accountNumber.substring(12));
      }

      public Account(String entitled, Client client, double balance) {

          this.entitled=entitled;
          this.client=client;
          this.accountBalance = balance;

      }
}
