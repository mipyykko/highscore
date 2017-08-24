/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.util;

import com.mipyykko.highscore.domain.Score;

/**
 *
 * @author pyykkomi
 */
public class CountScore {
    Score score;
    String string;

    public CountScore(Score score, String string) {
        this.score = score;
        this.string = string;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
