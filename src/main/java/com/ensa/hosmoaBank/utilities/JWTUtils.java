package com.ensa.hosmoaBank.utilities;

import java.util.*;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.ensa.hosmoaBank.models.User;
import com.ensa.hosmoaBank.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;

@Data
@Component
public class JWTUtils {
    
	@Autowired
	private UserRepository userRepository;
	
	private String token = "";
	
	private String CLAIMS_USER = "user";
	
	private Long tokenValidity = 604800L;
	
	@Value("${security.jwt.secret}")
	private String secret = "HOSMOA_BEST_BANK_SECRET_KEY";
	
	//Get username from JWT token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public User getUserFromToken() {
        return new ObjectMapper().convertValue(getClaims(token).get(CLAIMS_USER), User.class);
    }
    
    /*
        /!\ We need to implements same function for Admin, Agent, Admin
     */

    //Get expiration date from JWT token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    //Getting any information from token we will need the secret key
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
    
    
    //Get Claims from token
    public Claims getClaims(String token) {

        Claims claims;
        try {
            claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }
    
    //check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //Generate token for user
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }
    
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        User user = userRepository.findByEmail(subject);
        claims.put(CLAIMS_USER,
            User.builder()
                .email(user.getEmail())
                .id(user.getId())
                .role(user.getRole())
                .username(user.getUsername())
                .build()
        );
        return Jwts.
            builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(generateDateExpiration())
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact();
    }
    
    
    //Generating expiration date :
    private Date generateDateExpiration() {

        return new Date(System.currentTimeMillis() + (tokenValidity * 1000));
    }
    
  //Generating name from Token :
    public String generateNameFromToken(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getSubject();

        } catch (Exception e) {
            return null;
        }
    }

    //Validate a token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Check if a token is valid
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = generateNameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isNotTokenExpired(token));
    }
    
    //Check if token is not expired
    private boolean isNotTokenExpired(String token) {
        Date expiration = getClaims(token).getExpiration();
        return expiration.before(new Date());
    }
	
}
