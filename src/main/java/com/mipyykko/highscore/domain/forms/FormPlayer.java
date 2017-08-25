/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mipyykko.highscore.domain.forms;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.ScriptAssert;

/**
 *
 * @author pyykkomi
 */
public class FormPlayer {

    @NotBlank
    @Length(min = 4, max = 24, message = "Username must be 4 to 24 letters!")
    private String username;
    @Length(max = 64, message = "Name must be 64 letters or less!")
    private String name;
    @NotBlank
    @Length(min = 8, max = 24, message = "Password must be 8 to 24 letters!")
    private String password;
    private String passwordagain;
    private String oldpassword;
    @Email
    private String email;
    private String description;

    public FormPlayer() {}

    public FormPlayer(String username, String name, String email, String description) {
        this.username = username;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordagain() {
        return passwordagain;
    }

    public void setPasswordagain(String passwordagain) {
        this.passwordagain = passwordagain;
    }

    public String getOldpassword() {
        return oldpassword;
    }

    public void setOldpassword(String oldpassword) {
        this.oldpassword = oldpassword;
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

    @ReportAsSingleViolation
    @Constraint(validatedBy = {})
    @Target({METHOD, FIELD, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    public @interface ValidPasswordValidation {

        String password();
        String passwordagain();
        
        String message() default "Enter same password twice!";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

    }
}
