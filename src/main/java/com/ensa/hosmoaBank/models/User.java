package com.ensa.hosmoaBank.models;



import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Data
@MappedSuperclass
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    
    private String username; 
    
	private String email;
   
	private String password;
    
}
