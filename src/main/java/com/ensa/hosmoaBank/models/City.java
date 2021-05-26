package com.ensa.hosmoaBank.models;

import java.util.Collection;

import javax.persistence.*;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "cities")
public class City {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	
	 @OneToMany(mappedBy = "city", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
	Collection<Agency> agencies;

}
