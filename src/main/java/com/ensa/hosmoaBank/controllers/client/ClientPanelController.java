package com.ensa.hosmoaBank.controllers.client;

import com.ensa.hosmoaBank.enumerations.AccountStatus;
import com.ensa.hosmoaBank.enumerations.MultipleTransferStatus;
import com.ensa.hosmoaBank.enumerations.TransferStatus;
import com.ensa.hosmoaBank.models.Account;
import com.ensa.hosmoaBank.models.AccountCredentialsRequest;
import com.ensa.hosmoaBank.models.Beneficiary;
import com.ensa.hosmoaBank.models.ChangePasswordRequest;
import com.ensa.hosmoaBank.models.Client;
import com.ensa.hosmoaBank.models.CodeChangeRequest;
import com.ensa.hosmoaBank.models.CodeValidationRequest;
import com.ensa.hosmoaBank.models.MultipleTransfer;
import com.ensa.hosmoaBank.models.MultipleTransferBeneficiary;
import com.ensa.hosmoaBank.models.MultipleTransferRequest;
import com.ensa.hosmoaBank.models.Notification;
import com.ensa.hosmoaBank.models.Recharge;
import com.ensa.hosmoaBank.models.RechargeRequest;
import com.ensa.hosmoaBank.models.Transfer;
import com.ensa.hosmoaBank.models.TransferRequest;
import com.ensa.hosmoaBank.models.User;
import com.ensa.hosmoaBank.repositories.AccountRepository;
import com.ensa.hosmoaBank.repositories.ClientRepository;
import com.ensa.hosmoaBank.repositories.MultipleTransferRepository;
import com.ensa.hosmoaBank.repositories.NotificationRepository;
import com.ensa.hosmoaBank.repositories.RechargeRepositroy;
import com.ensa.hosmoaBank.repositories.RequestRepository;
import com.ensa.hosmoaBank.repositories.TransferRepository;
import com.ensa.hosmoaBank.repositories.UserRepository;
import com.ensa.hosmoaBank.services.AuthService;
import com.ensa.hosmoaBank.services.MailService;
import com.ensa.hosmoaBank.services.NotificationService;
import com.ensa.hosmoaBank.services.UploadService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import reactor.core.publisher.Flux;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/client/api")
@Log4j2
@RequiredArgsConstructor
public class ClientPanelController {

    Logger logger = LoggerFactory.getLogger(ClientPanelController.class);

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private RechargeRepositroy rechargeRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder encoder;
    
    @Autowired
    private MultipleTransferRepository multipletransferRepository;

    private final GoogleAuthenticator gAuth;


    //    ***************** API Client profil ********************

