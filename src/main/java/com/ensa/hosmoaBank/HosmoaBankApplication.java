package com.ensa.hosmoaBank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ensa.hosmoaBank.enumerations.AccountStatus;
import com.ensa.hosmoaBank.enumerations.Role;
import com.ensa.hosmoaBank.models.Account;
import com.ensa.hosmoaBank.models.Admin;
import com.ensa.hosmoaBank.models.Agency;
import com.ensa.hosmoaBank.models.Agent;
import com.ensa.hosmoaBank.models.City;
import com.ensa.hosmoaBank.models.Client;
import com.ensa.hosmoaBank.models.Request;
import com.ensa.hosmoaBank.models.User;
import com.ensa.hosmoaBank.repositories.AccountRepository;
import com.ensa.hosmoaBank.repositories.AdminRepository;
import com.ensa.hosmoaBank.repositories.AgencyRepository;
import com.ensa.hosmoaBank.repositories.AgentRepository;
import com.ensa.hosmoaBank.repositories.CityRepository;
import com.ensa.hosmoaBank.repositories.ClientRepository;
import com.ensa.hosmoaBank.repositories.RequestRepository;
import com.ensa.hosmoaBank.repositories.UserRepository;

@SpringBootApplication
public class HosmoaBankApplication implements CommandLineRunner{
	
	Logger logger = LoggerFactory.getLogger(HosmoaBankApplication.class);
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	CityRepository cityRepo;
	
	@Autowired
    AdminRepository adminRepo;
	
	@Autowired
	AgencyRepository agencyRepo;
	
	@Autowired
	AgentRepository agentRepository;
	
	@Autowired
	ClientRepository clientRepository;
	
	@Autowired
	RequestRepository requestRepository;
	
	@Autowired
	AccountRepository  accountRepository;
	
	@Autowired
    private PasswordEncoder encoder;
	
	public static void main(String[] args) {
		
		 ApplicationContext context = SpringApplication.run(HosmoaBankApplication.class, args); 
		 
	}
	
