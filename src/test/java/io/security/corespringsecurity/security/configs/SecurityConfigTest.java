package io.security.corespringsecurity.security.configs;

import io.security.corespringsecurity.constants.TestDataConstants;
import io.security.corespringsecurity.constants.UrlConstant;
import io.security.corespringsecurity.domain.Account;
import io.security.corespringsecurity.security.provider.CustomAuthenticationProvider;
import io.security.corespringsecurity.security.service.AccountContext;
import io.security.corespringsecurity.security.service.CustomUsersDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Set;

import static io.security.corespringsecurity.constants.TestDataConstants.*;
import static io.security.corespringsecurity.constants.UrlConstant.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest()
//@Import({SecurityConfig.class, CustomAuthenticationProvider.class})
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class SecurityConfigTest {

    @Autowired
    WebApplicationContext context;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockBean
    CustomUsersDetailsService customUsersDetailsService;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("등록된 유저들의 /login 페이지 로그인 성공한다.")
    void loginTest() throws Exception {
        //given
        Account account = getAdmin(passwordEncoder.encode(RAW_PASSWORD));
        Set<GrantedAuthority> roles = getRoles(account);
        AccountContext accountContext = new AccountContext(account, roles);

        given(customUsersDetailsService.loadUserByUsername(any()))
                .willReturn(accountContext);

        //when
        mvc.perform(formLogin()
                        .loginProcessingUrl(LOGIN_PROC_URL)
                        .user("admin")
                        .password(RAW_PASSWORD))
                .andDo(print())
                //then
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT_URL))
                .andExpect(authenticated()
                        .withUsername("admin")
                        .withAuthenticationPrincipal(accountContext)
                );
        //then
        verify(customUsersDetailsService, times(1)).loadUserByUsername(any());
    }

    @ParameterizedTest
    @DisplayName("web static resources 접근시에는 spring security 를 적용하지 않도록 한다.")
    @ValueSource(strings = {
            "/css/base.css", "/css/bootstrap.min.css", "/css/bootstrap-responsive.min.css", "/css/bootstrap-table.css", "/css/style.css",
            "/images/springsecurity.jpg",
            "/js/bootstrap.min.js", "/js/bootstrap-table.js", "/js/jquery-2.1.3.min.js",
    })
    void webIgnoreTest(String url) throws Exception {
        //when
        mvc.perform(get(url))
                .andDo(print())
                //then
                .andExpect(status().isOk());
    }
}