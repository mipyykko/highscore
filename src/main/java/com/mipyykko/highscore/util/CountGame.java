/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.util;

import com.mipyykko.highscore.domain.Game;

/**
 *
 * @author pyykkomi
 */
public class CountGame implements Comparable<CountGame> {
    Game game;
    Long count;
    
    protected CountGame(){}
    
    public CountGame(Game game, Long count) {
        this.game = game;
        this.count = count;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    public int compareTo(CountGame o) {
        return -this.count.compareTo(o.count);
    }
    
}
