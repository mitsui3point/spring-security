package io.security.corespringsecurity.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {
    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String password;
    private String email;
    private String age;
    private String role;

    @Builder
    public Account(Long id, String username, String password, String email, String age, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
        this.role = role;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
