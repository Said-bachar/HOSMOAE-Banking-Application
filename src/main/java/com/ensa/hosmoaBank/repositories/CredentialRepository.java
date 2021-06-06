package com.ensa.hosmoaBank.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.ensa.hosmoaBank.models.User;
import com.warrenstrange.googleauth.ICredentialRepository;

public class CredentialRepository implements ICredentialRepository{
	  
	@Autowired
    private UserRepository userRepository;

    @Override
    public String getSecretKey(String email) {
        return userRepository.findByEmail(email).getSecretKey();
    }

    @Override
    public void saveUserCredentials(String email,
                                    String secretKey,
                                    int validationCode,
                                    List<Integer> scratchCodes) {
        User u = userRepository.findByEmail(email);
        u.setSecretKey(secretKey);
        userRepository.save(u);
       //usersKeys.put(userName, new User(userName, secretKey, validationCode, scratchCodes));
    }

    public User getUser(String email) {
        return userRepository.findByEmail(email);
    }

	

}
