/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.service;

import com.mipyykko.highscore.domain.Game;
import com.mipyykko.highscore.domain.Player;
import com.mipyykko.highscore.repository.PlayerRepository;
import com.mipyykko.highscore.util.CountObject;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        return authentication == null 
             ? null
             : playerRepository.findByUsername(authentication.getName());
    }    
    
    public boolean isAdmin() {
        Player player = getAuthenticatedPlayer();
        return player == null ? false : player.isUserType(Player.UserType.ADMIN);
    }
    
    public Player get(Long id) {
        return playerRepository.findOne(id);
    }
    
    @Transactional
    public Player save(Player player) {
        return playerRepository.save(player);
    }
    
    public Long playerCount() {
        return playerRepository.count();
    }
    
    public List<Game> getGamesByPlayer(Long id) {
        Player player = playerRepository.findOne(id);
        
        if (player == null) {
            return null;
        }
        
        return player.getScores().stream().map(score -> score.getGame()).distinct().collect(Collectors.toList());
    }
    public Map<Player, Long> getMostActivePlayers() {
        List<CountObject> players = playerRepository.findMostActivePlayers();
        Collections.sort(players);
        Map<Player, Long> map = new LinkedHashMap<>();
        players.forEach(cp -> map.put((Player) cp.getObject(), cp.getCount()));
        return map;
    }
    
    public Page<Player> getPlayers(int page, int length, String direction, String criteria) {
        Pageable pageable = new PageRequest(page, length > 0 ? length : 25, 
                direction.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                criteria); 
        return playerRepository.findAll(pageable);
    }
}
