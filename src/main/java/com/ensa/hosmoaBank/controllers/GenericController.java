package com.ensa.hosmoaBank.controllers;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ensa.hosmoaBank.models.User;
import com.ensa.hosmoaBank.repositories.AccountRepository;
import com.ensa.hosmoaBank.repositories.UserRepository;
import com.ensa.hosmoaBank.services.AuthService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class GenericController {  // Used by all users
    
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private AuthService authService;
	
	@PostMapping("/connect")
	public String connect(HttpServletRequest req, Model model) {
		  
		String username = req.getParameter("username");
		System.out.println(username);
		return "/";
	}
	
//	@PostMapping("/api/auth")
//	@ResponseBody
//	public ResponseEntity<?> authenticate(@RequestBody User user) throws Exception{
//		System.out.println(user.getEmail() + " " + user.getPassword());
//		
//		try {
//			this.authService.authenticate(user.getEmail(), user.getPassword());
//		} catch (Exception e) {
//			e.printStackTrace(); // To ...
//		}
//		
//		Map<String, Object> res = new HashMap<>();
//		
//		
//		//User autheticateUser = this.userRepository.findByEmail(user.getEmail());
//		
//		return ResponseEntity.ok(res);
//	}
	
}
