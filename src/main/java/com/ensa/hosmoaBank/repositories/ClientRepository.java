package com.ensa.hosmoaBank.repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ensa.hosmoaBank.models.Account;
import com.ensa.hosmoaBank.models.Agent;
import com.ensa.hosmoaBank.models.Beneficiary;
import com.ensa.hosmoaBank.models.Client;
import com.ensa.hosmoaBank.models.Transfer;

public interface ClientRepository extends JpaRepository<Client, Long>{
	
	public Client findByAccountsIn(Collection<Account> accounts);
	public Collection<Client> findByAgent(Agent agenct);
	//public Client findAllByMultipleTransfer(MultipleTransfer multipleTransfer);
	public Collection<Client> findByTransfersIn(Collection<Transfer> transfers);
	public Collection<Client> findByBeneficiariesIn(Collection<Beneficiary> beneficiaries);
}
