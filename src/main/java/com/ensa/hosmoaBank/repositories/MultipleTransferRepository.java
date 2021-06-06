package com.ensa.hosmoaBank.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import com.ensa.hosmoaBank.models.MultipleTransfer;

@RepositoryRestController
public interface MultipleTransferRepository extends JpaRepository<MultipleTransfer,Long> {
	

}
