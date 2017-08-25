/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.controller;

import com.mipyykko.highscore.service.PlayerService;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest
public class DefaultControllerTest {

    @Autowired
    private WebApplicationContext webAppContext;
    @Autowired
    private FilterChainProxy springSecurityFilterChain;
    
    @Autowired
    private DefaultController defaultController;
    @Autowired
    private PlayerService playerService;
    
    private MockMvc mockMvc;
    
    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webAppContext)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    @Test
    public void contextLoads() throws Exception {
        assertThat(defaultController).isNotNull();
    }
    
    @Test
    public void indexTest() throws Exception {
        mockMvc.perform(get("/"))
               .andExpect(model().attribute("currentuser", Matchers.isNull()))
               .andExpect(model().attributeDoesNotExist("pendingScores"))
               .andExpect(content().string(containsString("Most active")));
        mockMvc.perform(get("/").with(user("admin").password("password").roles("USER", "ADMIN")))
               .andExpect(model().attributeExists("popularGames"))
               .andExpect(model().attributeExists("activePlayers"))
               .andExpect(model().attributeExists("currentuser"))
               .andExpect(model().attributeExists("pendingScores"));
    }

    /**/
//    @Test
//    public void loginTest() throws Exception {
//        mockMvc.perform(formLogin("/").user("fake").password("user"))
//               .andExpect(model().attribute("currentuser", Matchers.isNull()));
//        mockMvc.perform(post("/authenticate")
//                    .param("username", "admin")
//                    .param("password", "password"))
//               .andDo(print())
//               .andExpect(status().is3xxRedirection());
//        mockMvc.perform(get("/"))
//               .andExpect(model().attribute("currentuser", playerService.get(1l)));
//        mockMvc.perform(logout())
//               .andExpect(status().isOk())
//               .andExpect(model().attribute("currentuser", Matchers.isNull()));
//    }
    
}
