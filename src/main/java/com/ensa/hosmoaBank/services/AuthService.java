package com.ensa.hosmoaBank.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ensa.hosmoaBank.models.User;
import com.ensa.hosmoaBank.repositories.AgentRepository;
import com.ensa.hosmoaBank.repositories.UserRepository;
import com.ensa.hosmoaBank.utilities.JWTUtils;

import lombok.Data;

@Service
@Data
public class AuthService implements UserDetailsService { //implements UserDetailsService
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AgentRepository agentRepository;
	
	@Autowired
	private JWTUtils jwtUtils;
	
	
	
	@Autowired 
	private AuthenticationManager authenticationManager;
	 

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email);
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.getAuthorities());
	}
	
	
	// Auth :
	
	
	  public void authenticate(String email, String password) { 
		  try {
			  this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));    
		  } catch(DisabledException e) { 
			  e.printStackTrace(); // try to personnalise this
		  } catch(BadCredentialsException e) {
		        e.printStackTrace(); // too }
	      }
	  }
	 
	public User getCurrentUser() {   ///!!\ getCurrent{ADMIN | AGENT | CLIENT}
		
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(principal instanceof UserDetails) {
			return this.userRepository.findByEmail(((UserDetails) principal).getPassword());
		} else {
			return null;
		}
		
		//return jwtUtils.getUserFromToken();
	}
	
//	public User getCurrentAgent() {   ///!!\ getCurrent{ADMIN | AGENT | CLIENT}
//		
//		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		if(principal instanceof UserDetails) {
//			return this.agentRepository.findByEmail(((UserDetails) principal).getPassword());
//		} else {
//			return null;
//		}
//		
//		//return jwtUtils.getUserFromToken();
//	}
	
	

}
