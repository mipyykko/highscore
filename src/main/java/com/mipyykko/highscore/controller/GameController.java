/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.controller;

import com.mipyykko.highscore.domain.Game;
import com.mipyykko.highscore.service.GameService;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author pyykkomi
*/
@Controller
@RequestMapping("/games")
public class GameController {
    
    @Autowired
    private GameService gameService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String getGames(Model model, 
            @RequestParam(required = false, defaultValue = "0") int start,
            @RequestParam(required = false, defaultValue = "25") int length,
            @RequestParam(required = false, defaultValue = "asc") String direction,
            @RequestParam(required = false, defaultValue = "name") String criteria) {
        Page<Game> gamePage = gameService.getGames(start, length, direction, criteria);
        model.addAttribute("games", gamePage.getContent());
        
        return "games";
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String saveGame(@RequestParam String name, 
            @RequestParam String publisher,
            @RequestParam String year,
            @RequestParam(required = false, defaultValue = "0") Long id) {
        Game game = id > 0 ? gameService.get(id) : new Game(name, publisher, year);
        
        if (game != null) {
            gameService.save(game);
        }
        return "redirect:/games";
    }
    
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String getGame(Model model, @PathVariable Long id) {
        Game game = gameService.get(id);
        
        if (game == null) {
            return "redirect:/games";
        }
        
        Collections.sort(game.getScores());
        model.addAttribute("game", game);
        return "game";
    }
}
