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
    
    public Game get(Long id) {
        return gameRepository.findOne(id);
    }
    
    public List<Game> getTopGames() {
        return gameRepository.findMostPopularGames();
    }
    
    public Page<Game> getGames(int start, int length, String direction, String criteria) {
        Pageable pageable = new PageRequest(start, 
                length > 0 ? start + length : start + 25, 
                direction.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, 
                criteria);
        return gameRepository.findAll(pageable);
    }
    
    @Transactional
    public Game save(Game game) {
        return gameRepository.save(game);
    }
}
