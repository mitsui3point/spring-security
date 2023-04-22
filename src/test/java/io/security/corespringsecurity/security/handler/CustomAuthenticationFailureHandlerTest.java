package io.security.corespringsecurity.security.handler;

import io.security.corespringsecurity.test.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@WebMvcTest
@ExtendWith(MockitoExtension.class)
@Import(TestConfig.class)
public class CustomAuthenticationFailureHandlerTest {
    @Autowired
    @InjectMocks
    CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Mock
    SimpleUrlAuthenticationFailureHandler simpleUrlAuthenticationFailureHandler;

    HttpServletRequest request;
    HttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("CustomAuthenticationFailureHandler type 이 AuthenticationFailureHandler 이다.")
    void instanceOf() {
        boolean actualImplType = customAuthenticationFailureHandler instanceof SimpleUrlAuthenticationFailureHandler;
        boolean actualInterfaceType = customAuthenticationFailureHandler instanceof AuthenticationFailureHandler;
        assertThat(actualImplType).isTrue();
        assertThat(actualInterfaceType).isTrue();
    }

    @Test
    @DisplayName("(유효하지 않은 secret key) 인증 실패시 호출되어 화면으로 예외상황에 대한 메세지를 전달한다.")
    void onAuthenticationInvalidSecretKey() throws ServletException, IOException {
        InsufficientAuthenticationException ex = new InsufficientAuthenticationException("InsufficientAuthenticationException");
        failureAuthenticationTest(ex, "Invalid Secret Key");
    }

    @Test
    @DisplayName("(없는 회원) 인증 실패시 호출되어 화면으로 예외상황에 대한 메세지를 전달한다.")
    void onAuthenticationNotFoundUser() throws ServletException, IOException {
        UsernameNotFoundException ex = new UsernameNotFoundException("UsernameNotFoundException");
        failureAuthenticationTest(ex, "Invalid Username or Password");
    }

    @Test
    @DisplayName("(유효하지 않은 패스워드) 인증 실패시 호출되어 화면으로 예외상황에 대한 메세지를 전달한다.")
    void onAuthenticationInvalidPassword() throws ServletException, IOException {
        BadCredentialsException ex = new BadCredentialsException("invalid password");
        failureAuthenticationTest(ex, "Invalid Username or Password");
    }

    private void failureAuthenticationTest(AuthenticationException exception, String errorMessage) throws IOException, ServletException {
        //given
        simpleUrlAuthenticationFailureHandler.setDefaultFailureUrl("/login?error=true&exception=" + errorMessage);
        simpleUrlAuthenticationFailureHandler.onAuthenticationFailure(request, response, exception);
        //when
        customAuthenticationFailureHandler.onAuthenticationFailure(request, response, exception);
        //then
        verify(simpleUrlAuthenticationFailureHandler, times(1)).setDefaultFailureUrl(eq("/login?error=true&exception=" + errorMessage));
        verify(simpleUrlAuthenticationFailureHandler, times(1)).onAuthenticationFailure(eq(request), eq(response), eq(exception));
    }
}
