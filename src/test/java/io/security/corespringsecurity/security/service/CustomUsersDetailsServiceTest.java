package io.security.corespringsecurity.security.service;

import io.security.corespringsecurity.controller.user.UserController;
import io.security.corespringsecurity.domain.Account;
import io.security.corespringsecurity.repository.UserRepository;
import io.security.corespringsecurity.security.common.FormWebAuthenticationDetailsSource;
import io.security.corespringsecurity.security.configs.SecurityConfig;
import io.security.corespringsecurity.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

//@SpringBootTest
@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
@MockBeans({
        @MockBean(UserService.class),
        @MockBean(FormWebAuthenticationDetailsSource.class),
})
@Import({
        CustomUsersDetailsService.class,
        SecurityConfig.class
})
public class CustomUsersDetailsServiceTest {
    @Autowired
    private CustomUsersDetailsService customUsersDetailsService;

    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("username 으로 user1 을 찾는다.")
    void loadUserByUsername() {
        //given
        Account account = Account.builder().username("user1").password("1111").age("11").role("ADMIN").email("aa@aa.com").build();
        given(userRepository.findByUsername("user1")).willReturn(account);
        //when
        UserDetails details = customUsersDetailsService.loadUserByUsername("user1");
        //then
        assertThat(details).isNotNull();
    }

    @Test
    @DisplayName("username 으로 user1 찾기를 실패한다.")
    void loadUserByUsernameFail() {
        //given
        given(userRepository.findByUsername("user1")).willReturn(null);
        //then
        assertThatThrownBy(() -> {
            //when
            customUsersDetailsService.loadUserByUsername("user1");
        }).isInstanceOf(UsernameNotFoundException.class);
    }
}
