package com.ensa.hosmoaBank.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import com.ensa.hosmoaBank.models.ConfirmationToken;

@RepositoryRestController
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long>{

	ConfirmationToken findByConfirmationToken(String confirmationToken);

}	
