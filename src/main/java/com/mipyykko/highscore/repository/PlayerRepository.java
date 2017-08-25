/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.repository;

import com.mipyykko.highscore.domain.Player;
import com.mipyykko.highscore.util.CountObject;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author pyykkomi
 */
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findByUsername(String username);
    
    @Query("select new com.mipyykko.highscore.util.CountObject(p, count(sc)) from Player p left join p.scores sc "
         + "where sc.status = 'ACCEPTED' group by p order by count(sc) desc")
    List<CountObject> findMostActivePlayers();
    @Query("select new com.mipyykko.highscore.util.CountObject(ps.game, count(ps.game)) from Player p "
        + "left join p.scores ps where ps.status = 'ACCEPTED' group by ps.game order by count(ps.game) desc")
    List<CountObject> findMostPopularGames();
}
