package io.security.corespringsecurity.controller.user;

import io.security.corespringsecurity.domain.Account;
import io.security.corespringsecurity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import static io.security.corespringsecurity.constants.UrlConstant.MYPAGE_URL;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@ExtendWith(SpringExtension.class)
class UserControllerTest {
    @Autowired
    WebApplicationContext context;

    @MockBean
    UserService service;

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

    @ParameterizedTest
    @DisplayName("POST /users 호출시 회원가입 로직을 호출한다.")
    @CsvSource(value = {"username,1111,email@email.com,11,USER"}, delimiterString = ",")
    void createUserTest(
            String username,
            String password,
            String email,
            String age,
            String role) throws Exception {
        //given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", username);
        params.add("password", password);
        params.add("email", email);
        params.add("age", age);
        params.add("role", role);
        //when
        mvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .params(params))
                .andDo(print())
                //then
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        verify(service, times(1)).createUser(any());
    }
}