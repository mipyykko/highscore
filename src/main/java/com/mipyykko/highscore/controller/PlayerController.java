/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.controller;

import com.mipyykko.highscore.domain.Player;
import com.mipyykko.highscore.domain.Score;
import com.mipyykko.highscore.service.PlayerService;
import com.mipyykko.highscore.service.ScoreService;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private ScoreService scoreService;

    @RequestMapping(value = {"/profile", "/profile/{id}"}, method = RequestMethod.GET)
    public String getProfile(Model model, @PathVariable Map<String, String> pathVariables) {
        Player player = pathVariables.containsKey("id")
                      ? playerService.get(Long.parseLong(pathVariables.get("id")))
                      : playerService.getAuthenticatedPlayer();
        
        if (player == null) {
            return "redirect:/";
        }
        
        model.addAttribute("player", player);
        List<Score> highScores = scoreService.getTopScoresByPlayer(player.getId());
        model.addAttribute("highscores", highScores.size());
        model.addAttribute("games", playerService.getGamesByPlayer(player.getId()));
        return "player";
    }
    
    @RequestMapping(value = "/players", method = RequestMethod.GET)
    public String getPlayers(Model model) {
        List<Player> players = playerService.getPlayers();
        
        model.addAttribute("players", players);
        return "players";
        
    }
}
