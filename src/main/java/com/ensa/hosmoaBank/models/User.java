package com.ensa.hosmoaBank.models;

import javax.persistence.*;

import lombok.*;


@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
	private Long id;
    @Column(name = "username")
    private String username; 
    @Column(name = "email", unique = true)
	private String email;
    @Column(name = "password")
	private String password;
    @Column(name = "email_confirmed")
    private boolean emailConfirmed;
}
