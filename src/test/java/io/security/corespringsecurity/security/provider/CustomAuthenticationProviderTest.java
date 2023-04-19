package io.security.corespringsecurity.security.provider;

import io.security.corespringsecurity.controller.user.UserController;
import io.security.corespringsecurity.domain.Account;
import io.security.corespringsecurity.security.service.AccountContext;
import io.security.corespringsecurity.security.service.CustomUsersDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static io.security.corespringsecurity.constants.TestDataConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@WebMvcTest
@ExtendWith({MockitoExtension.class, SpringExtension.class})
public class CustomAuthenticationProviderTest {
    @Autowired
    AuthenticationProvider authenticationProvider;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockBean
    UserController userController;

    @MockBean
    CustomUsersDetailsService customUsersDetailsService;

    @Mock
    Authentication authentication;

    @Test
    @DisplayName("AuthenticationProvider instanceof CustomAuthenticationProvider")
    void instanceofTest() {
        //when
        boolean isAuthenticateProvider = authenticationProvider instanceof CustomAuthenticationProvider;
        //then
        assertThat(isAuthenticateProvider).isTrue();

    }

    @Test
    @DisplayName("추가 검증 메서드; 검증에 성공한다")
    void authenticateTest() {
        //given
        Account user = getUser(passwordEncoder.encode(RAW_PASSWORD));
        Set<GrantedAuthority> roles = getRoles(user);
        AccountContext accountContext = new AccountContext(user, roles);
        setGiven(user, accountContext);
        //when
        Authentication authenticate = authenticationProvider.authenticate(authentication);
        //then
        assertThat(authenticate.getPrincipal()).isEqualTo(accountContext);
        assertThat(authenticate.getCredentials()).isNull();
        assertThat(authenticate.getAuthorities().containsAll(accountContext.getAuthorities())).isTrue();
        verify(customUsersDetailsService, times(1)).loadUserByUsername(any());
    }

    @Test
    @DisplayName("추가 검증 메서드; UsernameNotFoundException 예외로 인해 검증에 실패한다")
    void authenticateUsernameNotFoundExceptionTest() {
        //given
        Account user = getUser(passwordEncoder.encode(RAW_PASSWORD));
        setGiven(user, new UsernameNotFoundException("UsernameNotFoundException"));
        //then
        assertThatThrownBy(() ->
                authenticationProvider.authenticate(authentication))//when
                .isInstanceOf(UsernameNotFoundException.class);
        verify(customUsersDetailsService, times(1)).loadUserByUsername(any());
    }

    @Test
    @DisplayName("추가 검증 메서드; BadCredentialsException 예외로 인해 검증에 실패한다")
    void authenticateBadCredentialsExceptionTest() {
        //given
        Account user = getUser(passwordEncoder.encode(RAW_PASSWORD));
        setGiven(user, new BadCredentialsException("BadCredentialsException"));
        //then
        assertThatThrownBy(() ->
                authenticationProvider.authenticate(authentication))//when
                .isInstanceOf(BadCredentialsException.class);
        verify(customUsersDetailsService, times(1)).loadUserByUsername(any());
    }

    @Test
    @DisplayName("토큰이 맞는지 검증하는 메서드; 토큰이 일치한다.")
    void supportsSuccessTest() {
        //when
        boolean supports = authenticationProvider.supports(UsernamePasswordAuthenticationToken.class);
        //then
        assertThat(supports).isTrue();
    }

    @Test
    @DisplayName("토큰이 맞는지 검증하는 메서드; 토큰이 일치하지 않는다.")
    void supportsFailTest() {
        //when
        boolean supports = authenticationProvider.supports(AnonymousAuthenticationToken.class);
        //then
        assertThat(supports).isFalse();
    }

    private void setGiven(Account user, AccountContext accountContext) {
        setAuthenticationGiven(user);
        given(customUsersDetailsService.loadUserByUsername(any()))
                .willReturn(accountContext);
    }

    private void setGiven(Account user, Exception exception) {
        setAuthenticationGiven(user);
        given(customUsersDetailsService.loadUserByUsername(any()))
                .willThrow(exception);
    }

    private void setAuthenticationGiven(Account user) {
        given(authentication.getName())
                .willReturn(user.getUsername());
        given(authentication.getCredentials())
                .willReturn(RAW_PASSWORD);
    }
}
