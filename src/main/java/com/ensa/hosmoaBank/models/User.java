package com.ensa.hosmoaBank.models;



import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.Email;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ensa.hosmoaBank.enumerations.Role;
import com.fasterxml.jackson.annotation.*;

import lombok.*;



@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@Entity
//@MappedSuperclass
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Builder
public class User implements UserDetails{
	
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	
	private Long id;
	
    
    //private String username; 
    
    @Column(unique = true)
    @Email
	private String email;
    
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
	
	@Enumerated(EnumType.STRING)
	private Role role;

	
	@CreationTimestamp
    private Date dateOfCreation;

    @UpdateTimestamp
    private Date dateUpdate;
    
    protected boolean emailConfirmed;
    
    protected Boolean archived;
    
    @JsonIgnore
    private String secretKey;
    
    // for 2Fa
    private Boolean _2FaEnabled;
    
    //Token Verf
    @JsonIgnore
	protected String verificationToken;
    
    private String phoneNumber;
    
    private String picture;
    protected String adress;
    protected String city;
    private Long codePostale;
    
    private String lastName, firstName;
    
    // Relations between User & other classes :
    
	@OneToOne(mappedBy = "user", cascade = {CascadeType.REMOVE})
	//  @JsonIgnore
	private Admin admin;
	
	@OneToOne(mappedBy = "user", fetch = FetchType.EAGER, cascade = {CascadeType.REMOVE})
	@JsonIgnoreProperties({"user"})
	private Agent agent;
	
	@OneToOne(mappedBy = "user", cascade = {CascadeType.REMOVE})
	//  @JsonIgnore
	@JsonIgnoreProperties({"user"})
	private Client client;
    
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		List<GrantedAuthority> authorityList = new ArrayList<GrantedAuthority>();
		authorityList.add(new SimpleGrantedAuthority("ROLE_" + role));
		return authorityList;
	}
	
	  // triggered at begining of transaction : generate default values for User
    @PrePersist
    void beforeInsert() {
        System.out.println("SETTING DEFAULT VALUES FOR USER");
        emailConfirmed = false;
        archived = false;
        _2FaEnabled = false;
        //verificationToken = VerificationTokenGenerator.generateVerificationToken();
    }
	

	
    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getPassword() {
        return password;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}
