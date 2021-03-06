package com.ensa.hosmoaBank.repositories;


import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import com.ensa.hosmoaBank.models.Client;
import com.ensa.hosmoaBank.models.Transfer;

@RepositoryRestController
public interface TransferRepository extends JpaRepository<Transfer, Long>{
	
	Optional<Transfer> findByIdAndAndAccount_Client(Long id, Client client);
	//List<Transfer> findAllByAccount_Client(Client client);
}
