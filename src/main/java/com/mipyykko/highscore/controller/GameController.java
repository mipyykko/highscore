/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.controller;

import com.mipyykko.highscore.domain.Game;
import com.mipyykko.highscore.domain.Player;
import com.mipyykko.highscore.domain.Score;
import com.mipyykko.highscore.service.GameService;
import com.mipyykko.highscore.service.PlayerService;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
@RequestMapping("/games")
public class GameController {
    
    @Autowired
    private GameService gameService;
    @Autowired
    private PlayerService playerService;
    
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
        Player player = playerService.getAuthenticatedPlayer();
        
        if (game == null) {
            return "redirect:/games";
        }
        
        List<Score> scores = game.getScores()
                                .stream()
                                .filter(score -> score.isAccepted() || 
                                        (player != null && score.getPlayer() == player))
                                .collect(Collectors.toList());
        Collections.sort(scores);
        model.addAttribute("game", game);
        model.addAttribute("scores", scores);
        return "game";
    }
    
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String showAddGame(Model model, @ModelAttribute Game game) {
        Player player = playerService.getAuthenticatedPlayer();
        
        if (player == null) {
            return "redirect:/";
        }
        
        model.addAttribute("game", game);
        
        return "addgame";
    }
    
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String handleAddGame(Model model, @Validated @ModelAttribute Game game, BindingResult bindingResult) {
        if (gameService.findSimilar(game) != null) {
                bindingResult.rejectValue("uniqueness", "errors.game", "Game is not unique enough!");
        }

        if (bindingResult.hasErrors()) {
            return "addgame";
        }
        
        gameService.save(game);
        return "redirect:/";
    }
}
