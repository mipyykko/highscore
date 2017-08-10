/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.controller;

import com.mipyykko.highscore.domain.Player;
import com.mipyykko.highscore.service.PlayerService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author pyykkomi
 */
@Controller
public class PlayerController {
    
    @Autowired
    private PlayerService playerService;
    
    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String ownProfile(Model model) {
        Player player = playerService.getAuthenticatedPlayer();
        
        if (player == null) {
            return "redirect:/";
        }
        
        model.addAttribute("player", player);
        // TODO: get highest scores
        model.addAttribute("highscores", null);
        return "player";
    }
    
    @RequestMapping(value = "/profile/{id}", method = RequestMethod.GET)
    public String otherProfile(Model model, @PathVariable Long id) {
        Player player = playerService.get(id);
        
        if (player == null) {
            return "redirect:/";
        }
        
        model.addAttribute("player", player);
        // TODO: get highest scores
        model.addAttribute("highscores", null);
        return "player";
    }
    @RequestMapping(value = "/players", method = RequestMethod.GET)
    public String getPlayers(Model model) {
        List<Player> players = playerService.getPlayers();
        
        model.addAttribute("players", players);
        return "players";
        
    }
}
