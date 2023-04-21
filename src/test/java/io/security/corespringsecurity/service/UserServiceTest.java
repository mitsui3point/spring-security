package io.security.corespringsecurity.service;

import io.security.corespringsecurity.domain.Account;
import io.security.corespringsecurity.repository.UserRepository;
import io.security.corespringsecurity.test.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import static io.security.corespringsecurity.constants.TestDataConstants.RAW_PASSWORD;
import static io.security.corespringsecurity.constants.TestDataConstants.getUser;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@WebMvcTest
@Import(TestConfig.class)
public class UserServiceTest {
    @Autowired
    UserService service;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired//TestConfig 에서 Inject 를 @MockBean 으로 하기 때문에 BDDMyOngoingStubbing 사용가능
    UserRepository repository;
    Account user;

    @BeforeEach
    void setUp() {
        user = getUser(passwordEncoder.encode(RAW_PASSWORD));
    }

    @Test
    @DisplayName("회원가입 후 DB 에 저장한다.")
    void createUser() {
        //given
        given(repository.save(user)).willReturn(user);

        //when
        service.createUser(user);

        //then
        verify(repository, times(1)).save(user);
    }
}
