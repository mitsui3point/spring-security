package io.security.corespringsecurity.security.common;

import io.security.corespringsecurity.security.service.CustomUsersDetailsService;
import io.security.corespringsecurity.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest
@MockBeans({
        @MockBean(UserService.class),
        @MockBean(FormWebAuthenticationDetailsSource.class),
        @MockBean(CustomUsersDetailsService.class)})
public class FormWebAuthenticationDetailsTest {

    FormWebAuthenticationDetails formWebAuthenticateDetails;

    @Test
    @DisplayName("FormWebAuthenticateDetails 의 생성자가 요청 파라미터의 secret_key 를 저장한다.")
    void webAuthenticateDetailsConstructorSaveParameter() {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        String expected = "secretKey";
        request.setParameter("secret_key", expected);
        //when
        formWebAuthenticateDetails = new FormWebAuthenticationDetails(request);
        String actual = formWebAuthenticateDetails.getSecretKey();
        //then
        assertThat(formWebAuthenticateDetails).isInstanceOf(WebAuthenticationDetails.class);
        assertThat(actual).isEqualTo(expected);
    }
}
