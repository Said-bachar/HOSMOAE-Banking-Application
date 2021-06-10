package com.ensa.hosmoaBank.controllers.agent;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.data.domain.Example;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.server.ResponseStatusException;
//import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
//
//import javax.persistence.EntityNotFoundException;
//import javax.transaction.Transactional;
//import java.util.*;
//
//@RestController
//@RequestMapping("/agent/api")
//@Transactional
//@CrossOrigin(value = "*")
//public class AgentPanelController {
//
//  Logger logger = LoggerFactory.getLogger(AgentPanelController.class);
//
//  @Autowired
//  private AgentRepository agentRepository;
//
//  @Autowired
//  private AgenceRepository agenceRepository;
//
//  @Autowired
//  private UserRepository userRepository;
//
//  @Autowired
//  private ClientRepository clientRepository;
//
//  @Autowired
//  private CompteRepository compteRepository;
//
//  @Autowired
//  private NotificationRepository notificationRepository;
//
//  @Autowired
//  private DemandeRepository demandeRepository;
//
//  @Autowired
//  private UploadService uploadService;
//
//  @Autowired
//  private MailService mailService;
//
////  ************************************************* API Agent profile ************************************************************
//
////  @GetMapping(value = "/profile") // return Agent by id
////  public User getAgent(){
////      try {
////          return agentRepository.findById(2L).get().getUser();
////      } catch (NoSuchElementException e) {
////          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'agent avec id = " + id + " est introuvable.");
////      }
////  }
//
////  **************************************************************************************************************************
//
//  @GetMapping(value = "/profile") // return Agent by id
//  public Agent getAgent(){
//      long id = 1L;
//      return agentRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "L'agent avec id = " + id + " est introuvable."));
//  }
//
//  @PostMapping("/avatar/upload")
//  @CacheEvict(cacheNames = "clients", allEntries = true)
//  public ResponseEntity<?> uploadAvatar(@RequestParam("image") MultipartFile image) {
//      Agent agent = getAgent();
//
//      String fileName = uploadService.store(image);
//
//      // generate download link
//      String imageDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//              .path("/client/api/")
//              .path("/avatar/")
//              .path(fileName)
//              .toUriString();
//
//      // check if client has already uploaded an image
//      if (agent.getUser().getPhoto() != null)
//          uploadService.delete(agent.getUser().getPhoto());
//
//      agent.getUser().setPhoto(fileName);
//      agentRepository.save(agent);
//
//      Map<String, String> response = new HashMap<>();
//      response.put("link", imageDownloadUri);
//
//      return new ResponseEntity<>(response, HttpStatus.CREATED);
//
//  }
////just for test
//  @GetMapping("/all")
//  public Collection<User> getAllAgents(){
//      return  userRepository.findAllByRole(Role.AGENT);    }
//
//  //    *********************************************************************************************************************
//  //    *****************************************************API Agence profile **********************************************
//
//  @GetMapping(value = "/agence")
//  public Agence getAgence() {
//      Long id = 1L; //just a test , we will use the token to get agent id
//      return agentRepository.findById(id).get().getAgence();
//  }
//
//
//
//  //    ************************************************************************************************************************
//  //    ************************************************* API Get All clients ******************************************************
//
//
//  @GetMapping(value = "/clients") //show all clients , works
//  public List<User> getClients(){
//      return userRepository.findAllByRole(Role.CLIENT);
//  }
//
//  //    ************************************************************************************************************************
//  //    ************************************************* API Get One Client ***************************************************
//
//  @GetMapping(value="/clients/{id}")
//  public Client getOneClient(@PathVariable(value = "id")Long id) {
//      try{
//          return clientRepository.findById(id).get();
//      } catch (NoSuchElementException | EntityNotFoundException e){
//          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le client avec id = " + id + " est introuvable")  ;
//      }
//  }
//
//  //    ************************************************************************************************************************
//  //    ************************************************* API Search for Client by Name ***************************************************
//
//  @GetMapping(value="/clients/rechercher/{nom}")
//  public List<User> getClientByName(@PathVariable(value = "nom") String clientName) {
//      try{
//          return userRepository.findUserByRoleAndNom(Role.CLIENT, clientName);
//      } catch (NoSuchElementException | EntityNotFoundException e){
//          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le client avec nom = " + clientName + " est introuvable")  ;
//      }
//  }
//
//  //    ******************************************************************************************************************
//  //    ***************************************************** API ADD Client **********************************************
//
//
//  @PostMapping("/clients/ajouter") //add new client , works
//  public User addClient(@RequestBody  User user) {
//      try {
//          user.setRole(Role.CLIENT);
//          //find first the client
//
//          Agent agent = agentRepository.findById(1L).get();  // i choose id 1 just for test
//
//          userRepository.save(user); // add user in table
//
//          Client client = Client.builder().agent(agent).agence(agent.getAgence()).user(user).build();
//          clientRepository.save(client); // add client in table
//          mailService.sendVerificationMail(user);
//          return user;
//      } catch (DataIntegrityViolationException e) {
//          throw new ResponseStatusException(HttpStatus.CONFLICT, "L'email que vous avez entré est déjà utilisé."+e.toString())  ;
//      }
//  }
//
//  //    *********************************************************************************************************************
//  //    ***************************************************** API Delete Client **********************************************
//
//
//  @DeleteMapping(value = "/clients/{id}/supprimer") // delete a client , works
//  public ResponseEntity<String> deleteClient(@PathVariable(value = "id") Long id){
//      try {
//          Client client = clientRepository.findById(id).get();
//          userRepository.delete(client.getUser());
//          clientRepository.delete(client);
//          compteRepository.deleteAll(client.getComptes());
//
//          return new ResponseEntity<>("Client est supprime avec succes." ,HttpStatus.OK);
//      } catch (NoSuchElementException e){
//          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le client avec id = " + id + " est introuvable.");
//      }
//  }
//
//  //    **********************************************************************************************************************
//  //    ***************************************************** API Modify Client **********************************************
//
//
//  @PutMapping(value = "/clients/{id}/modifier") // modify client , works
//  public User modifyClient(@PathVariable(value = "id") Long id , @RequestBody User user) throws UnirestException {
//      logger.info("CLIENT ID = " + id);
//      try {
//          User userToModify = clientRepository.findById(id).get().getUser();
//          if (user.getEmail() != null)
//              userToModify.setEmail(user.getEmail());
//          if (user.getNom() != null)
//              userToModify.setNom(user.getNom());
//          if (user.getPrenom() != null)
//              userToModify.setPrenom(user.getPrenom());
//          if (user.getNumeroTelephone() != null)
//              userToModify.setNumeroTelephone(user.getNumeroTelephone());
//
//          // renvoyer le code de confirmation pr verifier le nouveau email
//          if (user.getEmail() != null && !user.getEmail().equals(userToModify.getEmail())) {
//              userToModify.setEmailConfirmed(false);
//              mailService.sendVerificationMail(user);
//          }
//          return userRepository.save(userToModify);
//      }
//      catch (NoSuchElementException e){
//          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le client avec id = " + id + " est introuvable.");
//      }
//  }
//
//  //    **********************************************************************************************************************
//  //    ******************************************************** API get Client Comptes***************************************
//
//  @GetMapping(value = "/clients/{id}/comptes") // works v2
//  public Collection<Compte> getAllComptes(@PathVariable(value = "id")Long id) {
//      try {
//          return clientRepository.findById(id).get().getComptes();
//      } catch (NoSuchElementException e) {
//          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le client avec id = " + id + " est introuvable.");
//      }
//  }
//
//  //    **********************************************************************************************************************
//  //    *********************************************************** API Add Client Compte *************************************
//
//  @PostMapping(value = "/clients/{id}/comptes/ajouter") // works
//  public Compte addClientCompte(@PathVariable(value = "id")Long id,@RequestBody Compte compte){ // PS : if you didnt insert the solde , will be auto 0.0 and always its >0
//      try { //check firstly if client exist
//          Client client = clientRepository.findById(id).get();
//          compte.setClient(client);
//          compte =  compteRepository.save(compte);
//          mailService.sendCompteDetails(client.getUser(), compte);
//
//          Notification notification = notificationRepository.save(Notification.builder()
//              .client(compte.getClient())
//              .contenu("Un <b>nouveau compte</b> à été ajouté ! Veuillez verifier votre e-mail pour récupérer votre code.")
//              .build()
//          );
//          return compte;
//      } catch (NoSuchElementException e) {
//          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le client avec id = " + id + " est introuvable.");
//      }
//  }
//
//  //    **********************************************************************************************************************
//  //    *********************************************************** API get Client Compte by numero compte *********************************
////just for test
//  @GetMapping(value = "/comptes/{id}") //works
//  public Compte getClientCompteByNum(@PathVariable(value = "id") String numeroCompte){
//      Agent agent = agentRepository.findById(1L).get();
//      try {
////          String subNumero = numeroCompte.substring(8);
//          Compte compte = compteRepository.findByClient_Agent_AgenceAndNumeroCompteContaining(agent.getAgence(), numeroCompte).get();
//          return compte;
//      } catch (NoSuchElementException e) {
//          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le compte avec le numero = " + numeroCompte + " est introuvable.");
//      }
//  }
//  //    **********************************************************************************************************************
//  //    *********************************************************** API Delete Client Compte *********************************
//
//  @DeleteMapping(value = "/comptes/{id}/supprimer") //works
//  public ResponseEntity<String> deleteClientCompte(@PathVariable(value = "id") String numeroCompte){
//      Agent agent = agentRepository.findById(1L).get();
//      try {
//          String subNumero = numeroCompte.substring(8);
//          System.out.println(subNumero);
////          compteRepository.findCompteByNumeroCompteIsContaining(subNumero);
////          System.out.println(compteRepository.findCompteByNumeroCompteIsContaining(subNumero).getSolde());
//          // verifier si le compte est de meme agence que l'agent
//          Compte compte = compteRepository.findByClient_Agent_AgenceAndNumeroCompteContaining(agent.getAgence(), subNumero).get();
//          compteRepository.delete(compte);
//
//          Notification notification = notificationRepository.save(Notification.builder()
//              .client(compte.getClient())
//              .contenu("Le compte de nº <b>" + compte.getNumeroCompteHidden() + "</b> à été supprimé.")
//              .build()
//          );
//
//          return new ResponseEntity<>("Le compte est supprime avec succes." , HttpStatus.OK);
//      } catch (NoSuchElementException e) {
//          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le compte avec le numero = " + numeroCompte + " est introuvable.");
//      }
//  }
//  //    **********************************************************************************************************************
//  //    *********************************************************** API modify Client Compte *********************************
//
//  @PutMapping(value = "/comptes/{id}/modifier")
//  public Compte modifyClientCompte(@PathVariable(value = "id") String numeroCompte ,@RequestBody Compte compte) {
//      try {
////          Compte compteToModify = compteRepository.findById(numeroCompte).get();
//          Compte compteToModify = compteRepository.findByNumeroCompteContaining(numeroCompte); // just for test
//          if (compte.getIntitule() != null)
//              compteToModify.setIntitule(compte.getIntitule());
//          if (compte.getSolde() != 0.0)
//              compteToModify.setSolde(compte.getSolde());
//
//          Notification notification = notificationRepository.save(Notification.builder()
//              .client(compte.getClient())
//              .contenu("Le compte de nº <b>" + compte.getNumeroCompteHidden() + "</b> à été modifié par votre agent.")
//              .build()
//          );
//
//          return compteRepository.save(compteToModify);
//      }  catch (NoSuchElementException e) {
//          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le compte avec le numero = " + numeroCompte + " est introuvable.");
//      }
//  }
//
//  @PutMapping(value = "/demandes/{id}/approve")
//  public ResponseEntity<String> approveDemande (@PathVariable("id") Long id) {
//      Demande demande = demandeRepository.findById(id).get();
//      User clientUser = demande.getClient().getUser();
//
//      if (demande.getEmail() != null && !demande.getEmail().equalsIgnoreCase(clientUser.getEmail())) {
//          clientUser.setEmail(demande.getEmail());
//          clientUser.setEmailConfirmed(false);
//          mailService.sendVerificationMail(clientUser);
//      }
//      if (demande.getNom() != null)
//          clientUser.setNom(demande.getNom());
//      if (demande.getPrenom() != null)
//          clientUser.setPrenom(demande.getPrenom());
//
//      userRepository.save(clientUser);
//      demandeRepository.delete(demande);
//
//      Notification notification = Notification.builder()
//          .contenu("Vos données ont été mises à jour. Voir votre profil.")
//          .client(clientUser.getClient())
//          .build();
//
//      notificationRepository.save(notification);
//
//      return new ResponseEntity<>(HttpStatus.OK);
//
//  }
//
//  @GetMapping(value = "/demandes")
//  public List<Demande> getDemandes () {
//
//      return  demandeRepository.findAll();
//
//  }
//
//}
