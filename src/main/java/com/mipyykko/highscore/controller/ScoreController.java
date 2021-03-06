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
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
@RequestMapping("/scores")
public class ScoreController {
    
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
    
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String showScore(Model model, @PathVariable Long id) {
        Score score = scoreService.get(id);
        if (score == null) {
            return "redirect:/";
        }
        
        model.addAttribute("score", score);
        return "score";
    }
    
    @RequestMapping(value = "/add/{id}", method = RequestMethod.GET)
    public String showAddScore(Model model, @PathVariable Long id, @ModelAttribute Score score) {
        Game game = gameService.get(id);
        Player player = playerService.getAuthenticatedPlayer();
        
        if (game == null || player == null) {
            return "redirect:/";
        }
        
        if (score == null || score.getGame() == null) {
            score = new Score(game, player, null, null);
        }
        
        score.setGame(game);
        score.setPlayer(player);
        
        model.addAttribute("score", score);
        return "addscore";
    }
    
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String handleAddScore(Model model, @Valid @ModelAttribute Score score, BindingResult bindingResult) {
        if (playerService.getAuthenticatedPlayer() == null) {
            return "redirect:/games";
        }
        
        score.setSentDate(new Date(System.currentTimeMillis()));

        if (score.getScoreDate() == null) {
            bindingResult.rejectValue("scoreDate", "errors.score", "You must enter a date!");
        }
        if (score.getScoreDate() != null && score.getScoreDate().after(score.getSentDate())) {
            score.setScoreDate(new Date(System.currentTimeMillis()));
            bindingResult.rejectValue("scoreDate", "errors.score", "Date cannot be in the future!");
        }
        
        // last minute fix
        if (score.getGame() != null && score.getGame().getScoreType() == Game.ScoreType.LONG) {
            try {
                Long testNumberFormat = Long.parseLong(score.getScoreValue());
            } catch (Exception e) {
                bindingResult.rejectValue("scoreValue", "errors.score", "This score must be in a numeric format!");
            }
        }

        if (bindingResult.hasErrors()) {
            return showAddScore(model, score.getGame().getId(), score);
        }
 
        score = scoreService.save(score);
        
        return "redirect:/games/" + (score.getGame() == null ? "" : score.getGame().getId());
    }
    
    @RequestMapping(value = "/pending", method = RequestMethod.GET)
    public String getPending(Model model) {
        Player player = playerService.getAuthenticatedPlayer();
        
        if (player == null || 
           (player != null && !player.isUserType(Player.UserType.ADMIN))) {
            return "redirect:/";
        }
        
        List<Score> pendingScores = scoreService.getPending();
        model.addAttribute("pendingScores", pendingScores);

        return "pendingscores";
    }
    
    @RequestMapping(value = "/pending/{id}", method = RequestMethod.POST)
    public String handlePending(Model model, 
            @RequestParam(required = false, value = "acceptScore") String acceptScore, 
            @RequestParam(required = false, value = "rejectScore") String rejectScore, 
            @RequestParam(required = false, value = "redirect") String redirect,
            @PathVariable Long id) {
        Score score = scoreService.get(id);

        if (score != null) {
            score.setStatus(acceptScore != null 
                          ? Score.Status.ACCEPTED
                          : Score.Status.REJECTED);
            scoreService.save(score);
            
            switch (redirect) {
                case "pending":
                    return "redirect:/scores/pending";
                case "score":
                    return "redirect:/scores/" + id;
                default:
                    return "redirect:/";
            }
        }
        
        return "redirect:/";
    }

//    @RequestMapping(value = "/pending/{id}", params = "reject", method = RequestMethod.POST)
//    public String handleRejectPending(Model model, @PathVariable Long id) {
//        Score score = scoreSer
//        return "redirect:/";
//    }
}
