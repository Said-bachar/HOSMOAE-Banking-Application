package com.ensa.hosmoaBank.controllers.agent;

import java.util.Collection;
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

import com.ensa.hosmoaBank.controllers.AgentProfileController;
import com.ensa.hosmoaBank.models.Agent;
import com.ensa.hosmoaBank.models.ChangeClientDataRequest;
import com.ensa.hosmoaBank.models.Client;
import com.ensa.hosmoaBank.models.User;
import com.ensa.hosmoaBank.repositories.AccountRepository;
import com.ensa.hosmoaBank.repositories.AgentRepository;
import com.ensa.hosmoaBank.repositories.ClientRepository;
import com.ensa.hosmoaBank.repositories.UserRepository;

@RestController
@RequestMapping("/agent/api/clients")
@CrossOrigin(value = "*")
public class AgentClientsController {
       
	Logger logger = LoggerFactory.getLogger(AgentClientsController.class);

//    @Autowired
//    private AgentRepository agentRepository;
    
    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private UserRepository userRepositroy;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private AgentProfileController agentPorfileController;
    
    @Autowired
    private PasswordEncoder encoder;
    
    @GetMapping()
    public Collection<Client> getClients() {
    	Agent agent = new Agent(); // Cange to AgentProfilController.getAgent()
    	return this.clientRepository.findByAgent(agent);
    }
    
    @GetMapping(value = "/{id}")
    public Client getClient(@PathVariable(value = "id") Long id) {
    	try {
    		
    		return this.clientRepository.findById(id).get();
    		
    	} catch(NoSuchElementException | EntityNotFoundException e) {
    		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client with id = " + id + "is not found" );
    	}
    }
    
    @GetMapping(value = "/search/{name}")
    public Collection<Client> getClientByName(@PathVariable(value = "name") String clientName) { 
    	try {
    		return clientRepository.findByLastName(clientName);
    	} catch(NoSuchElementException | EntityNotFoundException e) {
    		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client with name = " + clientName + "is not found" );
    	}
    }
    /*
     * POSTs Requests................................................
     */
    
    @PostMapping("/add")
    public Client addClient(@RequestBody Client client) {
    try {
    	Agent agent = new Agent(); // = agentProfileController.getAgent()
    	client = Client.builder()
    			              .agent(agent)
    			              .build();
    	this.clientRepository.save(client);
    	return client;
    } catch(DataIntegrityViolationException e) {
    	throw new ResponseStatusException(HttpStatus.CONFLICT, "This mail is already used !." + e.toString() );
    	
    }
    
}
    /*
     * DELETE Requests...........................
     */
    
    @DeleteMapping(value = "/delete/{id}") // delete a client 
    public ResponseEntity<?> deleteClient(@PathVariable(value = "id") Long id){
        try {
            Client client =clientRepository.findById(id).get();
//            userRepository.delete(client.getUser());
              clientRepository.delete(client);
              accountRepository.deleteAll(client.getAccounts());
            //client.setArchived(true);

            return new ResponseEntity<>("Client is deleted with success ." ,HttpStatus.OK);
        } catch (NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client with id = " + id + " is not found !");
        }
    }
    
    @PutMapping(value = "/update/{id}")
    public Client updateClient(@PathVariable(value = "id") Long id, @RequestBody ChangeClientDataRequest changeClientDataRequest) { // Add throws exception here::
    	logger.info("CLINET ID : " + id);
        try {
        	Agent agent           = new Agent(); // .getAgent()
        	Client client         = changeClientDataRequest.getClient();
        	Client clientToUpdate = clientRepository.findById(id).get();
        	Boolean isMatch       = this.encoder.matches(changeClientDataRequest.getAgentPassword(), agent.getPassword());
        	
        	if(!isMatch) {
        		throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid password !");
        	}
        	
        	client.setVerificationToken(clientToUpdate.getVerificationToken());
        	
        	 if (client.getEmail() != null && !client.getEmail().equals(clientToUpdate.getEmail())) {
                 System.out.println("New email has been saved");
                 clientToUpdate.setEmailConfirmed(false);
                 //mailService.sendVerificationMail(user);
             }
             if (client.getEmail() != null)
            	 clientToUpdate.setEmail(client.getEmail());
             if (client.getLastName() != null)
            	 clientToUpdate.setLastName(client.getLastName());
             if (client.getFirstName() != null)
            	 clientToUpdate.setFirstName(client.getFirstName());
//             if (client.getNumeroTelephone() != null)
//            	 clientToUpdate.setNumeroTelephone(client.getNumeroTelephone());
             if(client.getCity() != null){
            	 clientToUpdate.setCity(client.getCity());
             }
             if(client.getAdress() != null){
            	 clientToUpdate.setAdress(client.getAdress());
             }
             
             return clientRepository.save(clientToUpdate);
        	
        }catch(NoSuchElementException e) {
        	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client with id = " + id + "est not found !");
        }    	
    	
    }
    
    @PutMapping(value = "verification/{id}")
    public ResponseEntity senClientVerification (@PathVariable(value = "id") Long id, @RequestBody ChangeClientDataRequest changeClientDataRequest) {
    	 
    	System.out.println(changeClientDataRequest);
    	 HashMap<String, String> map = new HashMap<>();
    	 
    	 try {
    		    if(!agentPorfileController.getAgent().getPassword().equals(changeClientDataRequest.getAgentPassword())) 
    		       {
    			      throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Password is invalid !.");
    		       }
    		    Client client = getClient(id);
    		    client.setPassword(null);
    		    client.setVerificationToken(null); // null ==> Generator of token here
    		    clientRepository.save(client);
    		    map.put("Message", "Verification is sended with success !");
    	        
    		     return new ResponseEntity(map, HttpStatus.OK);
    	   
    	   } 
    	 
    	 catch(NoSuchElementException | EntityNotFoundException e) {
    		 throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client with id = " + id + "is not found !");
    	 }
    	 
    	 
//    	 @GetMapping("/avatar/{filename}")
//    	    public ResponseEntity<Resource> getCLientAvatar(HttpServletRequest request, @PathVariable("filename") String filename) {
////    	        Client client = getAgent().getAgence().getClients();
//    	        System.out.println(filename);
//
////    	        if (agent.getUser().getPhoto() == null )
////    	            throw new ResponseStatusException(HttpStatus.OK, "Pas de photo définie.");
//
//
//    	        Resource resource = uploadService.get(filename);
//
//    	        // setting content-type header
//    	        String contentType = null;
//    	        try {
//    	            // setting content-type header according to file type
//    	            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
//    	        } catch (IOException e) {
//    	            System.out.println("Type indéfini.");
//    	        }
//    	        // setting content-type header to generic octet-stream
//    	        if (contentType == null) {
//    	            contentType = "application/octet-stream";
//    	        }
//
//    	        return ResponseEntity.ok()
//    	                .contentType(MediaType.parseMediaType(contentType))
//    	                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//    	                .body(resource);
//    	    }

    	
		
        
    
  }
    
}