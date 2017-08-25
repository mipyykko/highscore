/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.controller;

import com.mipyykko.highscore.domain.Game;
import com.mipyykko.highscore.domain.Score;
import com.mipyykko.highscore.domain.forms.FormGame;
import com.mipyykko.highscore.service.GameService;
import com.mipyykko.highscore.service.PlayerService;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 *
 * @author pyykkomi
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
//@WebMvcTest(GameController.class)
public class GameControllerTest {

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private GameController gameController;
    
    @Autowired
    private PlayerService playerService;
    @Autowired
    private GameService gameService;

    private MockMvc mockMvc;
    
    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    public void contextLoads() throws Exception {
        assertThat(gameController).isNotNull();
    }
    
    private MockHttpServletRequestBuilder postAnonymousGamesForm(
            String name, String publisher, String publishedYear) {
        return post("/games/add").sessionAttr("game", new Game())
                .param("name", name)
                .param("publisher", publisher)
                .param("publishedYear", publishedYear)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_FORM_URLENCODED);
    }
    
    private MockHttpServletRequestBuilder postLoggedInGamesForm(
            String name, String publisher, String publishedYear, String user, String password) {
        return post("/games/add").sessionAttr("game", new Game()).with(user(user).password(password))
                .param("name", name)
                .param("publisher", publisher)
                .param("publishedYear", publishedYear)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_FORM_URLENCODED);
    }

    @Test
    public void gamesTest() throws Exception {
        mockMvc.perform(get("/games"))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("Peli 1")))
               .andExpect(model().attributeExists("games"))
               .andExpect(model().attributeDoesNotExist("pendingScores"))
               .andExpect(model().attribute("currentuser", Matchers.isNull()));
    
        mockMvc.perform(get("/games/2"))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("Peli 2")))
               .andExpect(content().string(containsString("100000")))
               .andExpect(content().string(not(containsString("999999999"))))
               .andExpect(model().attributeExists("game"))
               .andExpect(model().attributeDoesNotExist("pendingScores"))
               .andExpect(model().attribute("currentuser", Matchers.isNull()));
        
        mockMvc.perform(get("/games/2").with(user("test2").password("password")))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("999999999")))
               .andExpect(model().attributeDoesNotExist("pendingScores"))
               .andExpect(model().attribute("currentuser", playerService.get(3l)));
        
        mockMvc.perform(get("/games/2").with(user("admin").password("password")))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("999999999")))
               .andExpect(model().attributeExists("pendingScores"))
               .andExpect(model().attribute("currentuser", playerService.get(1l)));
    }
    
//    @Test
//    public void testSave() throws Exception {
//        mockMvc.perform(post("/games"))
//               .andDo(print())
//               .andExpect(status().is4xxClientError());
//        mockMvc.perform(post("/games/666").with(user("test1").password("password")))
//               .andExpect(status().is4xxClientError());
//        mockMvc.perform(post("/games/").with(user("test1").password("password")))
//               .andExpect(status().is3xxRedirection());
//        mockMvc.perform(post("/games/")
//                    .param("name", "testi4")
//                    .param("publisher", "publisher")
//                    .param("year", "year").with(user("test1").password("password")))
//               .andDo(print())
//               .andExpect(status().is3xxRedirection());
//        mockMvc.perform(post("/games/")
//                    .param("name", "testi5")
//                    .param("publisher", "publisher")
//                    .param("year", "year"))
//               .andDo(print())
//               .andExpect(status().is3xxRedirection());
//     
//        assertNotNull(gameService.findByName("testi4"));
//        assertEquals("testi4", gameService.findByName("testi4").getName());
//        assertNull(gameService.findByName("testi5"));
//    }
    
    @Test
    public void testGetGame() throws Exception {
        mockMvc.perform(get("/games/999"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/games/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("game"))
                .andExpect(model().attributeExists("scores"))
                .andExpect(content().string(containsString("Peli 1")))
                .andExpect(content().string(not(containsString("112123323"))))
                .andExpect(content().string(containsString("11000")));
        mockMvc.perform(get("/games/1").with(user("admin").password("password")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("112123323")));
        mockMvc.perform(get("/games/1").with(user("test1").password("password")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("112123323")));
        mockMvc.perform(get("/games/1").with(user("test2").password("password")))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("112123323"))));

        Map<String, Object> model = mockMvc.perform(get("/games/1")).andReturn().getModelAndView().getModel();
        assertTrue(model.containsKey("scores"));
        List<Score> scores = (List<Score>) model.get("scores");
        assertFalse(scores.isEmpty());
        assertEquals("12320", scores.get(0).getScoreValue());
        assertEquals("11000", scores.get(1).getScoreValue());
    }
    
    @Test
    public void testShowAddGame() throws Exception {
        mockMvc.perform(get("/games/add").sessionAttr("game", new Game()))
                .andExpect(status().is3xxRedirection());
        Map<String, Object> model = 
            mockMvc.perform(get("/games/add").sessionAttr("game", new Game()).with(user("test1").password("password")))
                .andExpect(status().isOk())
                .andReturn().getModelAndView().getModel();

        assertTrue(model.containsKey("formGame"));
        assertNotNull(model.get("formGame"));
        assertTrue(model.get("formGame") instanceof FormGame);
        
        Game game = gameService.get(1l);
        
//        model = mockMvc.perform(get("/games/add")
//                    .param("game.Id", Long.toString(game.getId()))
//                    .param("game.name", game.getName())
//                    .param("game.publisher", game.getPublisher())
//                    .param("game.publishedYear", game.getPublishedYear()))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn().getModelAndView().getModel();
//        assertNotNull(model.get("game"));
//        Game game2 = (Game) model.get("game");
//        
//        assertEquals("Peli 1", game2.getName());
    }
    
    @Test
    public void testHandleAddGame() throws Exception {
        Game game1 = gameService.get(1l);
        mockMvc.perform(postAnonymousGamesForm("anything", "", ""))
            .andDo(print())
            .andExpect(status().is3xxRedirection());
        
        assertNull(gameService.findByName("anything"));
        
        mockMvc.perform(postLoggedInGamesForm(
                game1.getName(), game1.getPublisher(), game1.getPublishedYear(), 
                "admin", "password"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(model().attributeHasFieldErrors("formGame", "uniqueness"));

        mockMvc.perform(postLoggedInGamesForm("", "", "", "admin", "password"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(model().attributeHasFieldErrors("formGame", "name"));

        mockMvc.perform(postLoggedInGamesForm("acceptable", "", "", "admin", "password"))
            .andExpect(status().is3xxRedirection())
            .andExpect(model().hasNoErrors());
        
        assertNotNull(gameService.findByName("acceptable"));
    }
}
