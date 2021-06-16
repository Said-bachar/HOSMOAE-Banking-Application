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
    
	@Value("${security.jwt.secret}")
    private String secret = "HOSMOABANK_SECRET_KEY";

    @Autowired
    private UserRepository userRepository;

    private long TokenValidity = 604800L;

    private String token = "";


    private String CLAIMS_USER = "user";

    //retrieve username from jwt token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public User getUserFromToken() {
        return new ObjectMapper().convertValue(getClaims(token).get(CLAIMS_USER), User.class);
    }

    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    //for retrieveing any information from token we will need the secret key
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    //check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //generate token for user
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
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .picture(user.getPicture())
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

    private Date generateDateExpiration() {

        return new Date(System.currentTimeMillis() + (TokenValidity * 1000));
    }

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

    public String generateNameFromToken(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getSubject();

        } catch (Exception e) {
            return null;
        }
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = generateNameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isNotTokenExpired(token));
    }

    private boolean isNotTokenExpired(String token) {
        Date expiration = getClaims(token).getExpiration();
        return expiration.before(new Date());
    }
	
}
