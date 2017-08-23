/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.controller;

import com.mipyykko.highscore.config.TestdataConfig;
import com.mipyykko.highscore.controller.common.HeaderInfo;
import com.mipyykko.highscore.domain.Game;
import com.mipyykko.highscore.domain.Player;
//import com.mipyykko.highscore.domain.UserType;
//import com.mipyykko.highscore.repository.UserTypeRepository;
import com.mipyykko.highscore.service.GameService;
import com.mipyykko.highscore.service.PlayerService;
import com.mipyykko.highscore.service.ScoreService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

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
//    @Autowired
//    private UserTypeRepository userTypeRepository;
    @Autowired
    private HeaderInfo headerInfo;
    @Autowired
    private TestdataConfig testDataConfig;
    
    @PostConstruct
    public void init() {
        testDataConfig.init();
    }
    
    @ModelAttribute
    public void addHeaderAttributes(Model model) {
        model.addAllAttributes(headerInfo.getHeaderAttributes());
    }
    
    @RequestMapping(value = "/")
    public String index(Model model) {
        List<Game> games = gameService.getTopGames();
        Map<Game, Long> popularGames = new HashMap<>();
        games.stream().forEach((g) -> {
           popularGames.put(g,
                   g.getScores()
                        .stream()
                        .filter(score -> score.isAccepted())
                        .count());
        });
        model.addAttribute("popularGames", popularGames); 
        // should be number of distinct players per game but I can't be bovvered
        
        List<Player> players = playerService.getMostActivePlayers();
        Map<Player, Long> activePlayers = new HashMap<>();
        players.stream().forEach((p) -> {
            activePlayers.put(p,
                    p.getScores()
                            .stream()
                            .filter(score -> score.isAccepted())
                            .count());
        });
        model.addAttribute("activePlayers", activePlayers);
        
        return "index";
    }
}
