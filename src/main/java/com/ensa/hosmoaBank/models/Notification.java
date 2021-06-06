package com.ensa.hosmoaBank.models;

import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@EntityListeners(NotificationListener.class) // Link Notification entity to NotifiationListener
public class Notification {
	
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private Long id;
   
   private String content;
   
   private boolean readed;
   
   //@NoNull
   private String type;
   
   @CreationTimestamp
   private Date notificationDate;
   
   @ManyToOne
   @JoinColumn(name = "id_client", nullable = false) // Relation : every Notification --> 1 Client
   @JsonIgnore
   private Client client;
   
   @PrePersist
   void beforeInsert() {
	   this.readed = false;
   }
   
}
