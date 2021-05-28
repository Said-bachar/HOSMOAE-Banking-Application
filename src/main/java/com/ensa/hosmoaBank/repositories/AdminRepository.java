package com.ensa.hosmoaBank.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ensa.hosmoaBank.models.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {

}
