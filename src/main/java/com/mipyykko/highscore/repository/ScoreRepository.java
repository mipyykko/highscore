/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.repository;

import com.mipyykko.highscore.domain.Game;
import com.mipyykko.highscore.domain.Score;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author pyykkomi
 */
public interface ScoreRepository extends JpaRepository<Score, Long> {
    Long deleteByGame(Game game);
    List<Score> findByGame(Game game);
}
