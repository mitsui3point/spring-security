package io.security.corespringsecurity.repository;

import io.security.corespringsecurity.domain.Account;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserRepositoryTest {

    @Autowired
    UserRepository repository;

    @Test
    @DisplayName("회원테이블에 데이터를 저장후, 조회에 성공한다.")
    void saveAndFindById() {
        //given
        Account account = Account.builder().username("user1").build();

        //when
        repository.save(account);
        Account user1 = repository.findById(account.getId())
                .orElseGet(() -> Account
                        .builder()
                        .build());

        //then
        assertThat(user1)
                .extracting("username")
                .isEqualTo("user1");
    }

//    @AfterEach
//    void tearDown() {
//        repository.deleteAll();
//    }
}
