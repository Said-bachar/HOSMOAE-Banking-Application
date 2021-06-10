package com.ensa.hosmoaBank.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ensa.hosmoaBank.enumerations.Role;
import com.ensa.hosmoaBank.filters.JwtFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class Security extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {
	
	@Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private JwtAuthEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.formLogin().loginPage("/admin/login").defaultSuccessUrl("/admin", true); //true !!

//      Disable CSRF
        httpSecurity.cors().disable().csrf().disable()
//      Allow certain routes
                .authorizeRequests().antMatchers("/validate/key","/api/generate/**","/admin/login","/verify", "/api/auth/**", "/api/auth/agent", "/api/forgot_password", "/confirm", "/set_password","/js/**","/css/**").permitAll().
                and().authorizeRequests().antMatchers("/admin/**").hasRole(Role.ADMIN.name()). // just for now
                and().authorizeRequests().antMatchers("/agent/**").hasRole(Role.AGENT.name()).
                and().authorizeRequests().antMatchers("/client/**").hasRole(Role.CLIENT.name()).
                anyRequest().authenticated()
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);

        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowCredentials(false)    //true!!
            .allowedHeaders("*")
            .allowedMethods("GET, POST, PATCH, PUT, DELETE, OPTIONS")
            .allowedOrigins("*");
    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.formLogin().loginPage("/admin/login");
//        http.authorizeRequests().antMatchers("/admin/login","/verify", "/api/auth", "/api/auth/agent", "/api/forgot_password", "/confirm", "/set_password","/js/**","/css/**").permitAll()
//                .and().authorizeRequests().antMatchers("/admin/**").hasRole(Role.ADMIN.name());
////                .and().authorizeRequests().antMatchers("/agent/**").hasRole(Role.AGENT.name())
////                .and().authorizeRequests().antMatchers("/client/**").hasRole(Role.CLIENT.name())
////                .anyRequest().authenticated()
////                .and().exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
////                .sessionCreationPolicy(SessionCreationPolicy.NEVER);
//
//        http.authorizeRequests().antMatchers("/api/auth", "/api/auth/agent").permitAll()
//                .and().authorizeRequests().antMatchers("/agent/**").hasRole(Role.AGENT.name())
//                .and().authorizeRequests().antMatchers("/client/**").hasRole(Role.CLIENT.name())
//                .anyRequest().authenticated()
//                .and().exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
////
////                http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//
//
//
//    }

 //}

//    @Bean
//    public FilterRegistrationBean xssPreventFilter() {
//        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//
//        registrationBean.setFilter(new XSSFilter());
//        registrationBean.addUrlPatterns("/*");
//
//        return registrationBean;
//    }

 //}
//}
}
