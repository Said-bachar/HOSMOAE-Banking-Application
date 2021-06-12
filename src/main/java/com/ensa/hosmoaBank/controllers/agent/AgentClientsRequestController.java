package com.ensa.hosmoaBank.controllers.agent;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ensa.hosmoaBank.models.Notification;
import com.ensa.hosmoaBank.models.Request;
import com.ensa.hosmoaBank.models.User;
import com.ensa.hosmoaBank.repositories.AccountRepository;
import com.ensa.hosmoaBank.repositories.AgencyRepository;
import com.ensa.hosmoaBank.repositories.AgentRepository;
import com.ensa.hosmoaBank.repositories.ClientRepository;
import com.ensa.hosmoaBank.repositories.NotificationRepository;
import com.ensa.hosmoaBank.repositories.RequestRepository;
import com.ensa.hosmoaBank.repositories.UserRepository;
import com.ensa.hosmoaBank.services.MailService;
import com.ensa.hosmoaBank.services.UploadService;

@RestController
@RequestMapping("/agent/api/clients/requests")
@Transactional
@CrossOrigin(value = "*")
public class AgentClientsRequestController {
	
	Logger logger = LoggerFactory.getLogger(AgentClientsRequestController.class);

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

    @PutMapping(value = "/{id}/approve")
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
                .content("Your information has been updated. Check your profile.")
                .client(clientUser.getClient())
                .build();

        notificationRepository.save(notification);

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @GetMapping()
    public List<Request> getRequests () {
        return  requestRepository.findAll();

    }

}
