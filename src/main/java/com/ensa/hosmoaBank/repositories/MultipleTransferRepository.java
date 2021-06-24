package com.ensa.hosmoaBank.repositories;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import com.ensa.hosmoaBank.models.Account;
import com.ensa.hosmoaBank.models.Client;
import com.ensa.hosmoaBank.models.MultipleTransfer;
import com.ensa.hosmoaBank.models.Transfer;

@RepositoryRestController
public interface MultipleTransferRepository extends JpaRepository<MultipleTransfer,Long> {
	
	Optional<MultipleTransfer> findByIdAndAndAccount_Client(Long id, Client client);
	Optional<Collection<MultipleTransfer>> findByAccount(Account account);
}
