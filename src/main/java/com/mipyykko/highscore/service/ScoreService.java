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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
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
        gameRepository.save(game);
        playerRepository.save(player);

        return score;
    }

    @Transactional
    public Score save(Score score) {
        return scoreRepository.save(score);
    }

    public List<Score> getTopScoresByPlayer(Long id) {
        Player player = playerRepository.findOne(id);
        List<Score> topScores = new ArrayList<>();

        if (player != null) {
            Set<Game> playerGames = new TreeSet<>();
            player.getScores().stream().forEach((score) -> {
                playerGames.add(score.getGame());
            });
            playerGames.stream().map((game) -> {
                Collections.sort(game.getScores());
                return game;
            }).filter((game) -> 
                      (game.getScores() != null 
                    && game.getScores().get(0).getPlayer() == player))
              .forEach((game) -> {
                topScores.add(game.getScores().get(0));
            });
        }
        return topScores;
    }
    
    public List<Score> findByGame(Game game) {
        return scoreRepository.findByGame(game);
    }
}
