/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.controller;

import com.mipyykko.highscore.domain.Score;
import com.mipyykko.highscore.service.GameService;
import com.mipyykko.highscore.service.ScoreService;
import java.util.List;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author pyykkomi
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
public class ScoreControllerTest {
    
    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private ScoreController scoreController;
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private GameService gameService;
    
    private MockMvc mockMvc;
    
    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }
    
    @Test
    public void contextLoads() throws Exception {
        assertThat(scoreController).isNotNull();
    }

    @Test
    public void showScoreTest() throws Exception {
        mockMvc.perform(get("/scores/666"))
               .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/scores/1"))
               .andExpect(status().isOk())
               .andExpect(model().attributeExists("score"));
    }
    
    @Test
    public void showAddScoreTest() throws Exception {
        mockMvc.perform(get("/scores/add/1"))
               .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/scores/add/666").with(user("test1").password("password")))
               .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/scores/add/1").with(user("test1").password("password")))
               .andExpect(status().isOk())
               .andExpect(model().attributeExists("score"))
               .andExpect(content().string(containsString("Peli 1")));
    }
    
    @Test
    public void handleAddScoreTest() throws Exception {
        Score score = scoreService.get(1l);
        
        mockMvc.perform(post("/scores/add")
               .param("id", "1")
               .param("scoreValue", "666"))
               .andExpect(status().is3xxRedirection());
        mockMvc.perform(post("/scores/add").with(user("test1").password("password"))
               .param("id", "1")
               .param("scoreValue", "666")
               .param("scoreDate", "2099-11-11 00:00")
               .param("score", "1")
               .param("score.game", "1")
               .param("score.game.id", "1"))
               .andExpect(model().attributeHasFieldErrors("score", "scoreDate"));
        mockMvc.perform(post("/scores/add").with(user("test1").password("password"))
               .param("id", "1")
               .param("scoreValue", "666")
               .param("scoreDate", "")
               .param("score", "1")
               .param("score.game", "1")
               .param("score.game.id", "1"))
               .andExpect(status().isOk())
               .andExpect(model().attributeHasFieldErrors("score", "scoreDate"));
        mockMvc.perform(post("/scores/add").with(user("test1").password("password"))
               .param("id", "1")
               .param("scoreValue", "")
               .param("scoreDate", "2000-01-01 00:00")
               .param("score", "1")
               .param("score.game", "1")
               .param("score.game.id", "1"))
               .andExpect(model().attributeHasFieldErrors("score", "scoreValue"));
        mockMvc.perform(post("/scores/add").with(user("test1").password("password"))
               .param("id", "1")
               .param("scoreValue", "666")
               .param("scoreDate", "2000-01-01 00:00")
               .param("score", "7") // large enough to not overwrite existing test scores :x
               .param("score.game", "1")
               .param("score.game.id", "1"))
               .andDo(print())
               .andExpect(status().is3xxRedirection())
               .andExpect(model().hasNoErrors());
        
        // nah!
//        List<Score> scores = scoreService.findByGame(gameService.get(1l));
//        boolean found = false;
//        for (Score s : scores) {
//            if (s.getScoreValue().equals("666")) {
//                found = true;
//                break;
//            }
//        }
//        assertTrue(found);
    }
    
    @Test
    public void testGetPending() throws Exception {
        mockMvc.perform(get("/scores/pending"))
               .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/scores/pending").with(user("test1").password("password")))
               .andExpect(status().is3xxRedirection());
        
    } 
}
