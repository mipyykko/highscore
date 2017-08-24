/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.util;

import com.mipyykko.highscore.domain.Player;

/**
 *
 * @author pyykkomi
 */
public class CountPlayer implements Comparable<CountPlayer> {
    Player player;
    Long count;

    protected CountPlayer() {}
    
    public CountPlayer(Player player, Long count) {
        this.player = player;
        this.count = count;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    public int compareTo(CountPlayer o) {
        return -this.count.compareTo(o.count);
    }

    
}
