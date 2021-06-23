package com.ensa.hosmoaBank.controllers.agent;

import com.ensa.hosmoaBank.enumerations.Role;
import com.ensa.hosmoaBank.models.*;
import com.ensa.hosmoaBank.repositories.*;
import com.ensa.hosmoaBank.services.MailService;
import com.ensa.hosmoaBank.services.UploadService;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.*;

/*@RestController
@RequestMapping("/agent/api")
@Transactional
@CrossOrigin(value = "*")*/
public class AgentPanelController {

  /*Logger logger = LoggerFactory.getLogger(AgentPanelController.class);

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
  private NotificationRepository notificationRepository;

  @Autowired
  private RequestRepository requestRepository;

  @Autowired
  private UploadService uploadService;

  @Autowired
  private MailService mailService;


  @GetMapping(value = "/profile") // return Agent by id
  public Agent getAgent(){
      long id = 1L;
      return agentRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "L'agent avec id = " + id + " est introuvable."));
  }

  @PostMapping("/avatar/upload")
  @CacheEvict(cacheNames = "clients", allEntries = true)
  public ResponseEntity<?> uploadAvatar(@RequestParam("image") MultipartFile image) {
      Agent agent = getAgent();

      String fileName = uploadService.store(image);

      // generate download link
      String imageDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
              .path("/client/api/")
              .path("/avatar/")
              .path(fileName)
              .toUriString();

      // check if client has already uploaded an image
      if (agent.getUser().getPicture() != null)
          uploadService.delete(agent.getUser().getPicture());

      agent.getUser().setPicture(fileName);
      agentRepository.save(agent);

      Map<String, String> response = new HashMap<>();
      response.put("link", imageDownloadUri);

      return new ResponseEntity<>(response, HttpStatus.CREATED);

  }

  @GetMapping("/all")
  public Collection<User> getAllAgents(){
      return  userRepository.findAllByRole(Role.AGENT);    }

  
  @GetMapping(value = "/agency")
  public Agency getAgency() {
      Long id = 1L; 
      return agentRepository.findById(id).get().getAgency();
  }



  @GetMapping(value = "/clients") //show all clients , works
  public List<User> getClients(){
      return userRepository.findAllByRole(Role.CLIENT);
  }

  @GetMapping(value="/clients/{id}")
  public Client getOneClient(@PathVariable(value = "id")Long id) {
      try{
          return clientRepository.findById(id).get();
      } catch (NoSuchElementException | EntityNotFoundException e){
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le client avec id = " + id + " est introuvable")  ;
      }
  }

  @GetMapping(value="/clients/search/{nom}")
  public List<User> getClientByName(@PathVariable(value = "nom") String clientName) {
      try{
          return userRepository.findUserByRoleAndLastName(Role.CLIENT, clientName);
      } catch (NoSuchElementException | EntityNotFoundException e){
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le client avec nom = " + clientName + " est introuvable")  ;
      }
  }

 

  @PostMapping("/clients/add") //add new client , works
  public User addClient(@RequestBody  User user) {
      try {
          user.setRole(Role.CLIENT);
          //find first the client

          Agent agent = agentRepository.findById(1L).get();  // i choose id 1 just for test

          userRepository.save(user); // add user in table

          Client client = Client.builder().agent(agent).agency(agent.getAgency()).user(user).build();
          clientRepository.save(client); // add client in table
          mailService.sendVerificationMail(user);
          return user;
      } catch (DataIntegrityViolationException e) {
          throw new ResponseStatusException(HttpStatus.CONFLICT, "L'email que vous avez entré est déjà utilisé."+e.toString())  ;
      }
  }


  @DeleteMapping(value = "/clients/{id}/delete") // delete a client , works
  public ResponseEntity<String> deleteClient(@PathVariable(value = "id") Long id){
      try {
          Client client = clientRepository.findById(id).get();
          userRepository.delete(client.getUser());
          clientRepository.delete(client);
          accountRepository.deleteAll(client.getAccounts());

          return new ResponseEntity<>("Client est supprime avec succes." ,HttpStatus.OK);
      } catch (NoSuchElementException e){
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le client avec id = " + id + " est introuvable.");
      }
  }

 
  @PutMapping(value = "/clients/{id}/modify") 
  public User modifyClient(@PathVariable(value = "id") Long id , @RequestBody User user) throws UnirestException {
      logger.info("CLIENT ID = " + id);
      try {
          User userToModify = clientRepository.findById(id).get().getUser();
          if (user.getEmail() != null)
              userToModify.setEmail(user.getEmail());
          if (user.getLastName() != null)
              userToModify.setLastName(user.getLastName());
          if (user.getFirstName() != null)
              userToModify.setFirstName(user.getFirstName());
          if (user.getPhoneNumber() != null)
              userToModify.setPhoneNumber(user.getPhoneNumber());

          if (user.getEmail() != null && !user.getEmail().equals(userToModify.getEmail())) {
              userToModify.setEmailConfirmed(false);
              mailService.sendVerificationMail(user);
          }
          return userRepository.save(userToModify);
      }
      catch (NoSuchElementException e){
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client with id = " + id + " cannot be found.");
      }
  }

  @GetMapping(value = "/clients/{id}/accounts")
  public Collection<Account> getAllComptes(@PathVariable(value = "id")Long id) {
      try {
          return clientRepository.findById(id).get().getAccounts();
      } catch (NoSuchElementException e) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client with id  = " + id + " cannot be found.");
      }
  }

  @PostMapping(value = "/clients/{id}/accounts/add") // works
  public Account addClientAccount(@PathVariable(value = "id")Long id,@RequestBody Account account){
      try { //check firstly if client exist
          Client client = clientRepository.findById(id).get();
          account.setClient(client);
          account =  accountRepository.save(account);
          mailService.sendCompteDetails(client.getUser(), account);

          Notification notification = notificationRepository.save(Notification.builder()
              .client(account.getClient())
              .content("A <b>new account</b> has been added ! Check your email to get your code.")
              .build()
          );
          return account;
      } catch (NoSuchElementException e) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le client avec id = " + id + " est introuvable.");
      }
  }

  @GetMapping(value = "/accounts/{id}") //works
  public Account getClientAccountByNum(@PathVariable(value = "id") String numeroCompte){
      Agent agent = agentRepository.findById(1L).get();
      try {
//          String subNumero = numeroCompte.substring(8);
          Account compte = accountRepository.findByClient_Agent_AgencyAndAccountNumber(agent.getAgency(), numeroCompte).get();
          return compte;
      } catch (NoSuchElementException e) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account  = " + numeroCompte + " cannot be found.");
      }
  }

  @DeleteMapping(value = "/accounts/{id}/delete") //works
  public ResponseEntity<String> deleteClientAccount(@PathVariable(value = "id") String numeroCompte){
      Agent agent = agentRepository.findById(1L).get();
      try {
          String subNumero = numeroCompte.substring(8);
          System.out.println(subNumero);
//          compteRepository.findCompteByNumeroCompteIsContaining(subNumero);
//          System.out.println(compteRepository.findCompteByNumeroCompteIsContaining(subNumero).getSolde());
          // verifier si le compte est de meme agence que l'agent
          Account account = accountRepository.findByClient_Agent_AgencyAndAccountNumber(agent.getAgency(), subNumero).get();
          accountRepository.delete(account);

          Notification notification = notificationRepository.save(Notification.builder()
              .client(account.getClient())
              .content("The account nº <b>" + account.getNumeroAccountHidden() + "</b> has been deleted.")
              .build()
          );

          return new ResponseEntity<>("Le compte est supprime avec succes." , HttpStatus.OK);
      } catch (NoSuchElementException e) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le compte avec le numero = " + numeroCompte + " est introuvable.");
      }
  }
  
  @PutMapping(value = "/accounts/{id}/modify")
  public Account modifyClientCompte(@PathVariable(value = "id") String numeroCompte ,@RequestBody Account account) {
      try {
//          Compte compteToModify = compteRepository.findById(numeroCompte).get();
          Account accountToModify = accountRepository.findByAccountNumberContaining(numeroCompte); // just for test
          if (account.getEntitled() != null)
              accountToModify.setEntitled(account.getEntitled());
          if (account.getAccountBalance() != 0.0)
              accountToModify.setAccountBalance(account.getAccountBalance());

          Notification notification = notificationRepository.save(Notification.builder()
              .client(account.getClient())
              .content("Le compte de nº <b>" + account.getNumeroAccountHidden() + "</b> à été modifié par votre agent.")
              .build()
          );

          return accountRepository.save(accountToModify);
      }  catch (NoSuchElementException e) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account = " + numeroCompte + " cannot be found.");
      }
  }

  @PutMapping(value = "/requests/{id}/approve")
  public ResponseEntity<String> approveRequest (@PathVariable("id") Long id) {
      Request request = requestRepository.findById(id).get();
      User clientUser = request.getClient().getUser();

      if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(clientUser.getEmail())) {
          clientUser.setEmail(request.getEmail());
          clientUser.setEmailConfirmed(false);
          mailService.sendVerificationMail(clientUser);
      }
      if (request.getNom() != null)
          clientUser.setLastName(request.getNom());
      if (request.getPrenom() != null)
          clientUser.setFirstName(request.getPrenom());

      userRepository.save(clientUser);
      requestRepository.delete(request);

      Notification notification = Notification.builder()
          .content("Your information has been updated.Check your profile.")
          .client(clientUser.getClient())
          .build();

      notificationRepository.save(notification);

      return new ResponseEntity<>(HttpStatus.OK);

  }

  @GetMapping(value = "/requests")
  public List<Request> getRequests () {

      return  requestRepository.findAll();

  }*/

}
