package com.ensa.hosmoaBank.controllers.admin;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.ensa.hosmoaBank.models.Admin;
import com.ensa.hosmoaBank.models.Agency;
import com.ensa.hosmoaBank.models.Agent;
import com.ensa.hosmoaBank.models.ConfirmationToken;
import com.ensa.hosmoaBank.models.User;
import com.ensa.hosmoaBank.repositories.AdminRepository;
import com.ensa.hosmoaBank.repositories.AgencyRepository;
import com.ensa.hosmoaBank.repositories.AgentRepository;
import com.ensa.hosmoaBank.repositories.CityRepository;
import com.ensa.hosmoaBank.repositories.ClientRepository;
import com.ensa.hosmoaBank.repositories.ConfirmationTokenRepository;
import com.ensa.hosmoaBank.repositories.UserRepository;
import com.ensa.hosmoaBank.services.MailService;

@Controller
@RequestMapping("/admin")
public class AdminPanelController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private MailService mailService;
    
    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;
    
    @Autowired
    private JavaMailSender mailSender;

    Logger logger = LoggerFactory.getLogger(AdminPanelController.class);


    final String ADMIN_VIEWS_PATH = "views/admin/";


    @RequestMapping("/login")
    public String login(){
//        Redirect user to hompage if he's already authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
       System.out.println(auth);
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/admin";
        }
        return ADMIN_VIEWS_PATH + "login2";
    }
    @PostMapping("/logout")
    public String logout(HttpServletRequest request){
//        HttpSession session = request.getSession();
        return ADMIN_VIEWS_PATH + "login";
    }
    @GetMapping("")
    public String index(Model model) {
        return ADMIN_VIEWS_PATH + "index";
    }
    
    
    @GetMapping("forgotPassword")
    public String forgotPasssword() {
    	
    	return ADMIN_VIEWS_PATH + "forgotPassword";
    }
    
    @PostMapping("forgotPassword")
    public String resetPassswordRequest(Model model,@RequestParam("email") String email) {
    	User user = userRepository.findByEmail(email);
    	
   	
    	if(user != null) {
    		
    		 System.out.println("useeeeeeeeeeeeeeeeeeeeeeeer "+user);
    		
    		ConfirmationToken confirmationToken = new ConfirmationToken(user);
    		confirmationTokenRepository.save(confirmationToken);
    		SimpleMailMessage mailMessage = new SimpleMailMessage();
    		
    		mailMessage.setTo(user.getEmail());
    		mailMessage.setSubject("comlete passwordreset");
    		mailMessage.setText("To complete the password reset process, please click here: "
    	              + "http://localhost:8085/admin/confirmReset?token="+confirmationToken.getConfirmationToken());

    		mailSender.send(mailMessage);
    		model.addAttribute("code", "Request to reset password received. Check your inbox for the reset link.");
    		return ADMIN_VIEWS_PATH + "successForgotPassword";
    	} else {
    		model.addAttribute("code", "email non existant");
    		return "views//errors/any";
    	}
  
    }
    
    @GetMapping("confirmReset")
    public String  validateResetToken(Model model, @RequestParam("token")String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if (token != null) {
            User user = userRepository.findByEmail(token.getUser().getEmail());
     
         
            model.addAttribute("email", user.getEmail());
            return ADMIN_VIEWS_PATH + "resetPassword";
    	} else {
      
    		model.addAttribute("code","Le lien n'est pas valide.");
    		return "views/errors/any";
        }
    }
    
    
    @PostMapping("resetPassword")
    public String resetPasssword(Model model,@RequestParam("email") String email, @RequestParam("password") String password) {
    	User user = userRepository.findByEmail(email);
    	
   	
    	if(user != null) {
    		
    		user.setPassword(password);
    		userRepository.save(user);
    		return ADMIN_VIEWS_PATH + "";
    	} else {
    		model.addAttribute("code", "le lien n'est pas valide");
    		return "views//errors/any";
    	}
  
    }
    

    @GetMapping("users")
    public String usersView(Model model) {
        model.addAttribute("users",userRepository.findAll());
        return ADMIN_VIEWS_PATH + "users";
    }

    @GetMapping("users/add")
    public String addUserView(Model model) {
        model.addAttribute("agencies", agencyRepository.findAll());
        model.addAttribute("user", new User());
        return ADMIN_VIEWS_PATH + "forms/user.add";
    }
    @PostMapping("users/add")
    public String addUser(@ModelAttribute User user, HttpServletRequest request) {
//        if (user.getAgent() )
        try {
            if (user.getRole().name().equals("ADMIN")) {
//            System.out.println("SAVING ADMIN : " + user.toString());
                // pour eviter le probleme de transaction
                user.setAgent(null);
                user = userRepository.save(user);
                adminRepository.save(Admin.builder().user(user).build());
            }
            if (user.getRole().name().equals("AGENT")) {
//            System.out.println("SAVING AGENT : " + user.toString());
                Agency chosenAgence = user.getAgent().getAgency();
                // pour eviter le probleme de transaction
                user.setAgent(null);
                user = userRepository.save(user);
                // c juste parcequ'on n'a pas encore implemente l'auth.
                Admin admin = adminRepository.getOne((long) 1);
                agentRepository.save(Agent.builder().user(user).admin(admin).agency(chosenAgence).build());
            }
            mailService.sendVerificationMail(user);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This mail is already exists.");
        }


        return "redirect:/admin/users";
    }
    
    @GetMapping("config")
    public String configView(Model model) {
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	String email=null;
    	if (principal instanceof UserDetails) {
    	  email = ((UserDetails)principal).getUsername();
    	} 
    	else {
    	  email = principal.toString();
    	}
    	model.addAttribute("email",email);
        return ADMIN_VIEWS_PATH + "config";
    }
    
    @GetMapping("config/change_password")
    public String changePasswordView(Model model) {
        return ADMIN_VIEWS_PATH + "changePswd";
    }
    
    @PostMapping("config/change_password")
    public String changePassword(@RequestParam(value = "oldPassword") String oldPassword,@RequestParam(value = "newPassword") String newPassword) {
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	String username=null;
    	if (principal instanceof UserDetails) {
    	  username = ((UserDetails)principal).getUsername();
    	} 
    	else {
    	  username = principal.toString();
    	}
        User userToUpdate = userRepository.findByEmail(username);
        userToUpdate.setPassword(encoder.encode(newPassword));
        userRepository.save(userToUpdate);
        return "redirect:/admin/config";
    }

    @GetMapping("users/update/{id}")
    public String updateUserView(Model model, @PathVariable(value = "id") Long id) {
        model.addAttribute("user", userRepository.getOne(id));
        return ADMIN_VIEWS_PATH + "forms/user.update";
    }

    @PostMapping("users/update/{id}")
    public String updateUser(@ModelAttribute User user) {
        User userToUpdate = userRepository.getOne(user.getId());
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setLastName(user.getLastName());
        userToUpdate.setFirstName(user.getFirstName());

        userRepository.save(userToUpdate);
        return "redirect:/admin/users";
    }

    @PostMapping("users/delete/{id}")
    public String deleteUser(@PathVariable(value = "id") Long id) {
        User user = userRepository.getOne(id);
        // agentRepository.delete(user.getAgent());
        userRepository.delete(user);

        return "redirect:/admin/users";
    }

    @GetMapping("agencies")
    public String agencesView(Model model) {
//        model.addAttribute("agents", agentRepository.findAll());
        model.addAttribute("agencies", agencyRepository.findAll());
        return ADMIN_VIEWS_PATH + "agencies";
    }
    @GetMapping("agencies/add")
    public String addAgenceView(Model model) {
        model.addAttribute("agency", new Agency());
        model.addAttribute("cities", cityRepository.findAll());
        return ADMIN_VIEWS_PATH + "forms/agency.add";
    }
    @PostMapping("agencies/add")
    public String addAgence(@ModelAttribute Agency agency) {
        Admin admin = adminRepository.getOne((long) 1);
        agency = Agency.builder().city(agency.getCity()).agencyWording(agency.getAgencyWording()).admin(admin).build();
        agencyRepository.save(agency);
        return "redirect:/admin/agencies";
    }
    @GetMapping("agencies/update/{id}")
    public String updateAgenceView(Model model, @PathVariable(value = "id") Long id) {
        model.addAttribute("agency", agencyRepository.getOne(id));
        model.addAttribute("cities", cityRepository.findAll());

        return ADMIN_VIEWS_PATH + "forms/agency.update";
    }
    @PostMapping("agencies/update/{id}")
    public String updateAgence(@ModelAttribute Agency agency) {
//        System.out.println(agence.toString());
        Agency agenceToUpdate = agencyRepository.getOne(agency.getId());
        agenceToUpdate.setAgencyWording(agency.getAgencyWording());
        agenceToUpdate.setCity(agency.getCity());
        agencyRepository.save(agenceToUpdate);
        return "redirect:/admin/agencies";
    }
    @PostMapping("agencies/delete/{id}")
    public String deleteAgence(@PathVariable(value = "id") Long id) {
        Agency agency = agencyRepository.getOne(id);
        agencyRepository.delete(agency);
        return "redirect:/admin/agencies";
    }

}