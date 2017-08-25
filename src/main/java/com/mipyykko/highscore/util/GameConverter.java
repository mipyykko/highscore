/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.util;

import com.mipyykko.highscore.domain.forms.FormGame;
import com.mipyykko.highscore.domain.Game;
import com.mipyykko.highscore.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author pyykkomi
 */
@Component
public class GameConverter {

    @Autowired
    private GameService gameService;
    
    public Game toGame(FormGame formGame) {
        if (formGame == null) {
            return null;
        }

        Game game;
        if (formGame.getId() == null) {
            game = new Game();
        } else {
            game = gameService.get(formGame.getId());
        }
        
        game.setName(formGame.getName());
        game.setPublisher(formGame.getPublisher());
        game.setPublishedYear(formGame.getPublishedYear());

        return game;
    }
    
    public FormGame toFormGame(Game game) {
        if (game == null) {
            return null;
        }
        
        FormGame formGame = new FormGame(game);
        formGame.setId(game.getId());
        return formGame;
    }
}
