package io.security.corespringsecurity.security.handler;

import io.security.corespringsecurity.domain.Account;
import io.security.corespringsecurity.security.service.AccountContext;
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
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Set;

import static io.security.corespringsecurity.constants.TestDataConstants.*;
import static io.security.corespringsecurity.constants.UrlConstant.MYPAGE_URL;
import static io.security.corespringsecurity.constants.UrlConstant.ROOT_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpMethod.GET;

@WebMvcTest
@ExtendWith(MockitoExtension.class)
@Import(TestConfig.class)
public class CustomAuthenticationSuccessHandlerTest {

    @InjectMocks
    CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Mock
    HttpSessionRequestCache requestCache;

    @Mock
    RedirectStrategy redirectStrategy;

    Account user;
    Set<GrantedAuthority> roles;
    AccountContext accountContext;
    MockHttpServletRequest request;
    MockHttpServletResponse response;

    private TestingAuthenticationToken authentication;

    @BeforeEach
    void setUp() {
        user = getUser(passwordEncoder.encode(RAW_PASSWORD));
        roles = getRoles(user);
        accountContext = new AccountContext(user, roles);
        response = new MockHttpServletResponse();
        authentication = new TestingAuthenticationToken(accountContext, null);
    }

    @Test
    @DisplayName("CustomAuthenticationSuccessHandler 타입이 AuthenticationSuccessHandler 이다.")
    void instanceOf() {
        boolean actualAuthenticationSuccessHandler = customAuthenticationSuccessHandler instanceof AuthenticationSuccessHandler;
        boolean actualSimpleUrlAuthenticationSuccessHandler = customAuthenticationSuccessHandler instanceof SimpleUrlAuthenticationSuccessHandler;
        assertThat(actualAuthenticationSuccessHandler).isTrue();
        assertThat(actualSimpleUrlAuthenticationSuccessHandler).isTrue();
    }

    @Test
    @DisplayName("로그인 성공 후 처리; 미인증상태시 원래 접근하려던 페이지로 redirect 한다")
    void onAuthenticationPrevRedirect() throws ServletException, IOException {
        //given
        HttpSessionRequestCache savedRequestCache =  new HttpSessionRequestCache();
        request = new MockHttpServletRequest(GET.name(), MYPAGE_URL);
        request.setParameter("secret_key", "secret");
        savedRequestCache.saveRequest(request, response);

        given(requestCache.getRequest(any(), any())).willReturn(savedRequestCache.getRequest(request, response));
        doNothing().when(redirectStrategy).sendRedirect(any(), any(), any());

        //when
        customAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        //then
        verify(requestCache, times(1)).getRequest(any(), any());
        verify(redirectStrategy, times(1)).sendRedirect(any(), any(), eq("http://localhost" + MYPAGE_URL));
    }

    @Test
    @DisplayName("로그인 성공 후 처리; 미인증상태시 접근하려던 페이지가 없기 때문에 root url 로 redirect 한다")
    void onAuthenticationRootRedirect() throws ServletException, IOException {
        //given
        HttpSessionRequestCache notSavedRequestCache = new HttpSessionRequestCache();
        request = new MockHttpServletRequest();
        request.setParameter("secret_key", "secret");

        given(requestCache.getRequest(any(), any())).willReturn(notSavedRequestCache.getRequest(request, response));
        doNothing().when(redirectStrategy).sendRedirect(any(), any(), any());

        //when
        customAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        //then
        verify(requestCache, times(1)).getRequest(any(), any());//.getRedirectUrl();
        verify(redirectStrategy, times(1)).sendRedirect(any(), any(), eq(ROOT_URL));
    }
}
