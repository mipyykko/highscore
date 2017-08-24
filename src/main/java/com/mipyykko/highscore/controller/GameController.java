/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.controller;

import com.mipyykko.highscore.controller.common.HeaderInfo;
import com.mipyykko.highscore.domain.Game;
import com.mipyykko.highscore.domain.Player;
import com.mipyykko.highscore.domain.Score;
import com.mipyykko.highscore.service.GameService;
import com.mipyykko.highscore.service.PlayerService;
import com.mipyykko.highscore.service.ScoreService;
import com.mipyykko.highscore.util.Pager;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import org.springframework.web.bind.support.SessionStatus;

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
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private HeaderInfo headerInfo;
    
    @ModelAttribute
    public void addHeaderAttributes(Model model) {
        model.addAllAttributes(headerInfo.getHeaderAttributes());
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getGames(Model model, 
            @RequestParam(value = "page") Optional<Integer> page,
            @RequestParam(value = "length") Optional<Integer> length,
            @RequestParam(value = "direction") Optional<String> direction,
            @RequestParam(value = "criteria") Optional<String> criteria) {
        int pageLength = Math.max(length.orElse(10), 1);
        int currentPage = Math.min(Math.max(page.orElse(1), 1), Math.max(1, (int) (gameService.gameCount() / pageLength)));
        
        // 1 >= page <= amount of pages
        
        Page<Game> gamePage = 
                gameService.getGames(currentPage - 1, pageLength, 
                                     direction.orElse("asc"), criteria.orElse("name"));
        Pager pager = new Pager(currentPage, gamePage.getTotalPages(), pageLength);
        Map<Game, Long> games = new LinkedHashMap<>();
        gamePage.forEach(g -> games.put(g, g.getScores()
                .stream()
                .filter(s -> s.isAccepted())
                .count()));

        model.addAttribute("games", games);
        model.addAttribute("pager", pager);
        
        return "games";
    }
    
//    @RequestMapping(method = RequestMethod.POST)
//    public String saveGame(@RequestParam String name, 
//            @RequestParam String publisher,
//            @RequestParam String year,
//            @RequestParam(required = false, defaultValue = "0") Long id) {
//        Game game = id > 0 ? gameService.get(id) : new Game(name, publisher, year);
//        
//        if (game != null) {
//            gameService.save(game);
//        }
//        return "redirect:/games";
//    }
    
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String getGame(Model model, @PathVariable Long id) {
        Game game = gameService.get(id);
        Player player = playerService.getAuthenticatedPlayer();
        
        if (game == null) {
            return "redirect:/games";
        }
        
        List<Score> scores = game.getScores()
                                .stream()
                                .filter(score -> score.isAccepted() || // always show accepted
                                        (player != null 
                                            && (score.getPlayer() == player || playerService.isAdmin()) 
                                            && !score.isRejected())) // and pending, if logged in is admin or self
                                .collect(Collectors.toList());
        Collections.sort(scores);
        model.addAttribute("game", game);
        model.addAttribute("scores", scores);
        return "game";
    }
    
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String showAddGame(Model model, @ModelAttribute Game game, SessionStatus sessionStatus) {
        Player player = playerService.getAuthenticatedPlayer();
        
        if (player == null) {
            return "redirect:/";
        }
        
        model.addAttribute("game", game);
        return "addgame";
    }
    
    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String showEditGame(Model model, @PathVariable Long id) {
        Game game = gameService.get(id);
        if (game == null || playerService.getAuthenticatedPlayer() == null) {
            return "redirect:/";
        }
        model.addAttribute("game", game);
        
        return "editgame";
    }
    
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String handleAddGame(Model model, 
            @Validated @ModelAttribute Game game, 
            BindingResult bindingResult) {
        if (playerService.getAuthenticatedPlayer() == null) {
            return "redirect:/";
        }
        
        if (!gameService.findSimilar(game).isEmpty()) {
            bindingResult.rejectValue("uniqueness", "errors.game", "Game is not unique enough!");
        }

        if (bindingResult.hasErrors()) {
            return "addgame";
        }
        
        gameService.save(game);
        return "redirect:/";
    }
    
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String handleEditGame(Model model, 
            @Validated @ModelAttribute Game game,
            BindingResult bindingResult) {
        if (playerService.getAuthenticatedPlayer() == null) {
            return "redirect:/";
        }

        List<Game> similar = gameService.findSimilar(game);
        if (!similar.isEmpty()) {
            for (Game g : similar) {
                if (!g.getId().equals(game.getId())) {
                    bindingResult.rejectValue("uniqueness", "errors.game", "Game is not unique enough!");
                    break;
                }
            }
        }
        
        if (bindingResult.hasErrors()) {
            return "redirect:/games/edit/" + game.getId();
        }
        
        gameService.update(game);
        return "redirect:/games/" + game.getId();
    }
}
