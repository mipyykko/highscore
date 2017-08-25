/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.config;

import com.mipyykko.highscore.domain.Player;
import com.mipyykko.highscore.service.GameService;
import com.mipyykko.highscore.service.PlayerService;
import com.mipyykko.highscore.service.ScoreService;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 *
 * @author pyykkomi
 */
@Configuration
@Profile("production")
public class ProductionTestdataConfig extends TestdataConfig {
    @Autowired
    private PlayerService playerService;
    @Autowired
    private GameService gameService;
    @Autowired
    private ScoreService scoreService;

    @Bean
    @Override
    public boolean init() {
        if (playerService.playerCount() > 0) {
            // only init at first deploy
            return false;
        }
        
        Player admin = new Player("admin", "password", "Administrator", "admin@mail.com", "Administrator");
        admin.setUserTypes(Arrays.asList(new Player.UserType[]{Player.UserType.USER, Player.UserType.ADMIN}));
        playerService.save(admin);
        
        return true;
    }
    
}
