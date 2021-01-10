package com.sociapp.backend.auth;

import com.sociapp.backend.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class Token {

    @Id
    private String token;

    @ManyToOne
    private User user;
}
