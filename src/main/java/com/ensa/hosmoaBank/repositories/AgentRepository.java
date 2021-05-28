package com.ensa.hosmoaBank.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ensa.hosmoaBank.models.Agent;

public interface AgentRepository extends JpaRepository<Agent, Long>{
	
	
}
