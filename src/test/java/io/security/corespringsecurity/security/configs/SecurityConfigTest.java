package io.security.corespringsecurity.security.configs;

import io.security.corespringsecurity.domain.Account;
import io.security.corespringsecurity.security.service.AccountContext;
import io.security.corespringsecurity.security.service.CustomUsersDetailsService;
import io.security.corespringsecurity.test.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.servlet.server.Encoding;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static io.security.corespringsecurity.constants.TestDataConstants.*;
import static io.security.corespringsecurity.constants.UrlConstant.*;
import static java.nio.charset.StandardCharsets.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@ExtendWith(MockitoExtension.class)
@Import(TestConfig.class)
public class SecurityConfigTest {

    @Autowired
    WebApplicationContext context;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockBean
    CustomUsersDetailsService customUsersDetailsService;

    MockMvc mvc;
    Account account;
    Set<GrantedAuthority> roles;
    AccountContext accountContext;


    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        //given
        account = getAdmin(passwordEncoder.encode(RAW_PASSWORD));
        roles = getRoles(account);
        accountContext = new AccountContext(account, roles);
    }

    @Test
    @DisplayName("등록된 유저의 /login 페이지 로그인 성공한다.")
    void loginTest() throws Exception {
        //given
        given(customUsersDetailsService.loadUserByUsername(account.getUsername())).willReturn(accountContext);
        //when
        mvc.perform(post(LOGIN_PROC_URL)
                        .with(csrf())
                        .param("username", "admin")
                        .param("password", RAW_PASSWORD)
                        .param("secret_key", "secret")
                )
                .andDo(print())
                //then
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT_URL))
                .andExpect(authenticated()
                        .withUsername("admin")
                        .withAuthenticationPrincipal(accountContext)
                )
        ;
        //then
        verify(customUsersDetailsService, times(1)).loadUserByUsername(any());
    }

    @Test
    @DisplayName("등록되지 않은 유저의 /login 페이지 로그인 실패한다.")
    void loginUserInvalidTest() throws Exception {
        //given
        given(customUsersDetailsService.loadUserByUsername(account.getUsername())).willReturn(accountContext);
        //when
        mvc.perform(post(LOGIN_PROC_URL)
                        .with(csrf())
                        .param("username", "admin")
                        .param("password", "fail password")
                        .param("secret_key", "secret")
                )
                .andDo(print())
                //then
                .andExpect(redirectedUrl("/login?error=true&exception=Invalid Username or Password"))
                .andExpect(unauthenticated())
        ;
        //then
        verify(customUsersDetailsService, times(1)).loadUserByUsername(any());
    }

    @Test
    @DisplayName("secretKey 불일치시 /login 페이지 로그인 실패한다.")
    void loginKeyInvalidTest() throws Exception {
        //given
        given(customUsersDetailsService.loadUserByUsername(account.getUsername())).willReturn(accountContext);
        //when
        mvc.perform(post(LOGIN_PROC_URL)
                        .with(csrf())
                        .param("username", "admin")
                        .param("password", RAW_PASSWORD)
                        .param("secret_key", "ecret")
                )
                .andDo(print())
                //then
                .andExpect(redirectedUrl("/login?error=true&exception=Invalid Secret Key"))
                .andExpect(unauthenticated())
        ;
        //then
        verify(customUsersDetailsService, times(1)).loadUserByUsername(any());
    }

    @Test
    @DisplayName("권한을 갖고 있는 사용자가 권한이 필요한 페이지 접근을 성공한다.")
    @WithMockUser(username = "user", roles = "USER")
    void loginAuthorizedUserTest() throws Exception {
        //when
        mvc.perform(get(MYPAGE_URL)
                        .with(csrf())
                )
                .andDo(print())
                //then
                .andExpect(status().isOk())
                .andExpect(view().name("user/mypage"))
        ;
    }

    @Test
    @DisplayName("권한을 갖고 있는 사용자가 권한이 필요한 페이지 접근을 성공한다.")
    @WithMockUser(username = "user", roles = "USER")
    void loginUnauthorizedUserTest() throws Exception {
        //when
        mvc.perform(get(MESSAGES_URL)
                        .with(csrf())
                )
                .andDo(print())
                //then
                .andExpect(redirectedUrl("/denied?exception=Access is denied"))
        ;
    }

    @ParameterizedTest
    @DisplayName("web static resources 접근시에는 spring security 를 적용하지 않도록 한다.")
    @ValueSource(strings = {
            "/css/base.css", "/css/bootstrap.min.css", "/css/bootstrap-responsive.min.css", "/css/bootstrap-table.css", "/css/style.css",
            "/images/springsecurity.jpg",
            "/js/bootstrap.min.js", "/js/bootstrap-table.js", "/js/jquery-2.1.3.min.js",

            /*
            HttpSecurity#antMatchers(ROOT_URL, USERS_URL, "user/login/**", "/login").permitAll()

            "/login" 추가: 기존 /login 은 default login 기능에서 queryParam 를 제공하지 않았으나,
            failureHandler 추가하면서 queryParam 을 추가하고,
            /login?error=true&exception=... 를 접근 경로로 인식하기 때문에 모두 접근 가능하도록 해야 하므로 추가함.
            */

            "/login?error=false",
            "/login?error=true&exception=InsufficientAuthenticationException",
            "/login?error=true&exception=UsernameNotFoundException",
            "/login?error=true&exception=invalid password",

            "/denied"
    })
    void webIgnoreTest(String url) throws Exception {
        //when
        mvc.perform(get(URLDecoder.decode(url, UTF_8.name())))
                .andDo(print())
                //then
                .andExpect(status().isOk());
    }
}