/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.domain;

import java.util.Comparator;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 *
 * @author pyykkomi
 */
@Entity
public class Score extends AbstractPersistable<Long> implements Comparable<Score> {
    
    @ManyToOne(fetch = FetchType.EAGER, 
              cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE})
    private Game game;
    @ManyToOne(fetch = FetchType.EAGER)
    private Player player;
    private String score;
    @Temporal(TemporalType.DATE)
    private Date date;
    // private Screenshot screenshot;
    private Status status;
    
    public Score() {
        this.status = Status.PENDING;
    }
    
    public Score(Game game, Player player, String score, Date date) {
        this();
        this.game = game;
        this.player = player;
        this.score = score;
        this.date = date;
    }
    
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public int compareTo(Score o) {
        return this.game.getScoreType() == Game.ScoreType.LOWEST
            ? Comparators.LOWEST.compare(this, o) 
            : Comparators.HIGHEST.compare(this, o);
    }
    
    public static class Comparators {
        public static Comparator<Score> HIGHEST = (Score o1, Score o2) -> o1.getScore().compareTo(o2.getScore());
        public static Comparator<Score> LOWEST = (Score o1, Score o2) -> o2.getScore().compareTo(o1.getScore());
    }
    
    public enum Status {
        PENDING, REJECTED, ACCEPTED
    }
}
