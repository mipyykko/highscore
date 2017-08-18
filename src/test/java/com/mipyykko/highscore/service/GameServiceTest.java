/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.service;

import com.mipyykko.highscore.domain.Game;
import com.mipyykko.highscore.domain.Player;
import com.mipyykko.highscore.domain.Score;
import com.mipyykko.highscore.repository.GameRepository;
import com.mipyykko.highscore.repository.PlayerRepository;
import com.mipyykko.highscore.repository.ScoreRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author pyykkomi
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GameServiceTest {

    @Autowired
    public GameService gameService;
    @Autowired
    public ScoreService scoreService;
    @Autowired
    public PlayerService playerService;

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    
    public GameServiceTest() {
    }
    
    @Before
    public void setUp() {
        List<Game> games = gameRepository.findAll();
        if (games != null) {
            for (Game g : games) {
                System.out.println("deleting " + g);
                gameService.delete(g);
            }
        }
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSaveAndDelete() {
        Game game = new Game("test1", "publisher1", "1999");
        gameService.save(game);

        Game repGame = gameRepository.findByName("test1");
        assertNotNull(repGame);
        assertEquals("test1", repGame.getName());

        gameService.delete(game);
        repGame = gameRepository.findByName("test1");
        assertNull(repGame);
    }

    @Test
    public void testGetById() {
        Game game = new Game("test2", "publisher2", "1999");
        game = gameService.save(game);

        Game repGame = gameService.get(game.getId());
        assertNotNull(repGame);
        assertEquals("test2", repGame.getName());

        gameService.delete(game);
    }

    @Test
    public void testGetPopular() {
        Player dummy1 = new Player("user1", "password", "", "", "");
        playerService.save(dummy1);

        Game game1 = new Game("test3", "Game Company", "1998");
        Game game2 = new Game("test4", "Game Company", "2012");

        gameService.save(game1);
        gameService.save(game2);

        Score score1 = new Score(game1, dummy1, "12320", new Date(System.currentTimeMillis()));
        Score score2 = new Score(game1, dummy1, "11000", new Date(System.currentTimeMillis() - 1000));
        Score score3 = new Score(game2, dummy1, "100000", new Date(System.currentTimeMillis() - 22222));

        scoreService.addScore(score1);
        scoreService.addScore(score2);
        scoreService.addScore(score3);

        List<Game> games = gameService.getTopGames();
        assertNotNull(games);
        assertEquals(2, games.size());
        assertEquals("test3", games.get(0).getName());

        gameService.delete(game1);
        gameService.delete(game2);

        assertTrue(scoreService.findByGame(game1).isEmpty());
    }

    @Test
    public void testGetGames() {
        List<Game> savedGames = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Game g = new Game("" + (char) ('A' + i), "abc", "" + (char) ('9' - i));
            savedGames.add(g);
            gameService.save(g);
        }

        Page<Game> games = gameService.getGames(0, 5, "asc", "name");
        assertNotNull(games);
        assertEquals(5, games.getContent().size());
        assertEquals("A", games.getContent().get(0).getName());
        assertEquals(2, games.getTotalPages()); // twelve entries because of the test data :x

        Page<Game> games2 = gameService.getGames(0, 10, "desc", "publishedYear");
        assertNotNull(games2);
        assertEquals(10, games2.getContent().size());
        assertEquals("9", games2.getContent().get(0).getPublishedYear());
        assertEquals(1, games2.getTotalPages());

        savedGames.stream().forEach((g) -> gameService.delete(g));
        assertTrue(gameService.getTopGames().isEmpty());
    }
    
    @Test
    public void testFindSimilar() {
        Game game1 = new Game("test4", "Game Company", "1998");
        game1 = gameService.save(game1);
        
        Game game2 = new Game("test4", "Game Company", "1998");
        Game game3 = new Game("not equal", "Game Company", "1998");
        Game game4 = new Game("test4", "not equal", "1998");
        Game game5 = new Game("test4", "Game Company", "not equal");
        
        assertNotNull(gameService.findSimilar(game2));

        assertEquals(gameService.findSimilar(game2).getName(), game1.getName());
        
        assertNull(gameService.findSimilar(game3));
        assertNull(gameService.findSimilar(game4));
        assertNull(gameService.findSimilar(game5));

        gameRepository.delete(game1);
    }
}
