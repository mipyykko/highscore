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
import java.util.Map;
import javax.annotation.PostConstruct;
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
        Map<Game, Long> popularGames = gameService.getPopularGames();
        model.addAttribute("popularGames", popularGames); 
        // should be number of distinct players per game but I can't be bovvered

        Map<Player, Long> activePlayers = playerService.getMostActivePlayers();
        model.addAttribute("activePlayers", activePlayers);
        
        return "index";
    }
}
