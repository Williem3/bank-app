package com.userfront.userfront.controller;

import com.userfront.userfront.dao.RoleDao;
import com.userfront.userfront.domain.PrimaryAccount;
import com.userfront.userfront.domain.SavingsAccount;
import com.userfront.userfront.domain.User;
import com.userfront.userfront.domain.security.UserRole;
import com.userfront.userfront.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleDao roleDao;

    @RequestMapping("/")
    public String home() {
        return "redirect:/index";
    }

    @RequestMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/signup")
    public String signup(Model theModel) {
        User user = new User();

        theModel.addAttribute("user", user);

        return "signup";
    }

    @PostMapping("/signup")
    public String signupPost(@ModelAttribute("user") User user, Model theModel) {
        // check if user exists in database
        if (userService.checkUserExists(user.getUsername(), user.getEmail())) {
            // check if email exist in database
            if (userService.checkEmailExists(user.getEmail())) {
                theModel.addAttribute("emailExists", true);
            }
            // check if username exists in database
            if (userService.checkUsernameExists(user.getUsername())) {
                theModel.addAttribute("usernameExists", true);
            }
            return "signup";
        } else {
            Set<UserRole> userRoles = new HashSet<>();
            userRoles.add(new UserRole(user, roleDao.findByName("ROLE_USER")));
            userService.createUser(user, userRoles);

            return "redirect:/";
        }
    }

    @RequestMapping("/userFront")
    public String userFront(Principal principal, Model theModel) {
        User user = userService.findByUsername(principal.getName());
        PrimaryAccount primaryAccount = user.getPrimaryAccount();
        SavingsAccount savingsAccount = user.getSavingsAccount();

        theModel.addAttribute("primaryAccount", primaryAccount);
        theModel.addAttribute("savingsAccount", savingsAccount);

        return "userFront";
    }
}

