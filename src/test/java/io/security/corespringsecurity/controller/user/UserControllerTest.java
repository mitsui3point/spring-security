package io.security.corespringsecurity.controller.user;

import io.security.corespringsecurity.domain.Account;
import io.security.corespringsecurity.security.common.FormWebAuthenticationDetailsSource;
import io.security.corespringsecurity.security.service.CustomUsersDetailsService;
import io.security.corespringsecurity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static io.security.corespringsecurity.constants.TestDataConstants.REDIRECTED_LOGIN_URL;
import static io.security.corespringsecurity.constants.TestDataConstants.getAdmin;
import static io.security.corespringsecurity.constants.UrlConstant.MYPAGE_URL;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@MockBeans({
        @MockBean(CustomUsersDetailsService.class),
        @MockBean(FormWebAuthenticationDetailsSource.class),
        @MockBean(UserService.class)})//DI 를 위한 MockBean
class UserControllerTest {
    public static final String USER_LOGIN_REGISTER_URL = "user/login/register";

    @Autowired
    WebApplicationContext context;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockBean
    UserService service;

    MockMvc mvc;

    Account admin;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        admin = getAdmin(passwordEncoder.encode("1111"));
    }

    @Test
    @DisplayName("권한없이 /mypage 호출시 /login 으로 redirect 한다.")
    void mypageAccessFailTest() throws Exception {
        //when
        mvc.perform(get(MYPAGE_URL))
                //then
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(REDIRECTED_LOGIN_URL));
    }

    @Test
    @DisplayName("user user /mypage 호출시 정상접근한다.")
    @WithMockUser(username = "user", password = "1111", roles = "USER")
    void mypageAccessTest() throws Exception {
        //when
        mvc.perform(get(MYPAGE_URL))
                .andDo(print())
                //then
                .andExpect(status().isOk())
                .andExpect(view().name("user/mypage"));
    }

    @Test
    @DisplayName("GET /users 호출시 회원가입 페이지로 이동한다.")
    void createUserGetTest() throws Exception {
        //when
        mvc.perform(get("/users"))
                .andDo(print())
                //then
                .andExpect(status().isOk())
                .andExpect(view().name(USER_LOGIN_REGISTER_URL));
    }

    @Test
    @DisplayName("POST /users 호출시 회원가입 로직을 호출한다.")
    void createUserPostTest() throws Exception {
        //when
        mvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("username", admin.getUsername())
                        .param("password", admin.getPassword())
                        .param("email", admin.getEmail())
                        .param("age", admin.getAge())
                        .param("role", admin.getRole()))
                .andDo(print())
                //then
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        verify(service, times(1)).createUser(any());
    }
}