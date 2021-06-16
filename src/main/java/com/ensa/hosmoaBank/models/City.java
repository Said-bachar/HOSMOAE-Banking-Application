package com.ensa.hosmoaBank.models;

import java.util.*;

import javax.persistence.*;

import com.ensa.hosmoaBank.enumerations.AccountStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
public class City {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	 @OneToMany(mappedBy = "city")
	private List<Agency> agencies;

}
