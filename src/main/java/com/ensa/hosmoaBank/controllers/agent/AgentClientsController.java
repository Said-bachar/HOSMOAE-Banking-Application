package com.ensa.hosmoaBank.controllers.agent;


import java.util.HashMap;
import java.util.NoSuchElementException;

import javax.persistence.EntityNotFoundException;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


import com.ensa.hosmoaBank.enumerations.Role;
import com.ensa.hosmoaBank.models.Agent;
import com.ensa.hosmoaBank.models.ChangeClientDataRequest;
import com.ensa.hosmoaBank.models.Client;
import com.ensa.hosmoaBank.models.User;
import com.ensa.hosmoaBank.repositories.AccountRepository;
import com.ensa.hosmoaBank.repositories.AgencyRepository;
import com.ensa.hosmoaBank.repositories.AgentRepository;
import com.ensa.hosmoaBank.repositories.ClientRepository;
import com.ensa.hosmoaBank.repositories.NotificationRepositroty;
import com.ensa.hosmoaBank.repositories.RequestRepository;
import com.ensa.hosmoaBank.repositories.UserRepository;
import com.ensa.hosmoaBank.services.MailService;
import com.ensa.hosmoaBank.services.UploadService;
import com.ensa.hosmoaBank.utilities.VerificationTokenGenerator;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;

import java.util.List;


@RestController
@RequestMapping("/agent/api/clients")
@Transactional
@CrossOrigin(value = "*")
public class AgentClientsController {

    Logger logger = LoggerFactory.getLogger(AgentClientsController.class);

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
    private RequestRepository demandeRepository;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private MailService mailService;

    @Autowired
    private AgentProfileController agentProfileController;

    @Autowired
    private PasswordEncoder encoder;


    @GetMapping() //show all clients 
    public List<User> getClients(){
        return userRepository.findAllByRoleAndArchived(Role.CLIENT,false);
    }



    @GetMapping(value="/{id}")
    public Client getOneClient(@PathVariable(value = "id")Long id) {
        try{
            return userRepository.findById(id).get().getClient();
        } catch (NoSuchElementException | EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client with the id = " + id + " is not found !")  ;
        }
    }

    @GetMapping(value="/search/{name}")
    public List<User> getClientByName(@PathVariable(value = "name") String clientName) {
        try{
            return userRepository.findUserByRoleAndLastName(Role.CLIENT, clientName);
        } catch (NoSuchElementException | EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client with the id = " + clientName + " is not found !")  ;
        }
    }


    @PostMapping("/add") //add new client 
    public User addClient(@RequestBody User user) {
        try {
            user.setRole(Role.CLIENT);
            Agent agent = agentProfileController.getAgent();

            userRepository.save(user);

            Client client = Client.builder()
                    .agency(agent.getAgency())
                    .user(user)
                    .build();

            clientRepository.save(client);
            mailService.sendVerificationMail(user);
            return user;
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This mail is already used."+e.toString())  ;
        }
    }

    @DeleteMapping(value = "/{id}/delete")           // delete a client 
    public ResponseEntity<?> deleteClient(@PathVariable(value = "id") Long id){
        try {
            Client client = userRepository.findById(id).get().getClient();
			//userRepository.delete(client.getUser());
			//clientRepository.delete(client);
			//compteRepository.deleteAll(client.getComptes());
            client.getUser().setArchived(true);

            return new ResponseEntity<>("Client is deleted with success." ,HttpStatus.OK);
        } catch (NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client with id = " + id + " is not found.");
        }
    }


    @PutMapping(value = "/{id}/update")                    // modify client 
    public User modifyClient(@PathVariable(value = "id") Long id ,
                             @RequestBody ChangeClientDataRequest changeClientDataRequest) throws UnirestException {
        logger.info("CLIENT ID = " + id);

        try {
            Agent agent = agentProfileController.getAgent();
            User user = changeClientDataRequest.getUser();
            User userToModify = clientRepository.findById(id).get().getUser();
            Boolean isMatch = encoder.matches(changeClientDataRequest.getAgentPassword(),agent.getUser().getPassword());
            if(!isMatch){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Wrong password !.");
            }
            user.setVerificationToken(userToModify.getVerificationToken());
            // renvoyer le code de confirmation pr verifier le nouveau email
            if (user.getEmail() != null && !user.getEmail().equals(userToModify.getEmail())) {
                System.out.println("The new email has been saved");
                userToModify.setEmailConfirmed(false);
                mailService.sendVerificationMail(user);
            }
            if (user.getEmail() != null)
                userToModify.setEmail(user.getEmail());
            if (user.getLastName() != null)
                userToModify.setLastName(user.getLastName());
            if (user.getFirstName() != null)
                userToModify.setFirstName(user.getFirstName());
            if (user.getPhoneNumber() != null)
                userToModify.setPhoneNumber(user.getPhoneNumber());
            if(user.getCity() != null){
                userToModify.setCity(user.getCity());
            }
            if(user.getAdress() != null){
                userToModify.setAdress(user.getAdress());
            }

            return userRepository.save(userToModify);
        }
            catch (NoSuchElementException e){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client with id = " + id + " is not found.");
            }
    }

    @PutMapping(value = "/{id}/update/contact") // 
    public User modifyClient(@PathVariable(value = "id") Long id , @RequestBody User user) throws UnirestException {
        logger.info("CLIENT ID = " + id);

        try {
            User userToModify = clientRepository.findById(id).get().getUser();
            if(user.getCity() != null){
                userToModify.setCity(user.getCity());
            }
            if(user.getAdress() != null){
                userToModify.setAdress(user.getAdress());
            }

            return userRepository.save(userToModify);
        }
        catch (NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client with id = " + id + " is not found.");
        }
    }

    @PostMapping(value="/{id}/verification")
    public ResponseEntity sendClientVerification(@PathVariable(value = "id") Long  id ,
                                                    @RequestBody ChangeClientDataRequest changeClientDataRequest) {
        System.out.println(changeClientDataRequest);
        HashMap<String, String> map = new HashMap<>();
        try{
            if(!agentProfileController.getAgent().getUser().getPassword().equals(changeClientDataRequest.getAgentPassword())){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Wrong password!");
            }
            User user = getOneClient(id).getUser();
            user.setPassword(null);
            user.setVerificationToken(VerificationTokenGenerator.generateVerificationToken());
            userRepository.save(user);
            map.put("text","La vérification a été envoyée avec succès.");

            mailService.sendVerificationMail(user);
            return new ResponseEntity(map,HttpStatus.OK);
        } catch (NoSuchElementException | EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client with id = " + id + " is not found.");
        }
    }

    @GetMapping("/avatar/{filename}")
    public ResponseEntity<Resource> getCLientAvatar(HttpServletRequest request, @PathVariable("filename") String filename) {
//        Client client = getAgent().getAgence().getClients();
        System.out.println(filename);

//        if (agent.getUser().getPhoto() == null )
//            throw new ResponseStatusException(HttpStatus.OK, "Pas de photo définie.");


        Resource resource = uploadService.get(filename);

        // setting content-type header
        String contentType = null;
        try {
            // setting content-type header according to file type
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Type indéfini.");
        }
        // setting content-type header to generic octet-stream
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachmen; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}