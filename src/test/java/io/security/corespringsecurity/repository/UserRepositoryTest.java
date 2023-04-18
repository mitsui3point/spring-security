package io.security.corespringsecurity.repository;

import io.security.corespringsecurity.domain.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static io.security.corespringsecurity.constants.TestDataConstants.NO_INFO_USER;
import static io.security.corespringsecurity.constants.TestDataConstants.getUser;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class UserRepositoryTest {

    @Autowired
    UserRepository repository;

    @Autowired
    PasswordEncoder passwordEncoder;
    private Account user;

    @BeforeEach
    void setUp() {
        user = getUser(passwordEncoder.encode("1111"));
    }

    @Test
    @DisplayName("회원테이블에 데이터를 저장후, id 로 조회에 성공한다.")
    void saveAndFindById() {
        //when
        repository.save(user);
        Account actualUser = repository.findById(user.getId())
                .orElseGet(() -> NO_INFO_USER);

        //then
        assertThat(actualUser)
                .extracting("username")
                .isEqualTo("user");
    }

    @Test
    @DisplayName("회원테이블에 데이터를 저장후, username 으로 조회에 성공한다.")
    void saveAndFindByUsername() {
        //when
        repository.save(user);
        Account actualUser = repository.findByUsername(user.getUsername());

        //then
        assertThat(actualUser)
                .extracting("username")
                .isEqualTo("user");
    }

}
