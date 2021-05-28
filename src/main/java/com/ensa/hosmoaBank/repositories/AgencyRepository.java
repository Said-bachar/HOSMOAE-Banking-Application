package com.ensa.hosmoaBank.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ensa.hosmoaBank.models.Agency;

public interface AgencyRepository extends JpaRepository<Agency, Long>{
	
}
