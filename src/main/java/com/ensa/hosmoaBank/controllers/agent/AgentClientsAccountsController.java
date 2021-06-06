package com.ensa.hosmoaBank.controllers.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.csrf.CsrfWebFilter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.ensa.hosmoaBank.enumerations.AccountStatus;
import com.ensa.hosmoaBank.models.Account;
import com.ensa.hosmoaBank.models.AddAccountVerification;
import com.ensa.hosmoaBank.models.Agent;
import com.ensa.hosmoaBank.models.Client;
import com.ensa.hosmoaBank.repositories.AccountRepository;
import com.ensa.hosmoaBank.repositories.AgencyRepository;
import com.ensa.hosmoaBank.repositories.AgentRepository;
import com.ensa.hosmoaBank.repositories.ClientRepository;
import com.ensa.hosmoaBank.repositories.NotificationRepositroty;
import com.ensa.hosmoaBank.repositories.RequestRepository;
import com.ensa.hosmoaBank.repositories.UserRepository;
import com.ensa.hosmoaBank.services.MailService;
import com.ensa.hosmoaBank.services.UploadService;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/agent/api/clients")
@Transactional
@CrossOrigin(value = "*")
public class AgentClientsAccountsController {
	
	
	 Logger logger = LoggerFactory.getLogger(AgentClientsAccountsController.class);

	    @Autowired
	    private AgentRepository agentRepository;

	    @Autowired
	    private AgencyRepository agencyRepository;

	    @Autowired
	    private UserRepository userRepository;

	    @Autowired
	    private ClientRepository clientRepository;

	    @Autowired
	    private AccountRepository accountRepository;

	    @Autowired
	    private NotificationRepositroty notificationRepository;

	    @Autowired
	    private RequestRepository requestRepository;

	    @Autowired
	    private UploadService uploadService;

	    @Autowired
	    private MailService mailService;

	    @Autowired
	    private AgentProfileController agentProfileController;

	    @Autowired
	    private AgentClientsController agentClientsController;

	    @Autowired
	    private PasswordEncoder encoder;




	    @GetMapping(value = "/{id}/accounts") // 
	    public Collection<Account> getAllComptes(@PathVariable(value = "id")Long id) {
	        try {
	            return clientRepository.findById(id).get().getAccounts();
	        } catch (NoSuchElementException e) {
	            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client with id = " + id + " is not found.");
	        }
	    }


	    @PostMapping(value = "/{id}/accounts/add") // works
	    public Account addClientCompte(@PathVariable(value = "id")Long id, @RequestBody AddAccountVerification addCompteVerification){
	        Agent agent = agentProfileController.getAgent();
	        Account compte = new Account();
	        System.out.println(addCompteVerification);
	        Boolean isMatch = encoder.matches(addCompteVerification.getAgentPassword(),agent.getUser().getPassword());
	        try { //check firstly if client exist
	            if(!isMatch){
	                throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Wrong password.");
	            }
	            compte.setEntitled(addCompteVerification.getEntitled());
	            compte.setAccountBalance(addCompteVerification.getBalance());

	            Client client = clientRepository.findById(id).get();
	            compte.setClient(client);
	            accountRepository.save(compte);
	            mailService.sendCompteDetails(client.getUser(), compte);

	            //TODO check notificatiion class , there s some prb while parsing a new account for client
//	            Notification notification = notificationRepository.save(Notification.builder()
//	                    .client(compte.getClient())
//	                    .contenu("Un <b>nouveau compte</b> à été ajouté ! Veuillez verifier votre e-mail pour récupérer votre code.")
//	                    .build()
//	            );
	            return compte;
	        } catch (NoSuchElementException e) {
	            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client with id = " + id + " is not found.");
	        }
	    }

