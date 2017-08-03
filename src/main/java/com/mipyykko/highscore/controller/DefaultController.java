/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.controller;

import com.mipyykko.highscore.domain.Player;
import com.mipyykko.highscore.domain.UserType;
import com.mipyykko.highscore.repository.UserTypeRepository;
import com.mipyykko.highscore.service.PlayerService;
import java.util.Arrays;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author pyykkomi
 */
@Controller
public class DefaultController {
    
    @Autowired
    private PlayerService playerService;
    @Autowired
    private UserTypeRepository userTypeRepository;
    
    @PostConstruct
    public void init() {
        UserType userTypeUser = new UserType("USER");
        UserType userTypeAdmin = new UserType("ADMIN");
        userTypeRepository.save(userTypeUser);
        userTypeRepository.save(userTypeAdmin);
        
        Player admin = new Player("admin", "password");
        admin.setUserTypes(Arrays.asList(new UserType[]{userTypeUser, userTypeAdmin}));
        
        Player test1 = new Player("test1", "password");
        test1.setUserTypes(Arrays.asList(new UserType[]{userTypeUser}));

        playerService.save(admin);
        playerService.save(test1);
    }
    
    @RequestMapping(value = "/")
    public String index(Model model) {
        Player currentuser = playerService.getAuthenticatedPlayer();
        model.addAttribute("currentuser", currentuser);
        return "index";
    }
    
    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }
    
}
