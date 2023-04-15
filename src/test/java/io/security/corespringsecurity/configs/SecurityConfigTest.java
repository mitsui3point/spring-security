package io.security.corespringsecurity.configs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @ParameterizedTest
    @DisplayName("등록된 유저들의 /login 페이지 로그인 성공한다.")
    @CsvSource(value = {
            "user:1111",
            "manager:1111",
            "admin:1111"},
            delimiterString = ":")
    void loginTest(String user, String password) throws Exception {
        performAndExpectLoginSuccess(user, password);
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
