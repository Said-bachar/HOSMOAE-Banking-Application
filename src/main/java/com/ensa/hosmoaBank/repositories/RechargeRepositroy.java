package com.ensa.hosmoaBank.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import com.ensa.hosmoaBank.models.Recharge;

@RepositoryRestController
public interface RechargeRepositroy extends JpaRepository<Recharge, Long>{

}
