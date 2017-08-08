/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.service;

import com.mipyykko.highscore.domain.Player;
import com.mipyykko.highscore.repository.PlayerRepository;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 *
 * @author pyykkomi
 */
@Service
public class PlayerService {
    
    @Autowired
    private PlayerRepository playerRepository;

    public Player getAuthenticatedPlayer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return playerRepository.findByUsername(authentication.getName());
    }    
    
    @Transactional
    public Player save(Player player) {
        return playerRepository.save(player);
    }
    
    public List<Player> getMostActivePlayers() {
        return playerRepository.findMostActivePlayers();
    }
}
