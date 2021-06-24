package com.ensa.hosmoaBank.repositories;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import com.ensa.hosmoaBank.models.*;


@RepositoryRestController
public interface ClientRepository extends JpaRepository<Client, Long>{
	 
	Optional<Client> findByUser(User user);
    Client findClientByAccounts(Account account);

    Collection<Client> findAllByAgencyId(Long id);
	
}
