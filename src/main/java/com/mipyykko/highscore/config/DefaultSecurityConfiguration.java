/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.config;

import com.mipyykko.highscore.auth.JpaAuthenticationProvider;
//import com.mipyykko.highscore.domain.UserType;
import com.mipyykko.highscore.repository.PlayerRepository;
//import com.mipyykko.highscore.repository.UserTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 *
 * @author pyykkomi
 */
@Profile("default")
@Configuration
@PropertySource("application.properties")
@EnableWebSecurity
public class DefaultSecurityConfiguration extends WebSecurityConfigurerAdapter {
    
    @Autowired
    private PlayerRepository playerRepository;
//    @Autowired
//    private UserTypeRepository userTypeRepository;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        // sallitaan framejen käyttö
        http.headers().frameOptions().sameOrigin();

        http.authorizeRequests()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/scores/pending*/**").hasAuthority("ADMIN")
                .antMatchers("/", "/login", "/logout", "/register", "/scores*/**", "/static*/**", "/css/**", "/js/**", "/images/**").permitAll()
                .antMatchers(HttpMethod.GET, "/", "/players*/**" ,"/games*/**").permitAll()
                .anyRequest().authenticated();
        http.formLogin()
                .loginPage("/")
                .loginProcessingUrl("/authenticate")
                .defaultSuccessUrl("/")
                .failureForwardUrl("/") // ?error
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll();
        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
                .invalidateHttpSession(true);    
    }
    
    @Configuration
    protected static class AuthenticationConfiguration extends GlobalAuthenticationConfigurerAdapter {

        @Autowired
        private JpaAuthenticationProvider jpaAuthenticationProvider;

        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(jpaAuthenticationProvider);
        }
    }    
}
