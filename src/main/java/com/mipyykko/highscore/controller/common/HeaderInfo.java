/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.controller.common;

import com.mipyykko.highscore.domain.Player;
import com.mipyykko.highscore.domain.Score;
import com.mipyykko.highscore.service.PlayerService;
import com.mipyykko.highscore.service.ScoreService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author pyykkomi
 */
@Component
public class HeaderInfo {

    @Autowired
    private PlayerService playerService;
    @Autowired
    private ScoreService scoreService;
    
    public Map<String, Object> getHeaderAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        Player currentuser = playerService.getAuthenticatedPlayer();
        attributes.put("currentuser", currentuser);
        if (currentuser != null && currentuser.isUserType(Player.UserType.ADMIN)) { 
            List<Score> pendingScores = scoreService.getPending();
            attributes.put("pendingScores", pendingScores);
        }
        return attributes;
    }
}
