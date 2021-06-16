package com.ensa.hosmoaBank.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import com.ensa.hosmoaBank.models.Beneficiary;

@RepositoryRestController
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
	

}
