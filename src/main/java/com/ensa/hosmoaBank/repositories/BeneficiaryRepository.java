package com.ensa.hosmoaBank.repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ensa.hosmoaBank.models.Beneficiary;
import com.ensa.hosmoaBank.models.Client;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long>{
	
	public Collection<Beneficiary> findByClient(Client client);
}
