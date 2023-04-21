package io.security.corespringsecurity.service;

import io.security.corespringsecurity.domain.Account;
import io.security.corespringsecurity.repository.UserRepository;
import io.security.corespringsecurity.security.common.FormWebAuthenticationDetailsSource;
import io.security.corespringsecurity.security.service.CustomUsersDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.security.corespringsecurity.constants.TestDataConstants.RAW_PASSWORD;
import static io.security.corespringsecurity.constants.TestDataConstants.getUser;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@WebMvcTest
@ExtendWith(SpringExtension.class)
@MockBeans({
        @MockBean(CustomUsersDetailsService.class),
        @MockBean(FormWebAuthenticationDetailsSource.class)
})
@Import(UserServiceImpl.class)
public class UserServiceTest {
    @Autowired
    UserService service;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockBean
    UserRepository repository;

    @Test
    @DisplayName("회원가입 후 DB 에 저장한다.")
    void createUser() {
        //given
        Account user = getUser(passwordEncoder.encode(RAW_PASSWORD));
        given(repository.save(user)).willReturn(user);

        //when
        service.createUser(user);

        //then
        verify(repository, times(1)).save(user);
    }
}
