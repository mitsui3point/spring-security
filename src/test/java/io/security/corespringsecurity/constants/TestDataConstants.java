package io.security.corespringsecurity.constants;

import io.security.corespringsecurity.domain.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;

public class TestDataConstants {
    public static final String REDIRECTED_LOGIN_URL = "http://localhost/login";

    public static final String RAW_PASSWORD = "1111";

    public static final Account NO_INFO_USER = Account
            .builder()
            .build();

    public static Account getUser(String password) {
        return Account.builder()
                .username("user")
                .password(password)
                .age("11")
                .role("USER")
//                .role("ROLE_USER")
                .email("aa@aa.com")
                .build();
    }

    public static Account getManager(String password) {
        return Account.builder()
                .username("manager")
                .password(password)
                .age("11")
                .role("MANAGER")
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

    public static Set<GrantedAuthority> getRoles(Account account) {
        Set<GrantedAuthority> roles = new HashSet<>();
        roles.add(new SimpleGrantedAuthority(account.getRole()));
        return roles;
    }
}
