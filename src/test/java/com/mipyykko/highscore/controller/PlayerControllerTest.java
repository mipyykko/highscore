/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.controller;

import com.mipyykko.highscore.domain.Player;
import com.mipyykko.highscore.service.PlayerService;
import java.util.Map;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
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
public class PlayerControllerTest {
    
    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private PlayerController playerController;
    @Autowired
    private PlayerService playerService;
    
    private MockMvc mockMvc;
    
    public PlayerControllerTest() {
    }
    
    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }
    
    @Test
    public void contextLoads() throws Exception {
        assertThat(playerController).isNotNull();
    }

    @Test
    public void showRegisterTest() throws Exception {
        mockMvc.perform(get("/register"))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("Register")));
        mockMvc.perform(get("/register").with(user("test1").password("password")))
               .andExpect(status().is3xxRedirection());
    }
    
    @Test
    public void handleRegisterTest() throws Exception {
        mockMvc.perform(post("/register")
                .param("username", "foo")
                .param("password", "password")
                .param("passwordagain", "wrong"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("formPlayer", "passwordagain"));
        mockMvc.perform(post("/register")
                .param("username", "foobar")
                .param("password", "password")
                .param("passwordagain", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().hasNoErrors());
        
        Player p = playerService.getByUsername("foobar");
        assertNotNull(p);
        assertEquals("foobar", p.getUsername());
    }
    
    @Test
    public void testGetProfile() throws Exception {
        mockMvc.perform(get("/profile"))
               .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/profile").with(user("test1").password("password")))
               .andExpect(status().isOk())
               .andExpect(model().attributeExists("highscores"))
               .andExpect(model().attributeExists("player"))
               .andExpect(model().attributeExists("games"))
               .andExpect(content().string(containsString("test1")));
        mockMvc.perform(get("/profile/666"))
               .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/profile/1"))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("admin")));
    }
    
    @Test
    public void testEditProfile() throws Exception {
        mockMvc.perform(get("/profile/edit"))
               .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/profile/edit").with(user("test1").password("password")))
               .andExpect(status().isOk())
               .andExpect(model().attributeExists("formPlayer"))
               .andExpect(content().string(containsString("test1")));
    }
    
    @Test
    public void testHandleEditProfile() throws Exception {
        mockMvc.perform(post("/profile/edit"))
               .andExpect(status().is3xxRedirection());
        mockMvc.perform(post("/profile/edit").with(user("test1").password("password"))
               .param("id", "2")
               .param("username", "admin")
               .param("password", ""))
               .andExpect(status().isOk())
               .andExpect(model().attributeHasFieldErrors("formPlayer", "username"));
        mockMvc.perform(post("/profile/edit").with(user("test1").password("password"))
               .param("id", "2")
               .param("username", "test1")
               .param("password", "")
               .param("oldpassword", "false"))
               .andExpect(status().isOk())
               .andExpect(model().attributeHasFieldErrors("formPlayer", "oldpassword"));
        mockMvc.perform(post("/profile/edit").with(user("test1").password("password"))
               .param("id", "2")
               .param("username", "test1")
               .param("oldpassword", "password")
               .param("password", "passwordy")
               .param("newpassword", "passwordz"))
               .andExpect(status().isOk())
               .andExpect(model().attributeHasFieldErrors("formPlayer", "passwordagain"));
        mockMvc.perform(post("/profile/edit").with(user("test1").password("password"))
               .param("id", "2")
               .param("username", "test1")
               .param("password", ""))
               .andExpect(status().is3xxRedirection())
               .andExpect(model().hasNoErrors());
        mockMvc.perform(post("/profile/edit").with(user("test1").password("password"))
               .param("id", "2")
               .param("username", "test1")
               .param("oldpassword", "password")
               .param("password", "passwordz")
               .param("passwordagain", "passwordz"))
               .andExpect(status().is3xxRedirection())
               .andExpect(model().hasNoErrors());
        
        Player p = playerService.getByUsername("test1");
        assertEquals(BCrypt.hashpw("passwordz", p.getSalt()), p.getPassword());
        p.setPassword("password");
        playerService.save(p);
    }
    
    @Test
    public void testGetPlayers() throws Exception {
        Map<String, Object> m = mockMvc.perform(get("/players")
               .param("length", "1"))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("admin")))
               .andReturn().getModelAndView().getModel();
        
        assertTrue(m.containsKey("players"));
        assertEquals(1, ((Map<Player, Long>) m.get("players")).size());
        
    }
}
