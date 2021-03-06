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
import org.hibernate.validator.constraints.Length;
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
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:ss")
    private Date sentDate;
    @Length(max = 255, message = "Description can be at most 255 characters!")
    private String description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isAccepted() {
        return this.status == Status.ACCEPTED;
    }

    public boolean isRejected() {
        return this.status == Status.REJECTED;
    }
    
    @Override
    public int compareTo(Score o) {
        return this.game.getScoreSortType() == Game.ScoreSortType.LOWEST
            ? Comparators.LOWEST.compare(this, o) 
            : Comparators.HIGHEST.compare(this, o);
    }
    
    public static class Comparators {
        public static Comparator<Score> LOWEST = (Score o1, Score o2) -> {
            if (!o1.getGame().getScoreType().equals(o2.getGame().getScoreType())) {
                return 0;
            }
            
            switch (o1.getGame().getScoreType()) {
                case LONG:
                    Long o1score = Long.parseLong(o1.getScoreValue());
                    Long o2score = Long.parseLong(o2.getScoreValue());
                    return o1score.compareTo(o2score);
                default:
                    return o1.getScoreValue().compareTo(o2.getScoreValue());
            }
        };
        public static Comparator<Score> HIGHEST = (Score o1, Score o2) -> {
            switch (o1.getGame().getScoreType()) {
                case LONG:
                    Long o1score = Long.parseLong(o1.getScoreValue());
                    Long o2score = Long.parseLong(o2.getScoreValue());
                    return -o1score.compareTo(o2score);
                default:
                    return -o1.getScoreValue().compareTo(o2.getScoreValue());
            }
        };
    }
    
    public enum Status {
        PENDING, REJECTED, ACCEPTED
    }
}
