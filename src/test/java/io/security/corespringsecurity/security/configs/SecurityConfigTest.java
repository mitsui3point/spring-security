package io.security.corespringsecurity.security.configs;

import io.security.corespringsecurity.domain.Account;
import io.security.corespringsecurity.security.common.FormWebAuthenticationDetailsSource;
import io.security.corespringsecurity.security.service.AccountContext;
import io.security.corespringsecurity.security.service.CustomUsersDetailsService;
import io.security.corespringsecurity.service.UserService;
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
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Set;

import static io.security.corespringsecurity.constants.TestDataConstants.*;
import static io.security.corespringsecurity.constants.UrlConstant.LOGIN_PROC_URL;
import static io.security.corespringsecurity.constants.UrlConstant.ROOT_URL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ExtendWith(MockitoExtension.class)
@MockBeans(@MockBean(UserService.class))
@Import(FormWebAuthenticationDetailsSource.class)
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
    @DisplayName("등록된 유저들의 /login 페이지 로그인 성공한다.")
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