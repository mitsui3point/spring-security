package io.security.corespringsecurity.security.common;

import io.security.corespringsecurity.security.service.CustomUsersDetailsService;
import io.security.corespringsecurity.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest
@MockBeans({
        @MockBean(UserService.class),
        @MockBean(CustomUsersDetailsService.class)})
@Import(FormWebAuthenticationDetailsSource.class)
public class FormWebAuthenticationDetailsSourceTest {
    @Autowired
    FormWebAuthenticationDetailsSource formWebAuthenticateDetailsSource;

    @Test
    @DisplayName("FormWebAuthenticateDetailsSource 가 FormWebAuthenticateDetails 를 호출한다.")
    void invokeFormWebAuthenticateDetails() {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        String expected = "secretKey";
        request.setParameter("secret_key", expected);

        //when
        FormWebAuthenticationDetails authenticationDetails = (FormWebAuthenticationDetails) formWebAuthenticateDetailsSource.buildDetails(request);
        String actual = authenticationDetails.getSecretKey();

        //then
        assertThat(actual).isEqualTo(expected);
    }
}
