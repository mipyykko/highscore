/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.service;

import com.mipyykko.highscore.domain.Game;
import com.mipyykko.highscore.repository.GameRepository;
import com.mipyykko.highscore.repository.ScoreRepository;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 *
 * @author pyykkomi
 */
@Service
public class GameService {
    
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    
    public Game get(Long id) {
        return gameRepository.findOne(id);
    }
    
    public List<Game> getTopGames() {
        return gameRepository.findMostPopularGames();
    }
    
    public Page<Game> getGames(int page, int length, String direction, String criteria) {
        Pageable pageable = new PageRequest(page, 
                length > 0 ? length : 25, 
                direction.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, 
                criteria);
        return gameRepository.findAll(pageable);
    }
    
    public Game findSimilar(Game game) {
        return gameRepository.findByNameAndPublisherAndPublishedYear(game.getName(), game.getPublisher(), game.getPublishedYear());
    }
    
    public Game findByName(String name) {
        return gameRepository.findByName(name);
    }
    
    @Transactional
    public Game save(Game game) {
        return gameRepository.save(game);
    }
    
    @Transactional
    public Game delete(Game game) {
        gameRepository.delete(game);
        game.setScores(null);
        scoreRepository.deleteByGame(game);
        return game;
    }
}
