package io.security.corespringsecurity.domain;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountDto {

    private String username;
    private String password;
    private String email;
    private String age;
    private String role;

    @Builder
    public AccountDto(String username, String password, String email, String age, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
        this.role = role;
    }
}
