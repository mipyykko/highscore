/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.repository;

import com.mipyykko.highscore.domain.Game;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author pyykkomi
 */
public interface GameRepository extends JpaRepository<Game, Long> {
    
    @Query("select g from Game g where g.scores.size > 0 order by g.scores.size desc")
    List<Game> findMostPopularGames();
    Game findByName(String name);
    
}
