/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.config;

import com.mipyykko.highscore.domain.Game;
import com.mipyykko.highscore.domain.Player;
import com.mipyykko.highscore.domain.Score;
import com.mipyykko.highscore.service.GameService;
import com.mipyykko.highscore.service.PlayerService;
import com.mipyykko.highscore.service.ScoreService;
import java.util.Arrays;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 *
 * @author pyykkomi
 */
@Configuration
@Profile("default")
public class DefaultTestdataConfig extends TestdataConfig {
    
    @Autowired
    private PlayerService playerService;
    @Autowired
    private GameService gameService;
    @Autowired
    private ScoreService scoreService;
    
    @Bean
    @Override
    public boolean init() {
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
        
        game1.setScoreSortType(Game.ScoreSortType.HIGHEST);
        game2.setScoreSortType(Game.ScoreSortType.LOWEST);
        
        gameService.save(game1);
        gameService.save(game2);
        
        Score score1 = new Score(game1, test1, "12320", new Date(System.currentTimeMillis()));
        score1.setDescription("Nice!");
        score1.setStatus(Score.Status.ACCEPTED);
        Score score2 = new Score(game1, test2, "11000", new Date(System.currentTimeMillis() - 1000));
        score2.setStatus(Score.Status.ACCEPTED);
        Score score3 = new Score(game2, test2, "100000", new Date(System.currentTimeMillis() - 22222));
        score3.setStatus(Score.Status.ACCEPTED);
        Score score4 = new Score(game2, test1, "90000", new Date(System.currentTimeMillis() - 3333));
        Score score5 = new Score(game2, test2, "999999999", new Date(System.currentTimeMillis() + 555));
        score5.setDescription("This is a long descriptionThis is a long descriptionThis is a long descriptionThis is a long description");
        Score score6 = new Score(game1, test1, "112123323", new Date(System.currentTimeMillis() - 23434));
        score6.setDescription("I swear this is real!!!");

        scoreService.addScore(score1);
        scoreService.addScore(score2);
        scoreService.addScore(score3);
        scoreService.addScore(score4);
        scoreService.addScore(score5);
        scoreService.addScore(score6);
        
        return true;
    }
}