	@Override
	public void run(String... args) throws Exception{
		// TODO Auto-generated method stub
		
//		City city1 = cityRepo.save(City.builder().name("Marrakech").build());
//		City city2 = cityRepo.save(City.builder().name("Kelaa des sraghna").build());
//		
//		System.out.println(city1);
//		System.out.println(city2);
//		
//		User user1 = userRepo.save(User.builder()
//				    .firstName("Said")
//				    .lastName("BACHAR")
//				    .email("said.bachar@gmail.com")
//				    .role(Role.ADMIN)
//				    .password(encoder.encode("saidbachar"))
//			        .emailConfirmed(true)
//				    .phoneNumber("+212762797606")
//				    .adress("Rue Sidi abad 1")
//				    .city("Marrakech")
//				    .build());
//	    
////		Admin admin = adminRepo.save(Admin.builder().user(user1).build());
////		System.out.println(admin);
//		
//		 User user2 = userRepo.save(User.builder()
//	                .lastName("OUMARZOUG")
//	                .firstName("Haitham")
//	                .role(Role.AGENT)
//	                .password(encoder.encode("Haitham"))
//	                .email("oussama@gmail.com")
//	                .phoneNumber("+212123456789")
//	                .city("AGADIR")
//	                .adress("ENSA RUE N 1 ")
//	                //.photo("1.jpg")
//	                .build());
//	        User user3 = userRepo.save(User.builder()
//	                .firstName("Mahacine")
//	                .lastName("Ettahiri")
//	                .password("mahacine")
//	                .role(Role.CLIENT)
//	                .email("mahacine@gmail.com")
//	                .phoneNumber("+212666666666")
//	                .city("MARRAKECH")
//	                .adress("ENSA RUE N 2 ")
//	                //.photo("2.png")
//	                .build());
//	        User user4 = userRepo.save(User.builder()
//	                .firstName("Ouissal")
//	                .lastName("BOUMMAIZ")
//	                .password("ouissal")
//	                .role(Role.CLIENT)
//	                .email("ouissal@gmail.com")
//	                .phoneNumber("+2126111111111")
//	                .city("Marrakech")
//	                .adress("ENSA RUE N 3 ")
//	                .emailConfirmed(true)
//	                .build());
//	        User user5 = userRepo.save(User.builder()
//	                .firstName("Oumaima")
//	                .lastName("BOUFARA")
//	                .password("oumaima")
//	                .role(Role.CLIENT)
//	                .emailConfirmed(true)
//	                .email("pumaima@gmail.com")
//	                .city("MARRAKECH")
//	                .adress("ENSA RUE N 4 ")
//	                .phoneNumber("+2126444444444")
//	                .build());
//	        
//	        User user6 = userRepo.save(User.builder()
//	                .firstName("Ahmed")
//	                .lastName("ABOU HIJAZI")
//	                .password("ahmed")
//	                .role(Role.CLIENT)
//	                .emailConfirmed(true)
//	                .email("ahmed@gmail.com")
//	                .city("MARRAKECH")
//	                .adress("ENSA RUE N 5 ")
//	                .phoneNumber("+212666666666")
//	                .build());
//
//
//	        Admin admin = adminRepo.save(Admin.builder().user(user1).build());
//
//	        Agency agency = agencyRepo.save(Agency.builder().agencyWording("CORONA").city(city2).admin(admin).build());
//
//	        Agent agent = agentRepository.save(Agent.builder().user(user2).agency(agency).build());
//
//
//
//	        Client client = clientRepository.save(Client.builder().user(user3).agency(agency).build());
//	        Client client2 = clientRepository.save(Client.builder().user(user4).agency(agency).build());
//	        Client client3 = clientRepository.save(Client.builder().user(user5).agency(agency).build());
//	        Client client4 = clientRepository.save(Client.builder().user(user6).agency(agency).build());
//
//	        Request demande = requestRepository.save(Request.builder()
//	                .email("haitham@gmail.com")
//	                .nom("oumarzoug")
//	                .client(client)
//	                .prenom("haitham")
//	                .build());
//	        Request demande1 = requestRepository.save(Request.builder()
//	                .email("ahmed@gmail.com")
//	                .nom("abouhijazi")
//	                .client(client)
//	                .prenom("ahmed")
//	                .build());
//	        Request demande2 = requestRepository.save(Request.builder()
//	                .email("ouissal@gmail.com")
//	                .nom("boumaaiz")
//	                .client(client2)
//	                .prenom("ouissal")
//	                .build());
//
//
//	        Account compte1 = accountRepository.save(Account.builder()
//	                .accountBalance(125000.0)
//	                .entitled("haitham")
//	                .client(client)
//	                .statut(AccountStatus.ACTIVE)
//	                .build());
//	        
//	        Account compte2 = accountRepository.save(Account.builder()
//	                .accountBalance(516200.0)
//	                .entitled("said")
//	                .client(client)
//	                .statut(AccountStatus.BLOCKED)
//	                .build());
//	        Account compte3 = accountRepository.save(Account.builder()
//	                .accountBalance(1255840.0)
//	                .entitled("ahmed")
//	                .client(client)
//	                .statut(AccountStatus.ACTIVE)
//	                .build());

//	        Virement virement = virementRepository.save(Virement
//	                .builder()
//	                .compte(compte1)
//	                .montant(2000)
//	                .notes("Take it or leave it")
//	                .destCompte(compte2)
//	                .dateDeVirement(new Date())
//	                .statut(VirementStatus.CONFIRMED)
//	                .build());
//
//	        Recharge recharge = rechargeRepository.save(Recharge
//	                .builder()
//	                .compte(compte1)
//	                .montant(20)
//	                .statut(RechargeStatus.CONFIRMED)
//	                .operateur("MAROC TELECOM")
//	                .numeroTelephone("+21254789654")
//	                .dateDeRecharge(new Date())
//	                .build());



	}
	


}
