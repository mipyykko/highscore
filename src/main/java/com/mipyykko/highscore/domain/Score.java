/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.domain;

import java.util.Comparator;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author pyykkomi
 */
@Entity
public class Score extends AbstractPersistable<Long> implements Comparable<Score> {
    
    @ManyToOne
    private Game game;
    @ManyToOne
    private Player player;
    @NotNull
    @NotEmpty(message = "Score can not be empty!")
    private String scoreValue;
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date scoreDate;
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date sentDate;
    // private Screenshot screenshot;
    @Enumerated(EnumType.STRING)
    private Status status;
    
    public Score() {
        this.status = Status.PENDING;
    }
    
    public Score(Game game, Player player, String scoreValue, Date scoreDate) {
        this();
        this.game = game;
        this.player = player;
        this.scoreValue = scoreValue;
        this.scoreDate = scoreDate;
        this.sentDate = new Date(System.currentTimeMillis());
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

    public String getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(String scoreValue) {
        this.scoreValue = scoreValue;
    }

    public Date getScoreDate() {
        return scoreDate;
    }

    public void setScoreDate(Date scoreDate) {
        this.scoreDate = scoreDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }
    
    public boolean isAccepted() {
        return this.status == Status.ACCEPTED;
    }

    public boolean isRejected() {
        return this.status == Status.REJECTED;
    }
    
    @Override
    public int compareTo(Score o) {
        return this.game.getScoreType() == Game.ScoreType.LOWEST
            ? Comparators.LOWEST.compare(this, o) 
            : Comparators.HIGHEST.compare(this, o);
    }
    
    public static class Comparators {
        public static Comparator<Score> HIGHEST = (Score o1, Score o2) -> o1.getScoreValue().compareTo(o2.getScoreValue());
        public static Comparator<Score> LOWEST = (Score o1, Score o2) -> o2.getScoreValue().compareTo(o1.getScoreValue());
    }
    
    public enum Status {
        PENDING, REJECTED, ACCEPTED
    }
}
