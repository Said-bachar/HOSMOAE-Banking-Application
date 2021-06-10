package com.ensa.hosmoaBank.controllers.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.ensa.hosmoaBank.models.Admin;
import com.ensa.hosmoaBank.models.Agency;
import com.ensa.hosmoaBank.models.Agent;
import com.ensa.hosmoaBank.models.User;
import com.ensa.hosmoaBank.repositories.AdminRepository;
import com.ensa.hosmoaBank.repositories.AgencyRepository;
import com.ensa.hosmoaBank.repositories.AgentRepository;
import com.ensa.hosmoaBank.repositories.CityRepository;
import com.ensa.hosmoaBank.repositories.ClientRepository;
import com.ensa.hosmoaBank.repositories.UserRepository;
import com.ensa.hosmoaBank.services.MailService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/admin")
public class AdminPanelController {

    @Autowired
    private UserRepository userRepository;

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

    Logger logger = LoggerFactory.getLogger(AdminPanelController.class);


    final String ADMIN_VIEWS_PATH = "views/admin/";


    @RequestMapping("/login")
    public String login(){
//        Redirect user to hompage if he's already authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/admin";
        }
        return ADMIN_VIEWS_PATH + "login";
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

    @GetMapping("users")
    public String usersView(Model model) {
        model.addAttribute("users",userRepository.findAll());
        return ADMIN_VIEWS_PATH + "users";
    }

    @GetMapping("users/add")
    public String addUserView(Model model) {
        model.addAttribute("agences", agencyRepository.findAll());
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