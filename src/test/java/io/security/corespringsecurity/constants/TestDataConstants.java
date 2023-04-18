package io.security.corespringsecurity.constants;

import io.security.corespringsecurity.domain.Account;

public class TestDataConstants {
    public static final String REDIRECTED_LOGIN_URL = "http://localhost/login";

    public static final Account NO_INFO_USER = Account
            .builder()
            .build();

    public static final Account USER1_USER = Account.builder()
            .username("user1")
            .build();

    public static Account getUser(String password) {
        return Account.builder()
                .username("user")
                .password(password)
                .age("11")
                .role("ROLE_USER")
                .email("aa@aa.com")
                .build();
    }

    public static Account getManager(String password) {
        return Account.builder()
                .username("manager")
                .password(password)
                .age("11")
                .role("ROLE_USER")
                .email("aa@aa.com")
                .build();
    }

    public static Account getAdmin(String password) {
        return Account.builder()
                .username("admin")
                .password(password)
                .age("11")
                .role("ADMIN")
                .email("aa@aa.com")
                .build();
    }
}
