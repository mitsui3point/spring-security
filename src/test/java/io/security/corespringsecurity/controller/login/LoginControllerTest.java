package io.security.corespringsecurity.controller.login;

import io.security.corespringsecurity.domain.Account;
import io.security.corespringsecurity.security.service.AccountContext;
import io.security.corespringsecurity.test.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static io.security.corespringsecurity.constants.TestDataConstants.*;
import static io.security.corespringsecurity.constants.UrlConstant.LOGIN_URL;
import static io.security.corespringsecurity.constants.UrlConstant.LOGOUT_URL;
import static java.util.Arrays.stream;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
@ExtendWith(MockitoExtension.class)
@Import(TestConfig.class)
public class LoginControllerTest {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MockMvc mvc;

    @Mock
    Authentication authentication;

    @BeforeEach
    void setUp() {
//        mvc = MockMvcBuilders.webAppContextSetup(context)
//                .apply(springSecurity())
//                .build();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @WithAnonymousUser
    @DisplayName("직접 커스터마이징한 로그인 페이지를 노출한다.")
    void customLoginPageTest() throws Exception {
        //when
        mvc.perform(get(LOGIN_URL))
                .andDo(print())
                //then
                .andExpect(view().name("user/login/login"));
    }

    @Test
    @WithMockUser(username = "user", password = "1111", roles = "USER")
    @DisplayName("로그아웃하면 로그인페이지로 redirect 한다.")
    void logoutTest() throws Exception {
        //when
        mvc.perform(get(LOGOUT_URL))
                .andDo(print())
                //then
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LOGIN_URL))
                .andExpect(unauthenticated())//인증되지 않은 상태
        ;
    }

    @Test
    @DisplayName("인증되지 않았을 경우, 로그인페이지에 인증되지 않은 이유에 대한 메세지를 전달한다.")
    void loginFailureInvalidSecretTest() throws Exception {
        String[] exceptionsMessages = stream(getAuthenticationExceptions())
                .map(o -> o.getMessage())
                .toArray(String[]::new);
        //when
        for (String exceptionMessage : exceptionsMessages) {
            mvc.perform(get(LOGIN_URL)
                            .param("error", "true")
                            .param("exception", exceptionMessage)
                    )
                    .andDo(print())
                    //then
                    .andExpect(status().isOk())
                    .andExpect(unauthenticated())//인증되지 않은 상태
                    .andExpect(model().attribute("error", "true"))
                    .andExpect(model().attribute("exception", exceptionMessage))
                    .andExpect(view().name("user/login/login"))
                    .andExpect(result -> result.getResponse().getContentAsString().contains(exceptionMessage))//화면 내에 exception 메세지 출력여부 확인
            ;
        }
    }

    @Test
    @WithMockUser(username = "user", password = "1111", roles = {"USER"})
    @DisplayName("/denied 의 페이지 동작을 확인한다.")
    void accessDeniedPageTest() throws Exception {
        //given
        String exceptionMessage = "invalid access user";
        //when
        mvc.perform(get("/denied")
                        .param("exception", exceptionMessage)
                )
                .andDo(print())
                //then
                .andExpect(status().isOk())
                .andExpect(view().name("user/login/denied"))
                .andExpect(model().attribute("username", "user"))
                .andExpect(model().attribute("exception", exceptionMessage))
                .andExpect(result -> result.getResponse().getContentAsString().contains(exceptionMessage))//화면 내에 exception 메세지 출력여부 확인
                .andExpect(result -> result.getResponse().getContentAsString().contains("user"))//화면 내에 username 메세지 출력여부 확인
        ;
    }

    private AuthenticationException[] getAuthenticationExceptions() {
        return new AuthenticationException[]{
                new InsufficientAuthenticationException("InsufficientAuthenticationException"),
                new UsernameNotFoundException("UsernameNotFoundException"),
                new BadCredentialsException("invalid password")
        };
    }
}