    @GetMapping(value = "/profil") // return Client by id
    public Client getClient() {
        return clientRepository.findByUser(authService.getCurrentUser()).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Your account is not found.")
        );
    }

    @PostMapping(value = "/profil/change")
    public Client updateProfile(@RequestBody User user) {
        Client client = getClient();

        client.getUser().setEmail(user.getEmail());
        client.getUser().setFirstName(user.getFirstName());
        client.getUser().setLastName(user.getLastName());
        client.getUser().setPhoneNumber(user.getPhoneNumber());
        client.getUser().setAdress(user.getAdress());

        return clientRepository.save(client);
    }

    @SneakyThrows
    @GetMapping("/code/generate")
    public void generate(HttpServletResponse response) {

        String email = getClient().getUser().getEmail();
        final GoogleAuthenticatorKey key = gAuth.createCredentials(email);

        //I've decided to generate QRCode on backend site
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        response.setContentType("image/png");
        response.setHeader("X-QR-CODE", getClient().getUser().getSecretKey());

        String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("Hosmoabank", email, key);

        BitMatrix bitMatrix = qrCodeWriter.encode(otpAuthURL, BarcodeFormat.QR_CODE, 200, 200);

        ServletOutputStream outputStream = response.getOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "png", outputStream);
        outputStream.close();
    }

    @PostMapping("/code/validate")
    public ResponseEntity<String > validateKey(@RequestBody CodeValidationRequest body) {
        User currentClientUser = getClient().getUser();

        if (!gAuth.authorizeUser(currentClientUser.getEmail(), body.getCode()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Code is invalid.");

        currentClientUser.set_2FaEnabled(true);
        userRepository.save(currentClientUser);

        return ResponseEntity.ok("");
    }

    @PostMapping("/change_password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        User currentClientUser = getClient().getUser();
        if (!encoder.matches(request.getOldPassword(), currentClientUser.getPassword()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Old password is incorrect.");
        if (!request.getNewPassword().equals(request.getConfirmedPassword()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Two passwords are not the same.");

        currentClientUser.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(currentClientUser);

        return new ResponseEntity<>(HttpStatus.OK);
    }

//    ***************************************
//    ***************** API Client accounts ********************

    @GetMapping(value = "/accounts")// return listes des accounts d'un client
    public Collection<Account> getClientAccounts() {
        return getClient().getAccounts();
    }

    //    ** API to change code secret ***
    @PostMapping("/accounts/management/changer_code")
    public Account changeCodeSecret(@RequestBody @Valid CodeChangeRequest request) {
        Account account = accountRepository.findByAccountNumberAndClient(request.getAccountNumber(), getClient()).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Account is not found")
        );

        if (!account.getKeySecret().equals(request.getKeySecret()))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Old password is incorrect.");

        if (!request.getNewKeySecret().equals(request.getNewKeySecretConf()))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Two passwors are not the same.");

        account.setKeySecret(request.getNewKeySecret());
        return accountRepository.save(account);

    }

//    ***************************************
    //    ***************** API Client Recharges ********************

    @GetMapping(value = "/recharges")// return listes des recharges  d'un client
    public Collection<Recharge> getAllRecharges() {
        Collection<Account> accounts = getClientAccounts();
        Collection<Recharge> recharges = new ArrayList<>();

        for (Account account : accounts) {
            recharges.addAll(account.getRecharges());
        }

        return recharges;
    }

    //    ***************************************
//    ***************** API to create Recharge ********************

    @PostMapping(value = "/recharges/create")
    public ResponseEntity<Recharge> createRecharge(@RequestBody @Valid RechargeRequest rechargeRequest) {
        Account account = accountRepository.findByAccountNumberAndClient(rechargeRequest.getAccountNumber(), getClient()).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Account number is incorrect.")
        );

        verifyaccountStatus(account);


        if (!account.getKeySecret().equals(rechargeRequest.getKeySecret()))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Code is incorrect.");

        if (account.getAccountBalance() < rechargeRequest.getAmount())
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Your balance is insufficient to perform this operation.");

        account.setAccountBalance(account.getAccountBalance() - rechargeRequest.getAmount());
        account.setLastOperation(new Date());

        Recharge recharge = rechargeRepository.save(Recharge.builder()
            .account(account)
            .montant(rechargeRequest.getAmount())
            .operator(rechargeRequest.getOperator())
            .numberPhone(rechargeRequest.getPhoneNumber())
            .build()
        );

        return new ResponseEntity<>(recharge, HttpStatus.CREATED);
    }

    //    ***************************************
//    ***************** API to create Virement ********************

    @PostMapping(value = "/transfers/create")
    public ResponseEntity<Transfer> createVirement(@RequestBody TransferRequest transferRequest) {
        Account account = accountRepository.findByAccountNumberAndClient(transferRequest.getAcountNumber(), getClient()).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Account number is incorrect.")
        );

        if (!account.getKeySecret().equals(transferRequest.getKeySecret()))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,  "Code is incorrect.");

        verifyaccountStatus(account);

        Account accountDest = accountRepository.findById(transferRequest.getNumeroCompteDest()).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Destination account number is incorrect.")
        );

        if (accountDest == account)
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cannot make the transfer to the same account.");

        if (account.getAccountBalance() < transferRequest.getAmount())
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Your balance is insufficient to perform this operation.");


//            Le solde va etre decrementé lors qu'il confirme le virement.
//            account.setSolde(account.getSolde() - virementRequest.getMontant());
//            accountDest.setSolde(accountDest.getSolde() + virementRequest.getMontant());

        account.setLastOperation(new Date());

        Transfer transfer = transferRepository.save(Transfer.builder()
            .account(account)
            .destAccount(accountDest)
            .montant(transferRequest.getAmount())
            .build()
        );


        // verifier le solde dispo
        if (account.getAccountBalance() < transfer.getMontant())
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Your balance is insufficient to perform this operation.");

        // echange de montant
        account.setAccountBalance(account.getAccountBalance() - transfer.getMontant());
        accountDest.setAccountBalance(accountDest.getAccountBalance() + transfer.getMontant());

        transfer.setStatus(TransferStatus.CONFIRMED);
        transferRepository.save(transfer);


        //mailService.sendVirementCodeMail(getClient().getUser(), transfer);

        // Notification notification = Notification.builder()
        //     .client(account.getClient())
        //     .contenu("Un <b>nouveau virement</b> à été effectué ! Veuillez verifier votre e-mail pour le confirmer.")
        //     .build();

        // notificationRepository.save(notification);

        //return new ResponseEntity<>(transfer, HttpStatus.CREATED);
        return new ResponseEntity<>(transfer, HttpStatus.OK);
    }

    //    ****** API Client Virements *******

    @GetMapping(value = "/transfers")// return listes des virements  d'un client
    public Collection<Transfer> getAllVirements() {
        Collection<Account> accounts = getClientAccounts();
        Collection<Transfer> transfers = new ArrayList<>();

        for (Account account : accounts) {
        	transfers.addAll(account.getTransfers());
        }

        return transfers;

    }

    //    ************************

    //    ****** API  Virement confirmation *******

    @PostMapping(value = "/transfers/{id}/confirm")
    public ResponseEntity<String> virementConfirmation(@PathVariable(value = "id") Long id, @RequestBody HashMap<String, Integer> request) {
        Transfer transfer = transferRepository.findByIdAndAndAccount_Client(id, getClient()).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transfer with id = " + id + " is not found .")
        );
        // verifier si le virement est confirmé
        if (transfer.getStatus().name().equals(TransferStatus.CONFIRMED.name()) || transfer.getStatus().name().equals(TransferStatus.RECEIVED.name())) {
            logger.info("Transfer status : {}", transfer.getStatus().name());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This transfer is already confirmed.");
        }

        // verifier le code de verification
        int codeVerification = Math.toIntExact(request.get("codeVerification"));
        if (codeVerification != transfer.getCodeVerification())
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Code is invalid.");


        Account account = transfer.getAccount();
        Account destaccount = transfer.getDestAccount();

        // verifier le solde dispo
        if (account.getAccountBalance() < transfer.getMontant())
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Your balance is insufficient to perform this operation.");

        // echange de montant
        account.setAccountBalance(account.getAccountBalance() - transfer.getMontant());
        destaccount.setAccountBalance(destaccount.getAccountBalance() + transfer.getMontant());

        transfer.setStatus(TransferStatus.CONFIRMED);
        transferRepository.save(transfer);


        return new ResponseEntity<>("Your transfer has been confirmed !", HttpStatus.OK);
    }

    //***************************
    //******** API Verify account number **************
    @PostMapping(value = "/verify_number")
    public ResponseEntity<String> verifyaccountNumber(@RequestBody AccountCredentialsRequest request) {
        Account account = accountRepository.findByAccountNumberAndClient(request.getAccountNumber(), getClient()).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Le nº de account est erroné.")
        );
        if (!account.getKeySecret().equals(request.getKeySecret()))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, " Code is incorrect.");

        verifyaccountStatus(account);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //**********************
    //    ****** API  Block account *******
    @PutMapping(value = "/accounts/block")
    public ResponseEntity<Account> accountBlock(@RequestBody Account request) {
        Account account = accountRepository.findByAccountNumberAndClient(request.getAccountNumber(), getClient()).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "The account number is wrong.")
        );
        if (account.getStatut().name().equals(AccountStatus.BLOCKED.name()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account is already blocked.");
        if (!account.getKeySecret().equals(request.getKeySecret()))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "The code is incorrect.");

        account.setStatut(AccountStatus.PENDING_BLOCKED);
        account.setRaison(request.getRaison());

        accountRepository.save(account);

        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    //***************************
//    ****** API  Suspend account *******

    @PutMapping(value = "/accounts/suspend")
    public ResponseEntity<Account> accountSuspend(@RequestBody Account request) {
        Account account = accountRepository.findByAccountNumberAndClient(request.getAccountNumber(), getClient()).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Account number is incorrect.")
        );

        if (account.getStatut().name().equals(AccountStatus.BLOCKED))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account is blocked .");
        if (!account.getKeySecret().equals(request.getKeySecret()))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Code is incorrect.");

        account.setStatut(AccountStatus.PENDING_SUSPENDED);
        account.setRaison(request.getRaison());

        accountRepository.save(account);

        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @DeleteMapping("/transfers/{id}/delete")
    public ResponseEntity<String> deleteVirement(@PathVariable("id") Long id) {
        Transfer transfer = transferRepository.findByIdAndAndAccount_Client(id, getClient()).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transfer with id= " + id + " is not found.")
        );
        logger.info("Virement status : {}", transfer.getStatus().name());
        transferRepository.delete(transfer);
        return new ResponseEntity<>("Votre virement a été bien supprimé !", HttpStatus.OK);
    }

    @GetMapping(path = "/notifications")
    public Collection<Notification> getAllNotifications() {
        return getClient().getNotifications();
    }

    // api to subscribe to notifications event stream via SSE
    @GetMapping(path = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Notification> receive() {
        return Flux.create(sink -> notificationService.subscribe(sink::next));
    }

    @PostMapping(path = "/notification")
    public String sendNotification(@RequestBody Notification notification) {
        logger.info("NOTIF = {}", notification.getContent());

        // we set notification reciever to current client.
        notification.setClient(getClient());
        notificationService.publish(notification);
        return "OK";
    }

    // ******** this api to mark all notifications as seen *******
    @PutMapping("/notifications/mark_seen")
    public ResponseEntity<String> markNotificationsSeen() {
        Collection<Notification> notifications = getClient().getNotifications()
            .stream()
            .map(notification -> {
                notification.setReaded(true);
                return notification;
            })
            .collect(Collectors.toList());

        notificationRepository.saveAll(notifications);

        return ResponseEntity.ok().body("OK");
    }

    // helper function to check if account is active.
    public void verifyaccountStatus(Account account) {
        if (!account.getStatut().name().equals(AccountStatus.ACTIVE.name()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ce account n'est pas actif.");
    }

    //***************************
//    ****** API to upload avatar image *******

    @PostMapping("/avatar/upload")
//    @CacheEvict(cacheNames = "clients", allEntries = true)
    public ResponseEntity<?> uploadAvatar(@RequestParam("image") MultipartFile image) {
        Client client = getClient();

        String fileName = uploadService.store(image);

        // generate download link
        String imageDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/client/api/")
            .path("/avatar/")
            .path(fileName)
            .toUriString();
        System.out.println(imageDownloadUri);

        // check if client has already uploaded an image
        if (client.getUser().getPicture() != null)
            uploadService.delete(client.getUser().getPicture());

        client.getUser().setPicture(fileName);
        clientRepository.save(client);

        Map<String, String> response = new HashMap<>();
        response.put("link", fileName);

        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    //***************************
//    ****** API to get avatar image *******
    @GetMapping("/avatar/{filename}")
    public ResponseEntity<Resource> getAvatar(HttpServletRequest request, @PathVariable("filename") String filename) {
        Client client = getClient();
//        System.out.println(filename);

        if (client.getUser().getPicture() == null)
            throw new ResponseStatusException(HttpStatus.OK, "Pas de photo définie.");

        Resource resource = uploadService.get(client.getUser().getPicture());

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
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }

    //***************************
//    ****** API to get avatar image *******
    @DeleteMapping("/avatar/delete")
//    @CacheEvict(cacheNames = "clients", allEntries = true)
    public ResponseEntity<String> deleteAvatar() {
        Client client = getClient();

        // check if client has already uploaded an image
        if (client.getUser().getPicture() != null)
            uploadService.delete(client.getUser().getPicture());
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        client.getUser().setPicture(null);
        clientRepository.save(client);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    
    //    ****** API Client MultipleTransfers *******

    @GetMapping(value = "/multipletransfers")
    public Collection<MultipleTransfer> getAllMultipleTransfers() {
        Collection<Account> accounts = getClientAccounts();
        Collection<MultipleTransfer> multipletransfers = new ArrayList<>();

        for (Account account : accounts) {
        	multipletransfers.addAll(account.getMultipletransfers());
        }

        return multipletransfers;
    }
    
//  ***************** API to get Clients Beneficiaries ********************

  @PostMapping(value = "/multipletransfers/getBeneficiaries")
  public Collection<Beneficiary> getBeneficiaries(){
	  
	Collection<Beneficiary> beneficiaries=new ArrayList<>();
	
	Collection<Account> accounts = getClientAccounts();
	
	Collection<MultipleTransfer> multipletransfers = new ArrayList<>();
    for (Account account : accounts) {
    	multipletransfers.addAll(account.getMultipletransfers());
    }
    
    Collection<MultipleTransferBeneficiary> multipletransferbeneficiaries = new ArrayList<>();
    for(MultipleTransfer m:multipletransfers) {
    	multipletransferbeneficiaries.addAll(m.getMultipletransferbeneficiary());
    }
    
    for(MultipleTransferBeneficiary m:multipletransferbeneficiaries) {
    	beneficiaries.add(m.getBeneficiary());
    }
	return beneficiaries;
  }
  
  	//***************** API to add Beneficiary ********************

	@PostMapping(value = "/multipletransfers/addBeneficiary")
	public String addBeneficiary(@RequestBody MultipleTransferBeneficiary multipletransferbeneficiary){
		return "redirect:/client/api/multipletransfer/create";
	}
	
	//***************** API to create Multiple Transfer ********************
	@PostMapping(value = "/multipletransfers/create")
    public ResponseEntity<MultipleTransfer> createMultipleTransfer(@RequestBody MultipleTransferRequest multipletransferRequest) {
        Account account = accountRepository.findByAccountNumberAndClient(multipletransferRequest.getAcountNumber(), getClient()).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Account number is incorrect.")
        );

        if (!account.getKeySecret().equals(multipletransferRequest.getSecretKey()))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,  "Code is incorrect.");

        verifyaccountStatus(account);

        Collection<MultipleTransferBeneficiary> mbeneficiaries=multipletransferRequest.getMultipletransfersbeneficiaries();
        float globalAmount=0;
        for(MultipleTransferBeneficiary m:mbeneficiaries) {
        	globalAmount+=m.getAmount();
        }
        if (account.getAccountBalance() < globalAmount)
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Your balance is insufficient to perform this operation.");


        account.setLastOperation(new Date());

        MultipleTransfer multipletransfer = multipletransferRepository.save(MultipleTransfer.builder()
            .account(account)
            .multipletransferbeneficiary(multipletransferRequest.getMultipletransfersbeneficiaries())
            .build()
        );

        mailService.sendMultipleTransferCodeMail(getClient().getUser(), multipletransfer);

        return new ResponseEntity<>(multipletransfer, HttpStatus.CREATED);
    }
	
	//***************** API to confirm Multiple Transfer ********************
	
	@PostMapping(value = "/multipletransfers/{id}/confirm")
    public ResponseEntity<String> MultipleTransferConfirmation(@PathVariable(value = "id") Long id, @RequestBody HashMap<String, Integer> request) {
        MultipleTransfer multipletransfer = multipletransferRepository.findByIdAndAndAccount_Client(id, getClient()).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transfer with id = " + id + " is not found .")
        );
        // verify if multiple transfer is confirmed
        if (multipletransfer.getStatus().name().equals(MultipleTransferStatus.CONFIRMED.name()) || multipletransfer.getStatus().name().equals(MultipleTransferStatus.RECEIVED.name())) {
            logger.info("Multiple Transfer status : {}", multipletransfer.getStatus().name());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This transfer is already confirmed.");
        }

        // verifier le code de verification
        int codeVerification = Math.toIntExact(request.get("codeVerification"));
        if (codeVerification != multipletransfer.getCodeVerification())
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Code is invalid.");


        Account account = multipletransfer.getAccount();
        
        Collection<MultipleTransferBeneficiary> mbeneficiaries=multipletransfer.getMultipletransferbeneficiary();
        float globalAmount=0;
        for(MultipleTransferBeneficiary m:mbeneficiaries) {
        	globalAmount+=m.getAmount();
        }

        // check balance
        if (account.getAccountBalance() < globalAmount)
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Your balance is insufficient to perform this operation.");

        // echange de montant
        account.setAccountBalance(account.getAccountBalance() - globalAmount);
        for(MultipleTransferBeneficiary m:mbeneficiaries) {
        	Account accountDest = accountRepository.findByAccountNumber(m.getBeneficiary().getAccountNumber());
        	accountDest.setAccountBalance(accountDest.getAccountBalance() + m.getAmount());
        }

        multipletransfer.setStatus(MultipleTransferStatus.CONFIRMED);
        multipletransferRepository.save(multipletransfer);


        return new ResponseEntity<>("Your multiple transfer has been confirmed !", HttpStatus.OK);
    }
}
