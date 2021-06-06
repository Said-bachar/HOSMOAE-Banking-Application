package com.ensa.hosmoaBank;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ensa.hosmoaBank.repositories.UserRepository;
import com.ensa.hosmoaBank.services.AuthService;
import com.ensa.hosmoaBank.utilities.JWTUtils;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class JWTFilter extends OncePerRequestFilter{
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private JWTUtils jWTUtils;
	
	@Autowired
	private UserRepository userRepository;
	
	private String TOKEN_HEADER = "Authorization";

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		 final String requestTokenHeader = req.getHeader(TOKEN_HEADER);
//       System.out.println(httpServletRequest.getParameter("username"));
//       System.out.println(httpServletRequest.getRequestURI());
//       User user = userRepository.findByEmail(httpServletRequest.getParameter("username"));
//       System.out.println(user.getRole());


       String username = null;
       String jwtToken = null;


       if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
           jwtToken = requestTokenHeader.substring(7);
           try {
               username = jWTUtils.getUsernameFromToken(jwtToken);

           } catch (IllegalArgumentException e) {
               System.out.println("Unable to get JWT Token");
           } catch (ExpiredJwtException e) {
               System.out.println("JWT Token has expired");
           }
       } else {
           logger.warn("JWT Token does not begin with Bearer String");
       }

       if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
    	   jWTUtils.setToken(jwtToken);
           UserDetails userDetails = jWTUtils.getUserFromToken();

           if (jWTUtils.validateToken(jwtToken, userDetails)) {
               log.info("Extracted user from claims : {}", userDetails.toString());
               UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                       userDetails, null, userDetails.getAuthorities());

               usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
               SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
           }
       }

       filterChain.doFilter(req, res);
   }
		
	}
 

