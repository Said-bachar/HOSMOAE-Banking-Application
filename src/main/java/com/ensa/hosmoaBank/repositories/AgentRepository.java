package com.ensa.hosmoaBank.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import com.ensa.hosmoaBank.models.Agent;
import com.ensa.hosmoaBank.models.User;

@RepositoryRestController
public interface AgentRepository extends JpaRepository<Agent, Long>{
	
	//Get Agent by user
	Optional<Agent> findByUser(User user);
	
	
}
