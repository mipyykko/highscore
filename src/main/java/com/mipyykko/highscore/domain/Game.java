/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 *
 * @author pyykkomi
 */
@Entity
public class Game extends AbstractPersistable<Long> {
    
    @Column(unique = true)
    private String name;
    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Score> scores = new ArrayList<>();
    private ScoreType scoreType;
    
    public Game() {
        this.scoreType = ScoreType.HIGHEST;
    }
    
    public Game(String name) {
        this();
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
    }
    
    public enum ScoreType {
        HIGHEST, LOWEST
    }
}
