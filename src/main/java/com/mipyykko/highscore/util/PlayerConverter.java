/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.util;

import com.mipyykko.highscore.domain.Player;
import com.mipyykko.highscore.domain.forms.FormPlayer;

/**
 *
 * @author pyykkomi
 */
public class PlayerConverter {
    
    public static Player toPlayer(FormPlayer formPlayer) {
        if (formPlayer == null) { 
            return null;
        }
        
        Player player = new Player(formPlayer.getUsername(), formPlayer.getPassword(),
                                   formPlayer.getName(), formPlayer.getEmail(),
                                   formPlayer.getDescription());
        player.getUserTypes().add(Player.UserType.USER);
        return player;
    }
    
    public static FormPlayer toForm(Player player) {
        if (player == null) {
            return null;
        }
        
        FormPlayer formPlayer = 
                new FormPlayer(player.getUsername(), player.getName(),
                                      player.getEmail(), player.getDescription());
        formPlayer.setId(player.getId());
        return formPlayer;
    }
}
