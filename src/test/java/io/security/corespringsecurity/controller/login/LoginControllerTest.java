package io.security.corespringsecurity.controller.login;

import io.security.corespringsecurity.repository.UserRepository;
import io.security.corespringsecurity.security.configs.SecurityConfig;
import io.security.corespringsecurity.security.service.CustomUsersDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static io.security.corespringsecurity.constants.UrlConstant.LOGIN_URL;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(LoginController.class)
public class LoginControllerTest {

    @Autowired
    WebApplicationContext context;
    @MockBean
    CustomUsersDetailsService customUsersDetailsService;
    @MockBean
    UserRepository userRepository;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithAnonymousUser
    void customLoginPageTest() throws Exception {
        //when
        mvc.perform(get(LOGIN_URL))
                .andDo(print())
                //then
                .andExpect(view().name("user/login/login"));
    }
}
