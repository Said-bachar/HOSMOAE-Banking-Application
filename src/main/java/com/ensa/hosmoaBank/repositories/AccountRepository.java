package com.ensa.hosmoaBank.repositories;

import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ensa.hosmoaBank.models.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
	
	public Collection<Account> findByClientId(Long client_id);
}
