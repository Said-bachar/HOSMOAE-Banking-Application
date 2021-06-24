package com.ensa.hosmoaBank.repositories;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;


import com.ensa.hosmoaBank.models.Beneficiary;
import com.ensa.hosmoaBank.models.Client;
import com.ensa.hosmoaBank.models.User;

@RepositoryRestController
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
	Optional<Collection<Beneficiary>> findByClient(Client client);
}