	    @GetMapping(value = "/{id}/accounts/{idAccount}") //works
	    public Account getClientCompteByNum(@PathVariable(value = "id") Long id , @PathVariable(value = "idAccount") String numeroCompte){
	        Agent agent = agentProfileController.getAgent();
	        System.out.println(id);
	        Client client = agentClientsController.getOneClient(id);
	        try {
//	            String subNumero = numeroCompte.substring(8);
	                Account compte = accountRepository.findByClientAndClient_AgencyAndAccountNumberContaining(client ,agent.getAgency(), numeroCompte).get();
	            return compte;
	        } catch (NoSuchElementException e) {
	            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account with num = " + numeroCompte + " is not found.");
	        }
	    }


	    @DeleteMapping(value = "/{id}/accounts/{idAccount}/delete") //works
	    public ResponseEntity<?> deleteClientCompte(@PathVariable(value = "id") Long id , @PathVariable(value = "idAccount") String numeroCompte){
	        Agent agent = agentProfileController.getAgent();
	        try {
	            String subNumero = numeroCompte.substring(8);
	            System.out.println(subNumero);
	            Account compte = accountRepository.findByClient_AgencyAndAccountNumberContaining(agent.getAgency(), subNumero).get();
	            accountRepository.delete(compte);

//	            Notification notification = notificationRepository.save(Notification.builder()
//	                    .client(compte.getClient())
//	                    .contenu("Le compte de nº <b>" + compte.getNumeroCompteHidden() + "</b> à été supprimé.")
//	                    .build()
//	            );

	            return new ResponseEntity<>("Account is deleted with success." , HttpStatus.OK);
	        } catch (NoSuchElementException e) {
	            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account with num = " + numeroCompte + " is not found.");
	        }
	    }


	    @PutMapping(value = "/{id}/accounts/{idAccounts}/update")
	    public Account modifyClientCompte(@PathVariable(value = "id") Long id ,
	                                     @PathVariable(value = "idAccounts") String accountNumber ,
	                                     @RequestBody Account account) {
	        try {
	        	Account compteToModify = accountRepository.findById(accountNumber).get();
//	            Client client = clientRepository.findById(id).get();
//	            Compte compteToModify = getClientCompteByNum(id,numeroCompte) ;// just for test
	            if (account.getEntitled() != null)
	                compteToModify.setEntitled(account.getEntitled());
	            if (account.getAccountBalance() != 0.0)
	                compteToModify.setAccountBalance(account.getAccountBalance());

//	            Notification notification = notificationRepository.save(Notification.builder()
//	                    .client(client)
//	                    .contenu("Le compte de nº <b>" + compteToModify.getNumeroCompteHidden() + "</b> à été modifié par votre agent.")
//	                    .build()
//	            );

	            return accountRepository.save(compteToModify);
	        }  catch (NoSuchElementException e) {
	            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account with num = " + accountNumber + " is not found !.");
	        }
	    }
	    @PutMapping(value = "/{id}/accounts/{idAccounts}/update/status")
	    public Account modifyClientCompteStatus(@PathVariable(value = "id") Long id ,
	                                           @PathVariable(value = "idAccounts") String accountNumber ,
	                                           @RequestParam(value = "status") String status ) {
	        try {
	            Account compteToModify = accountRepository.findById(accountNumber).get();
//	            Client client = userRepository.findById(id).get().getClient();
//	            Compte compte = getClientCompteByNum(id,numeroCompte) ;// just for test
	            System.out.println(status);

	            switch (status){
	                case "ACTIVER" : compteToModify.setStatut(AccountStatus.ACTIVE);break;
	                case "BLOQUER" : compteToModify.setStatut(AccountStatus.BLOCKED);break;
	                case "SUSPENDRE" : compteToModify.setStatut(AccountStatus.SUSPENDED);break;
	            }
	            accountRepository.save(compteToModify);


//	            Notification notification = notificationRepository.save(Notification.builder()
//	                    .client(client)
//	                    .contenu("Le compte de nº <b>" + compteToModify.getNumeroCompteHidden() + "</b> à été modifié par votre agent.")
//	                    .build()
//	            );

	            return compteToModify;
	        }  catch (NoSuchElementException e) {
	            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account with num = " + accountNumber + " is not found.");
	        }
	    }

}
