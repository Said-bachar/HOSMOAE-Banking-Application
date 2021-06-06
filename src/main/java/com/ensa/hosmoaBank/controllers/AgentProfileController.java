package com.ensa.hosmoaBank.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ensa.hosmoaBank.models.Agent;
import com.ensa.hosmoaBank.repositories.AgentRepository;
import com.ensa.hosmoaBank.services.AuthService;


@RestController
@RequestMapping("/agent/api/profile")
@Transactional
@CrossOrigin(value = "*")
public class AgentProfileController {
	
	@Autowired
	private AgentRepository agentRepository;
	
	@Autowired
	private AuthService authService;
	
	
	  @GetMapping()
	  public Agent getAgent() {
	        return agentRepository.findById(authService.getCurrentUser().getId()).orElseThrow(
	            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agent is not found !")
	        );
	    }

}
