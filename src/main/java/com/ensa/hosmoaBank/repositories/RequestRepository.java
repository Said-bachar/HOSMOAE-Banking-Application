package com.ensa.hosmoaBank.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import com.ensa.hosmoaBank.models.Client;
import com.ensa.hosmoaBank.models.Request;

@RepositoryRestController
public interface RequestRepository extends JpaRepository<Request, Long>{
    
	void deleteAllByClient(Client client);

    Request findByClient(Client client);
}
