package io.security.corespringsecurity.service;

import io.security.corespringsecurity.domain.Account;
import org.springframework.stereotype.Service;

public interface UserService {
    void createUser(Account account);
}
