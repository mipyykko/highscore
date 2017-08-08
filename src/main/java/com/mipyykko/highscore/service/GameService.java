/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.service;

import com.mipyykko.highscore.domain.Game;
import com.mipyykko.highscore.repository.GameRepository;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author pyykkomi
 */
@Service
public class GameService {
    
    @Autowired
    private GameRepository gameRepository;
    
    public List<Game> getTopGames() {
        return gameRepository.findMostPopularGames();
    }
    
    @Transactional
    public Game save(Game game) {
        return gameRepository.save(game);
    }
}
