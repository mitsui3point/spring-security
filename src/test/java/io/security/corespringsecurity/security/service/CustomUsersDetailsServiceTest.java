package io.security.corespringsecurity.security.service;

import io.security.corespringsecurity.domain.Account;
import io.security.corespringsecurity.repository.UserRepository;
import io.security.corespringsecurity.test.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static io.security.corespringsecurity.constants.TestDataConstants.RAW_PASSWORD;
import static io.security.corespringsecurity.constants.TestDataConstants.getUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@WebMvcTest
@ExtendWith(MockitoExtension.class)
@Import(TestConfig.class)
public class CustomUsersDetailsServiceTest {

    @Autowired
    @InjectMocks
    private CustomUsersDetailsService customUsersDetailsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired//TestConfig 에서 Inject 를 @MockBean 으로 하기 때문에 BDDMyOngoingStubbing 사용가능
    private UserRepository userRepository;

    Account user;

    @BeforeEach
    void setUp() {
        user = getUser(passwordEncoder.encode(RAW_PASSWORD));
    }

    @Test
    @DisplayName("username 으로 user 을 찾는다.")
    void loadUserByUsername() {
        //given
        given(userRepository.findByUsername(any())).willReturn(user);
        //when
        UserDetails details = customUsersDetailsService.loadUserByUsername("user");
        //then
        verify(userRepository, times(1)).findByUsername(any());
        assertThat(details).isNotNull();
    }

    @Test
    @DisplayName("username 으로 user1 찾기를 실패한다.")
    void loadUserByUsernameFail() {
        //given
        given(userRepository.findByUsername(any())).willReturn(null);
        //then
        assertThatThrownBy(() -> {
            //when
            customUsersDetailsService.loadUserByUsername(user.getUsername());
        }).isInstanceOf(UsernameNotFoundException.class);
    }
}
