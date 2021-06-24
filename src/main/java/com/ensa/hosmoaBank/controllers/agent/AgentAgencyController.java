package com.ensa.hosmoaBank.controllers.agent;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ensa.hosmoaBank.models.Agency;
import com.ensa.hosmoaBank.repositories.AgentRepository;

import javax.transaction.Transactional;

@RestController
@RequestMapping("/agent/api/agence")
@Transactional
@CrossOrigin(value = {"https://hosmoa-bank-front.vercel.app"})
public class AgentAgencyController {

    Logger logger = LoggerFactory.getLogger(AgentAgencyController.class);


    @Autowired
    private AgentRepository agentRepository;

    @GetMapping()
    public Agency getAgency() {
        Long id = 1L; //just a test , we will use the token to get agent id
        return agentRepository.findById(id).get().getAgency();
    }
}
