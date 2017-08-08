/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.service;

import com.mipyykko.highscore.domain.Game;
import com.mipyykko.highscore.domain.Player;
import com.mipyykko.highscore.domain.Score;
import com.mipyykko.highscore.repository.GameRepository;
import com.mipyykko.highscore.repository.PlayerRepository;
import com.mipyykko.highscore.repository.ScoreRepository;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author pyykkomi
 */
@Controller
public class ScoreService {
    
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    
    @Transactional
    public Score addScore(Score score) {
        Game game = gameRepository.findByName(score.getGame().getName());
        Player player = playerRepository.findByUsername(score.getPlayer().getUsername());
        if (game == null || player == null) {
            return null;
        }
        
        scoreRepository.save(score);
        
        game.getScores().add(score);
        player.getScores().add(score);
        if (!player.getGames().contains(game)) {
            player.getGames().add(game);
        }
        gameRepository.save(game);
        playerRepository.save(player);
        
        return score;
    }
    
    public Score save(Score score) {
        return scoreRepository.save(score);
    }
}
