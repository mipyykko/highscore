/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.controller;

import com.mipyykko.highscore.auth.JpaAuthenticationProvider;
import com.mipyykko.highscore.controller.common.HeaderInfo;
import com.mipyykko.highscore.domain.Player;
import com.mipyykko.highscore.domain.forms.FormPlayer;
import com.mipyykko.highscore.domain.Score;
import com.mipyykko.highscore.service.PlayerService;
import com.mipyykko.highscore.service.ScoreService;
import com.mipyykko.highscore.util.Pager;
import com.mipyykko.highscore.util.PlayerConverter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.lang.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author pyykkomi
 */
@Controller
public class PlayerController {
    
    @Autowired
    private PlayerService playerService;
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private JpaAuthenticationProvider jpaAuthenticationProvider;
    @Autowired
    private HeaderInfo headerInfo;
    
    @ModelAttribute
    public void addHeaderAttributes(Model model) {
        model.addAllAttributes(headerInfo.getHeaderAttributes());
    }
    
    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }
    
    @RequestMapping(value = "register", method = RequestMethod.GET)
    public String showRegister(@ModelAttribute FormPlayer registeringPlayer) {
        if (playerService.getAuthenticatedPlayer() != null) {
            return "redirect:/";
        }
        
        return "register";
    }
    
    @RequestMapping(value = "register", method = RequestMethod.POST)
    public String handleRegister(@Validated @ModelAttribute FormPlayer formPlayer, BindingResult bindingResult) {
        if (!formPlayer.getPassword().isEmpty() &&
            !formPlayer.getPassword().equals(formPlayer.getPasswordagain())) {
            bindingResult.rejectValue("passwordagain", "error.player", "Must match password!");
        }
        if (bindingResult.hasErrors()) {
            return "register";
        }

        Player player = PlayerConverter.toPlayer(formPlayer);
        playerService.save(player);
        
        Authentication user = new UsernamePasswordAuthenticationToken(player.getUsername(), player.getPassword());
        SecurityContextHolder
                .getContext()
                .setAuthentication(
                        jpaAuthenticationProvider
                                .authenticate(user));
        return "redirect:/";
    }

    @RequestMapping(value = {"/profile", "/profile/{id}"}, method = RequestMethod.GET)
    public String getProfile(Model model, @PathVariable Map<String, String> pathVariables) {
        Player authenticatedPlayer = playerService.getAuthenticatedPlayer();
        
        Player player = pathVariables.containsKey("id")
                      ? playerService.get(Long.parseLong(pathVariables.get("id")))
                      : authenticatedPlayer;
        
        if (player == null) {
            return "redirect:/";
        }
        
        model.addAttribute("player", player);
        List<Score> highScores = scoreService.getTopScoresByPlayer(player.getId());
        model.addAttribute("highscores", highScores.size());
        model.addAttribute("games", playerService.getGamesByPlayer(player.getId()));
        model.addAttribute("ownprofile", !pathVariables.containsKey("id") 
                || pathVariables.containsKey("id") 
                    && authenticatedPlayer != null 
                    && Long.parseLong(pathVariables.get("id")) == authenticatedPlayer.getId());
        return "player";
    }
    
    @RequestMapping(value = "/profile/edit", method = RequestMethod.GET)
    public String getEditProfile(Model model, @ModelAttribute FormPlayer formPlayer) {
        Player player = playerService.getAuthenticatedPlayer();
        
        if (player == null) {
            return "redirect:/";
        }
         if (formPlayer == null || formPlayer.getUsername() == null) {
            formPlayer = PlayerConverter.toForm(player);
        }
        
        model.addAttribute("formPlayer", formPlayer);
        model.addAttribute("edit", true);
        return "editprofile";
    }
    
    @RequestMapping(value = "/profile/edit", method = RequestMethod.POST)
    public String handleEditProfile(Model model, @Validated @ModelAttribute FormPlayer formPlayer,
            BindingResult bindingResult) {
        Player player = playerService.getAuthenticatedPlayer();
        if (player == null) {
            return "redirect:/";
        }

        Player checkPlayer = playerService.getByUsername(formPlayer.getUsername().trim());
        if (checkPlayer != null && !checkPlayer.getId().equals(formPlayer.getId())) {
            bindingResult.rejectValue("username", "error.player", "This username already exists!");
        }
        
        if (formPlayer.getOldpassword() != null && !formPlayer.getOldpassword().isEmpty()) {
            if (!BCrypt.hashpw(formPlayer.getOldpassword(), player.getSalt()).equals(player.getPassword())) {
                bindingResult.rejectValue("oldpassword", "error.player", "Old password does not match!");
            }
            if (!formPlayer.getPassword().isEmpty() &&
                !formPlayer.getPassword().equals(formPlayer.getPasswordagain())) {
                bindingResult.rejectValue("passwordagain", "error.player", "Must match password!");
            }
        } else {
            BindingResult ignoredBindingResult = new BindException(model, null);
            for (FieldError fe : bindingResult.getFieldErrors()) {
                if (!fe.getField().equals("password") && !fe.getField().equals("passwordagain")) {
                    ignoredBindingResult.addError(fe);
                }
            }
            bindingResult = ignoredBindingResult;
        }

        if (bindingResult.hasErrors()) {
            return "editprofile";
        }
        
        player.setUsername(formPlayer.getUsername());
        player.setEmail(formPlayer.getEmail());
        player.setDescription(formPlayer.getDescription());
        player.setName(formPlayer.getName());
        if (formPlayer.getOldpassword() != null && !formPlayer.getOldpassword().isEmpty()) {
            player.setPassword(formPlayer.getPassword());
        }
        
        playerService.save(player);
        
        return "redirect:/profile";
    }
    
    @RequestMapping(value = "/players", method = RequestMethod.GET)
    public String getPlayers(Model model,
            @RequestParam(value = "page") Optional<Integer> page,
            @RequestParam(value = "length") Optional<Integer> length,
            @RequestParam(value = "direction") Optional<String> direction,
            @RequestParam(value = "criteria") Optional<String> criteria) {
        int pageLength = Math.max(length.orElse(10), 1);
        int currentPage = Math.min(Math.max(page.orElse(1), 1), Math.max(1, (int) (playerService.playerCount() / pageLength)));

        Page<Player> playerPage = 
                playerService.getPlayers(currentPage - 1, pageLength, 
                        direction.orElse("asc"), criteria.orElse("username"));
        Pager pager = new Pager(currentPage, playerPage.getTotalPages(), pageLength);
        Map<Player, Long> players = new LinkedHashMap<>();
        playerPage.forEach(p -> players.put(p, p.getScores()
                .stream()
                .filter(s -> s.isAccepted())
                .count()));
        
        model.addAttribute("players", players);
        model.addAttribute("pager", pager);
        return "players";
        
    }
}
