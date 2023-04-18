package io.security.corespringsecurity.controller;

import io.security.corespringsecurity.security.service.CustomUsersDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static io.security.corespringsecurity.constants.UrlConstant.ROOT_URL;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = HomeController.class)
class HomeControllerTest {
    public static final String HOME_URL = "home";
    @Autowired
    WebApplicationContext context;

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
    @DisplayName("/ 호출시 접근할수 viewname home 을 호출한다.")
    void home() throws Exception {
        mvc.perform(get(ROOT_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(HOME_URL));
    }
}