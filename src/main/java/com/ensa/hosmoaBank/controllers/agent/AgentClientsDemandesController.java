package com.ensa.hosmoaBank.controllers.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ensa.hosmoaBank.models.Notification;
import com.ensa.hosmoaBank.models.Request;
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

import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping("/agent/api/clients/demandes")
@Transactional
@CrossOrigin(value = "*")
public class AgentClientsDemandesController {

    Logger logger = LoggerFactory.getLogger(AgentClientsDemandesController.class);

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

    @PutMapping(value = "/{id}/approve")
    public ResponseEntity<String> approveDemande (@PathVariable("id") Long id) {
        Request request = demandeRepository.findById(id).get();
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
        demandeRepository.delete(request);

        Notification notification = Notification.builder()
                .content("Your data are updated with success. See your profil.")
                .client(clientUser.getClient())
                .build();

        notificationRepository.save(notification);

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @GetMapping()
    public List<Request> getDemandes () {
        return  demandeRepository.findAll();

    }
}
