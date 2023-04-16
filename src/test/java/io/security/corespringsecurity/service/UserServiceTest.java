package io.security.corespringsecurity.service;

import io.security.corespringsecurity.domain.Account;
import io.security.corespringsecurity.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class UserServiceTest {
    @Autowired
    UserService service;

    @Autowired
    UserRepository repository;

    @Test
    @DisplayName("회원가입 후 DB 에 저장한다.")
    void createUser() {
        //given
        Account user1 = Account.builder().username("user1").build();

        //when
        service.createUser(user1);

        //then
        Account actual = repository.findById(user1.getId()).orElseGet(() -> Account.builder().build());
        Assertions.assertThat(actual).extracting("username").isEqualTo("user1");
    }
}
