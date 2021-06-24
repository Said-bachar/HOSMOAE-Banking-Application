package com.ensa.hosmoaBank.controllers.agent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ensa.hosmoaBank.models.Agent;
import com.ensa.hosmoaBank.models.ChangePasswordRequest;
import com.ensa.hosmoaBank.models.User;
import com.ensa.hosmoaBank.repositories.AccountRepository;
import com.ensa.hosmoaBank.repositories.AgencyRepository;
import com.ensa.hosmoaBank.repositories.AgentRepository;
import com.ensa.hosmoaBank.repositories.ClientRepository;
import com.ensa.hosmoaBank.repositories.NotificationRepository;
import com.ensa.hosmoaBank.repositories.RequestRepository;
import com.ensa.hosmoaBank.repositories.UserRepository;
import com.ensa.hosmoaBank.services.AuthService;
import com.ensa.hosmoaBank.services.MailService;
import com.ensa.hosmoaBank.services.UploadService;


@RestController
@RequestMapping("/agent/api/profile")
@Transactional
@CrossOrigin(value = "*")
public class AgentProfileController {

	Logger logger = LoggerFactory.getLogger(AgentProfileController.class);

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepository compteRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private MailService mailService;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder encoder;
    
    @GetMapping()
    public Agent getAgent() {
        return agentRepository.findByUser(authService.getCurrentUser()).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "agent not found.")
        );
    }
    
    @PostMapping("/avatar/upload")
    @CacheEvict(cacheNames = "clients", allEntries = true)
    public ResponseEntity<?> uploadAvatar(@RequestParam("image") MultipartFile image) {
        System.out.println(image);
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
    
    @PostMapping("/modifier/password")
    public ResponseEntity changePassword(@RequestBody ChangePasswordRequest passwordChangeRequest) {
        System.out.println(passwordChangeRequest);
        Agent currentAgent = getAgent();
        Boolean isMatch = encoder.matches(passwordChangeRequest.getOldPassword(), currentAgent.getUser().getPassword());

        if (!passwordChangeRequest.getNewPassword().equals(passwordChangeRequest.getConfirmedPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your password confirmation doesn't match.");
        }

        if (!isMatch) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong password.");
        }

        currentAgent.getUser().setPassword(encoder.encode(passwordChangeRequest.getNewPassword()));
        agentRepository.save(currentAgent);


        return new ResponseEntity(HttpStatus.OK);


    }
    
    @PostMapping("/modifier/user")
    public User modifyAgentParam(@RequestBody User user) {
        System.out.println(user);
        User oldUser = getAgent().getUser();
        user.setPassword(oldUser.getPassword());
        user.setVerificationToken(oldUser.getVerificationToken());
        user.setAgent(getAgent());
        userRepository.save(user);

        return user;
    }
    
    @GetMapping("/avatar/{filename}")
    public ResponseEntity<Resource> getAvatar(HttpServletRequest request, @PathVariable("filename") String filename) {
        Agent agent = getAgent();
        System.out.println(filename);
        Resource resource = uploadService.get(agent.getUser().getPicture());
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } 
        catch (IOException e) {
            System.out.println("Indefinite type.");
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }

    @DeleteMapping("/avatar/delete/{filename}")
    public ResponseEntity deleteAgentPhoto(@PathVariable("filename") String filename) {
        uploadService.delete(filename);
        getAgent().getUser().setPicture(null);
        agentRepository.save(getAgent());
        return new ResponseEntity(HttpStatus.OK);
    }
    
}

