/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 *
 * @author pyykkomi
 */
@Entity
public class Player extends AbstractPersistable<Long> {

    @Column(unique = true)
    private String username;
    private String password;
    private String name;
    private String email;
    private String description;
    private String salt;
    @OneToMany(mappedBy = "player", orphanRemoval = true, 
               fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Score> scores = new ArrayList<>();
//    @ManyToMany(targetEntity = Player.UserType.class, fetch = FetchType.EAGER,
//            cascade = {CascadeType.MERGE})
//    @Fetch(value = FetchMode.SUBSELECT)
    @Column(name = "usertype")
    @ElementCollection(targetClass = Player.UserType.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<UserType> userTypes = new ArrayList<>();
    
    public Player() {}
    
    public Player(String username, String password, String name, String email, String description) {
        this.username = username;
        setPassword(password);
        this.name = name;
        this.email = email;
        this.description = description;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.salt = BCrypt.gensalt();
        this.password = BCrypt.hashpw(password, this.salt);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
    }

    public List<UserType> getUserTypes() {
        return userTypes;
    }

    public void setUserTypes(List<UserType> userTypes) {
        this.userTypes = userTypes;
    }
    
    public boolean isUserType(UserType userType) {
        return userTypes.contains(userType);
    }
    
    public enum UserType {
        USER, ADMIN
    }
}
