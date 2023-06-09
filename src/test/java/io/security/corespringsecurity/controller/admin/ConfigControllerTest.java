package io.security.corespringsecurity.controller.admin;

import io.security.corespringsecurity.test.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static io.security.corespringsecurity.constants.TestDataConstants.REDIRECTED_LOGIN_URL;
import static io.security.corespringsecurity.constants.UrlConstant.CONFIG_URL;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConfigController.class)
@Import(TestConfig.class)
class ConfigControllerTest {

    public static final String ADMIN_CONFIG_URL = "admin/config";
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
    @DisplayName("권한없이 /config 호출시 /login 으로 redirect 한다.")
    void configAccessFailTest() throws Exception {
        //when
        mvc.perform(get(CONFIG_URL))
                //then
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(REDIRECTED_LOGIN_URL));
    }

    @Test
    @DisplayName("admin user /config 호출시 정상접근한다.")
    @WithMockUser(username = "admin", password = "1111", roles = "ADMIN")
    void configAccessTest() throws Exception {
        //when
        mvc.perform(get(CONFIG_URL)).andDo(print())
                //then
                .andExpect(status().isOk())
                .andExpect(view().name(ADMIN_CONFIG_URL));
    }
}