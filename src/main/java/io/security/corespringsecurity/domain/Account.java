package io.security.corespringsecurity.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@EqualsAndHashCode
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
}
