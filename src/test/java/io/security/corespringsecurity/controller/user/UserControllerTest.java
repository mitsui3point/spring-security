package io.security.corespringsecurity.controller.user;

import io.security.corespringsecurity.domain.AccountDto;
import io.security.corespringsecurity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import static io.security.corespringsecurity.constants.UrlConstant.MYPAGE_URL;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(controllers = UserController.class)
@SpringBootTest
class UserControllerTest {
    @Autowired
    WebApplicationContext context;

//    @InjectMocks
//    UserController userController;
//
//    @MockBean
//    UserService userService;
//
//    @MockBean
//    PasswordEncoder passwordEncoder;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("권한없이 /mypage 호출시 /login 으로 redirect 한다.")
    void mypageAccessFailTest() throws Exception {
        //when
        mvc.perform(get(MYPAGE_URL))
                //then
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
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
    void createUserTest() throws Exception {
        //when
        mvc.perform(get("/users"))
                .andDo(print())
                //then
                .andExpect(status().isOk())
                .andExpect(view().name("user/login/register"));
    }
}