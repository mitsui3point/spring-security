package io.security.corespringsecurity.configs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class SecurityConfigTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void registeredConfigBean() {
        SecurityConfig securityConfig = context.getBean(SecurityConfig.class);
        //then
        assertThat(securityConfig).isNotNull();
        assertThat(securityConfig).isInstanceOf(WebSecurityConfigurerAdapter.class);
    }

    @Test
    @DisplayName("등록된 유저들의 /login 페이지 로그인 성공한다.")
    void loginTest() throws Exception {
        performAndExpectLoginSuccess("user", "1111");
        performAndExpectLoginSuccess("manager", "1111");
        performAndExpectLoginSuccess("admin", "1111");
    }

    private void performAndExpectLoginSuccess(String user, String password) throws Exception {
        //when
        mvc.perform(formLogin()
                        .loginProcessingUrl("/login")
                        .user(user)
                        .password(password))
                .andDo(print())
                //then
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername(user));
    }
}
