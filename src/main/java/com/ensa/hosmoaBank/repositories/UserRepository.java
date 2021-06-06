package com.ensa.hosmoaBank.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import com.ensa.hosmoaBank.enumerations.Role;
import com.ensa.hosmoaBank.models.User;

@RepositoryRestController
public interface UserRepository extends JpaRepository<User, Long>{
	
	List<User> findAllByRoleIsNotOrderByDateDeCreationDesc(Role role);

    List<User> findAllByRole(Role role);

    User findUserByRoleAndId(Role role , Long id);

    User deleteByIdAndRole(Long id , Role role);

    //User findUserByNomOrPrenom(String lastName);

    User findOneByVerificationToken(String token);

    List<User> findUserByRoleAndLastName(Role role, String clientName);

    //Optional<User> findByEmail(String email);

    User findByEmail(String email);

    List<User> findAllByRoleAndArchived(Role role , Boolean archived);

}
