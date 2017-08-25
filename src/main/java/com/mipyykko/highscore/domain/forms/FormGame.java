/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.domain.forms;

import com.mipyykko.highscore.domain.Game;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author pyykkomi
 */
public class FormGame {

    private Long id;
    @NotBlank(message = "Name cannot be empty!")
    @Length(max = 128, message = "Name can be at most 128 characters")
    private String name;
    private String publisher;
    private String publishedYear;

    // for error
    private String uniqueness;
    
    public FormGame() {
        id = null;
    }
    
    public FormGame(Game game) {
        this.id = game.getId();
        this.name = game.getName();
        this.publisher = game.getPublisher();
        this.publishedYear = game.getPublishedYear();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(String publishedYear) {
        this.publishedYear = publishedYear;
    }

    public String getUniqueness() {
        return uniqueness;
    }

    public void setUniqueness(String uniqueness) {
        this.uniqueness = uniqueness;
    }
}
