/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.service;

import com.mipyykko.highscore.auth.JpaAuthenticationProvider;
import com.mipyykko.highscore.domain.Game;
import com.mipyykko.highscore.domain.Player;
import com.mipyykko.highscore.domain.Score;
import com.mipyykko.highscore.repository.GameRepository;
import com.mipyykko.highscore.repository.PlayerRepository;
import com.mipyykko.highscore.repository.ScoreRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author pyykkomi
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PlayerServiceTest {
    
    @Autowired
    public PlayerService playerService;
    @Autowired
    public GameService gameService;
    @Autowired
    public ScoreService scoreService;
    
    @Autowired
    public PlayerRepository playerRepository;
    @Autowired
    public GameRepository gameRepository;
    @Autowired
    public ScoreRepository scoreRepository;
    @Autowired
    public JpaAuthenticationProvider authProvider;
    
    Player player1, player2;
    Game game1, game2;
    Score score1, score2, score3, score4, score5;
    
    public PlayerServiceTest() {
    }
    
    @Before
    public void setUp() {
        flushTestData();
        MockitoAnnotations.initMocks(this);
    }
    
    @After
    public void tearDown() {
    }

    private void addTestData() {
        player1 = new Player("test1", "password", "Test1", "test1@mail.com", "test description");
        player2 = new Player("test2", "password", "Test2", "test2@mail.com", "test description");
        player1 = playerService.save(player1);
        player2 = playerService.save(player2);
        
        game1 = new Game("test1", "test1", "1111");
        game2 = new Game("test2", "test2", "1112");
        game1 = gameService.save(game1);
        game2 = gameService.save(game2);
        
        score1 = new Score(game1, player1, "555", null);
        score2 = new Score(game2, player1, "666", null);
        score3 = new Score(game1, player2, "777", null);
        score4 = new Score(game1, player1, "222", null);
        score5 = new Score(game2, player2, "5555", null);

        score1.setStatus(Score.Status.ACCEPTED);
        score2.setStatus(Score.Status.ACCEPTED);
        score3.setStatus(Score.Status.ACCEPTED);
        score4.setStatus(Score.Status.ACCEPTED);
        
        score1 = scoreService.save(score1);
        score2 = scoreService.save(score2);
        score3 = scoreService.save(score3);
        score4 = scoreService.save(score4);
        score5 = scoreService.save(score5);
    }
    
    private void flushTestData() {
        scoreRepository.deleteAll();
        gameRepository.deleteAll();
        playerRepository.deleteAll();
        player1 = null;
        game1 = null;
        game2 = null;
        score1 = null;
        score2 = null;
    }
    
    @Test
    public void testGetNonAuthenticatedPlayer() {
        Player nonAuth = playerService.getAuthenticatedPlayer();
        assertNull(nonAuth);
    }

    @Test
    public void testGetAuthenticatedPlayer() {
        Player p1 = new Player("auth", "password", "Authorized", "auth@auth.com", "authorized");
        p1.setUserTypes(Arrays.asList(new Player.UserType[]{Player.UserType.USER}));
        p1 = playerService.save(p1);
  
        try {
            Thread.sleep(1000);
        } catch (Exception ex) {
        }
        
        Authentication auth = new UsernamePasswordAuthenticationToken("auth", "password");
        auth = authProvider.authenticate(auth);
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        Player yesAuth = playerService.getAuthenticatedPlayer();
        assertEquals("auth", yesAuth.getUsername());
        
        playerRepository.delete(p1);
    }
    
    @Test
    public void testGet() {
        addTestData();
        Player t1 = playerService.get(player1.getId());
        assertEquals(t1.getName(), player1.getName());
        flushTestData();
    }

    @Test
    public void testSave() {
        Player test1 = new Player("test1", "password", "Test1", "test1@mail.com", "test description");
        test1.setUserTypes(Arrays.asList(new Player.UserType[]{Player.UserType.USER}));
        test1 = playerService.save(test1);
        
        assertEquals(test1.getId(), playerRepository.findOne(test1.getId()).getId());
        assertEquals(test1.getId(), playerRepository.findByUsername("test1").getId());
        
        playerRepository.delete(test1);
        assertNull(playerRepository.findOne(test1.getId()));
    }

    @Test
    public void testGetGamesByPlayer() {
        assertNull(playerService.getGamesByPlayer(6666666l));
        
        addTestData();
        List<Game> games = playerService.getGamesByPlayer(player1.getId());
        assertEquals(2, games.size());
        flushTestData();
    }

    @Test
    public void testGetMostActivePlayers() {
        addTestData();
        List<Player> players = playerService.getMostActivePlayers();
        assertNotNull(players);
        assertEquals(players.get(0).getId(), player1.getId());
        assertEquals(players.get(1).getId(), player2.getId());
        flushTestData();
    }

    @Test
    public void testGetPlayers() {
        addTestData();
        List<Player> players = playerService.getPlayers();
        assertEquals(2, players.size());
        flushTestData();
    }
    
}
