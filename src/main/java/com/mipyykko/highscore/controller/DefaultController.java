/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.controller;

import com.mipyykko.highscore.auth.JpaAuthenticationProvider;
import com.mipyykko.highscore.domain.Game;
import com.mipyykko.highscore.domain.Player;
import com.mipyykko.highscore.domain.RegisteringPlayer;
import com.mipyykko.highscore.domain.Score;
//import com.mipyykko.highscore.domain.UserType;
//import com.mipyykko.highscore.repository.UserTypeRepository;
import com.mipyykko.highscore.service.GameService;
import com.mipyykko.highscore.service.PlayerService;
import com.mipyykko.highscore.service.ScoreService;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import static sun.audio.AudioPlayer.player;

/**
 *
 * @author pyykkomi
 */
@Controller
@RequestMapping("*")
public class DefaultController {
    
    @Autowired
    private PlayerService playerService;
    @Autowired
    private GameService gameService;
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private JpaAuthenticationProvider jpaAuthenticationProvider;
    @Autowired
    private AuthenticationManager authenticationManager;
//    @Autowired
//    private UserTypeRepository userTypeRepository;
    
    @PostConstruct
    @Profile("default")
    public void init() {
        Player admin = new Player("admin", "password", "Administrator", "admin@mail.com", "Administrator");
        admin.setUserTypes(Arrays.asList(new Player.UserType[]{Player.UserType.USER, Player.UserType.ADMIN}));
        
        Player test1 = new Player("test1", "password", "Test User 1", "test1@mail.com", 
                "This is a long description. This is a long description. This is a long description. This is a long description. This is a long description.");
        test1.setUserTypes(Arrays.asList(new Player.UserType[]{Player.UserType.USER}));

        Player test2 = new Player("test2", "password", "Test User 2", "test2@mail.com", "");
        test2.setUserTypes(Arrays.asList(new Player.UserType[]{Player.UserType.USER}));
        
        playerService.save(admin);
        playerService.save(test1);
        playerService.save(test2);
        
        Game game1 = new Game("Peli 1", "Game Company", "1998");
        Game game2 = new Game("Peli 2", "Game Company", "2012");
        
        game1.setScoreType(Game.ScoreType.HIGHEST);
        game2.setScoreType(Game.ScoreType.LOWEST);
        
        gameService.save(game1);
        gameService.save(game2);
        
        Score score1 = new Score(game1, test1, "12320", new Date(System.currentTimeMillis()));
        Score score2 = new Score(game1, test2, "11000", new Date(System.currentTimeMillis() - 1000));
        Score score3 = new Score(game2, test2, "100000", new Date(System.currentTimeMillis() - 22222));
        Score score4 = new Score(game2, test1, "90000", new Date(System.currentTimeMillis() - 3333));

        scoreService.addScore(score1);
        scoreService.addScore(score2);
        scoreService.addScore(score3);
        scoreService.addScore(score4);
    }
    
    @RequestMapping(value = "/")
    public String index(Model model) {
        Player currentuser = playerService.getAuthenticatedPlayer();
        model.addAttribute("currentuser", currentuser);
        List<Game> games = gameService.getTopGames();
        model.addAttribute("games", games);
        List<Player> players = playerService.getMostActivePlayers();
        model.addAttribute("players", players);
        return "index";
    }
    
    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }
    
    @RequestMapping(value = "register", method = RequestMethod.GET)
    public String showRegister(@ModelAttribute RegisteringPlayer registeringPlayer) {
        if (playerService.getAuthenticatedPlayer() != null) {
            return "redirect:/";
        }
        
        return "register";
    }
    
    @RequestMapping(value = "register", method = RequestMethod.POST)
    public String handleRegister(@Validated @ModelAttribute RegisteringPlayer registeringPlayer, BindingResult bindingResult) {
        if (!registeringPlayer.getPassword().isEmpty() &&
            !registeringPlayer.getPassword().equals(registeringPlayer.getPasswordagain())) {
            bindingResult.rejectValue("passwordagain", "error.player", "Must match password!");
        }
        if (bindingResult.hasErrors()) {
            return "register";
        }

        Player player = new Player(registeringPlayer.getUsername(), registeringPlayer.getPassword(),
                                   registeringPlayer.getName(), registeringPlayer.getEmail(),
                                   registeringPlayer.getDescription());
        player.getUserTypes().add(Player.UserType.USER);
        playerService.save(player);
        
        UsernamePasswordAuthenticationToken token = 
                new UsernamePasswordAuthenticationToken(player.getUsername(), player.getPassword());
        Authentication user = authenticationManager.authenticate(token);
        SecurityContextHolder
                .getContext()
                .setAuthentication(
                        jpaAuthenticationProvider
                                .authenticate(user));
        return "redirect:/";
    }
}
