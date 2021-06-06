package com.ensa.hosmoaBank.repositories;

import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import com.ensa.hosmoaBank.models.Account;

@RepositoryRestController
public interface AccountRepository extends JpaRepository<Account, Long> {
	
	public Collection<Account> findByClientId(Long client_id);
}
