package com.ensa.hosmoaBank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.ensa.hosmoaBank.enumerations.Role;
import com.ensa.hosmoaBank.models.User;
import com.ensa.hosmoaBank.repositories.UserRepository;

@SpringBootApplication
public class HosmoaBankApplication implements CommandLineRunner{
	
	Logger logger = LoggerFactory.getLogger(HosmoaBankApplication.class);
	
	@Autowired
	UserRepository userRepo;
    
	
	public static void main(String[] args) {
		
		 ApplicationContext ctx = SpringApplication.run(HosmoaBankApplication.class, args); 
		 
	}
	
	@Override
	public void run(String... args) throws Exception{
		// TODO Auto-generated method stub
		
//		User user = userRepo.save(User.builder()
//				             .username("BACHAR Said")
//				             .password("147852")
//				             .email("said@gmail.com")
//				             .role(Role.CLIENT)
//				             .build()
//			);
//		System.out.println(user);
//		 System.out.println("........................................... ==== Let's Start ==== .....................................");
//		System.out.println(userRepo.findByEmail("said@gmail.com"));
//		System.out.println(userRepo.de);

	}
	
	

}
