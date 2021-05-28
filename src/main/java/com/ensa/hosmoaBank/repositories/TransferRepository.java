package com.ensa.hosmoaBank.repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ensa.hosmoaBank.models.Client;
import com.ensa.hosmoaBank.models.Transfer;

public interface TransferRepository extends JpaRepository<Transfer, Long>{
	
	public Collection<Transfer> findAllByClient(Client client);
}
