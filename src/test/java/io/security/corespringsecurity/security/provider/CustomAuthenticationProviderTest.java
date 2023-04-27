package io.security.corespringsecurity.security.provider;

import io.security.corespringsecurity.domain.Account;
import io.security.corespringsecurity.security.common.FormWebAuthenticationDetails;
import io.security.corespringsecurity.security.service.AccountContext;
import io.security.corespringsecurity.security.service.CustomUsersDetailsService;
import io.security.corespringsecurity.test.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static io.security.corespringsecurity.constants.TestDataConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@WebMvcTest
@Import(TestConfig.class)
public class CustomAuthenticationProviderTest {
    @InjectMocks
    CustomAuthenticationProvider authenticationProvider;

    @Mock
    PasswordEncoder mockPasswordEncoder;

    @Mock
    CustomUsersDetailsService customUsersDetailsService;

    @Mock
    Authentication authentication;

    /* 테스트 데이터 실제 encoder 기능을 구현하기 위해 추가 */
    @Autowired
    PasswordEncoder passwordEncoder;


    Account user;
    Set<GrantedAuthority> roles;
    AccountContext accountContext;
    MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        user = getUser(passwordEncoder.encode(RAW_PASSWORD));
        roles = getRoles(user);
        accountContext = new AccountContext(user, roles);
        request = new MockHttpServletRequest();
        request.setParameter("secret_key", "secret");
    }

    @Test
    @DisplayName("AuthenticationProvider instanceof CustomAuthenticationProvider")
    void instanceOf() {
        //when
        boolean isAuthenticateProvider = authenticationProvider instanceof CustomAuthenticationProvider;
        //then
        assertThat(isAuthenticateProvider).isTrue();

    }

    @Test
    @DisplayName("추가 검증 메서드; 검증에 성공한다")
    void authenticate() {
        //given
        given(authentication.getName()).willReturn(user.getUsername());
        given(authentication.getCredentials()).willReturn(RAW_PASSWORD);
        given(authentication.getDetails()).willReturn(new FormWebAuthenticationDetails(request));
        given(mockPasswordEncoder.matches(any(), any())).willReturn(true);
        given(customUsersDetailsService.loadUserByUsername(any())).willReturn(accountContext);

        //when
        Authentication authenticate = authenticationProvider.authenticate(authentication);

        //then
        assertThat(authenticate.getPrincipal()).isEqualTo(accountContext.getAccount());
        assertThat(authenticate.getCredentials()).isNull();
        assertThat(authenticate.getAuthorities().containsAll(accountContext.getAuthorities())).isTrue();
        verify(customUsersDetailsService, times(1)).loadUserByUsername(any());
    }

    @Test
    @DisplayName("추가 검증 메서드; 유저 조회 실패하여 UsernameNotFoundException 예외가 발생하여 검증에 실패한다")
    void usernameNotFound() {
        //given
        given(authentication.getName()).willReturn("notFoundUsername");//없는 유저id
        given(authentication.getCredentials()).willReturn(RAW_PASSWORD);
        given(mockPasswordEncoder.matches(any(), any())).willReturn(false);
        given(customUsersDetailsService.loadUserByUsername(any())).willThrow(new UsernameNotFoundException("UsernameNotFoundException"));//유저 조회 실패

        //then
        assertThatThrownBy(() ->
                authenticationProvider.authenticate(authentication))//when
                .isInstanceOf(UsernameNotFoundException.class);
        verify(customUsersDetailsService, times(1)).loadUserByUsername(any());
    }

    @Test
    @DisplayName("추가 검증 메서드; 유저 비밀번호 불일치하여 BadCredentialsException 예외가 발생하여 검증에 실패한다")
    void passwordBadCredentials() {
        //given
        given(authentication.getName()).willReturn(user.getUsername());
        given(authentication.getCredentials()).willReturn("notAcceptedPassword");//패스워드 불일치
        given(authentication.getDetails()).willReturn(new FormWebAuthenticationDetails(request));
        given(mockPasswordEncoder.matches(any(), any())).willReturn(false);
        given(customUsersDetailsService.loadUserByUsername(any())).willReturn(accountContext);

        //then
        assertThatThrownBy(() ->
                authenticationProvider.authenticate(authentication))//when
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("invalid password");
        verify(customUsersDetailsService, times(1)).loadUserByUsername(any());
    }

    @Test
    @DisplayName("추가 검증 메서드; 유저 secretKey 불일치하여 BadCredentialsException 예외가 발생하여 검증에 실패한다")
    void secretKeyBadCredentials() {
        //given
        request.setParameter("secret_key", "notAcceptedSecretKey");
        given(authentication.getName()).willReturn(user.getUsername());
        given(authentication.getCredentials()).willReturn(RAW_PASSWORD);
        given(authentication.getDetails()).willReturn(new FormWebAuthenticationDetails(request));
        given(mockPasswordEncoder.matches(any(), any())).willReturn(true);
        given(customUsersDetailsService.loadUserByUsername(any())).willReturn(accountContext);

        //then
        assertThatThrownBy(() ->
                authenticationProvider.authenticate(authentication))//when
                .isInstanceOf(InsufficientAuthenticationException.class)
                .hasMessage("InsufficientAuthenticationException");
        verify(customUsersDetailsService, times(1)).loadUserByUsername(any());
    }

    @Test
    @DisplayName("토큰이 맞는지 검증하는 메서드; 토큰이 일치한다.")
    void supportsSuccess() {
        //when
        boolean supports = authenticationProvider.supports(UsernamePasswordAuthenticationToken.class);
        //then
        assertThat(supports).isTrue();
    }

    @Test
    @DisplayName("토큰이 맞는지 검증하는 메서드; 토큰이 일치하지 않는다.")
    void supportsFail() {
        //when
        boolean supports = authenticationProvider.supports(AnonymousAuthenticationToken.class);
        //then
        assertThat(supports).isFalse();
    }

}
