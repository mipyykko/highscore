/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.repository;

import com.mipyykko.highscore.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author pyykkomi
 */
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findByUsername(String username);
}
