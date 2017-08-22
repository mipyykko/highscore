/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.controller;

import com.mipyykko.highscore.domain.Player;
import com.mipyykko.highscore.service.PlayerService;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

/**
 *
 * @author pyykkomi
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest
public class GameControllerTest {

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private GameController gameController;
    
    @Autowired
    private PlayerService playerService;
    
    private MockMvc mockMvc;
    
    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    public void contextLoads() throws Exception {
        assertThat(gameController).isNotNull();
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
}
