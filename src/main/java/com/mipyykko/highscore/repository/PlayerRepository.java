/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.repository;

import com.mipyykko.highscore.domain.Player;
import java.util.List;
import java.util.Map.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author pyykkomi
 */
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findByUsername(String username);
    
    @Query("select p from Player p left join p.scores sc "
         + "where sc.status = 'ACCEPTED' group by p order by count(sc) desc")
//    @Query("select p from Player p where p.scores.size > 0 order by p.scores.size desc")
    List<Player> findMostActivePlayers();
}
