package com.ensa.hosmoaBank.repositories;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import com.ensa.hosmoaBank.models.Account;
import com.ensa.hosmoaBank.models.Agency;
import com.ensa.hosmoaBank.models.Client;

@RepositoryRestController
public interface AccountRepository extends JpaRepository<Account, String> {
	
	   Collection<Account> findAllByClientId(Long id);

	    Optional<Account> findByAccountNumberAndClient(String numeroAccount, Client client);

//	    Account findByClient_Agent_AgencyAndNumeroAccount(String numeroAccount);

	    Optional<Account> findByClient_Agent_AgencyAndAccountNumber(Agency Agency, String id);

	    Account findByAccountNumberContaining(String id);
	    Account findByAccountNumber(String id);

	    Account findAccountByAccountNumberIsContaining(String numeroAccount);

	    Optional<Account> findByClient_Agent_AgencyAndAccountNumberContaining(Agency Agency, String id); // that s what we need because numeroAccount is hidden we saw just the last 4 numbers
	    Optional<Account> findByClient_AgencyAndAccountNumberContaining(Agency Agency, String id);
	    Optional<Account> findByClientAndClient_AgencyAndAccountNumberContaining(Client client ,Agency Agency, String id);


	    Collection<Account> findAllByClientIdAndAccountNumber(Long id,String numero_Account);

//	    Optional<Account> findByClient_Agent_AgencyAndNumeroAccount(Agency Agency, String id);
//	    Page<Account> findAllByClientId(Long id, Pageable pageable);

}
