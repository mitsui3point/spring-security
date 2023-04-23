package io.security.corespringsecurity.security.handler;

import io.security.corespringsecurity.test.TestConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@WebMvcTest
@ExtendWith(MockitoExtension.class)
@Import(TestConfig.class)
public class CustomAccessDeniedHandlerTest {
    @Autowired
    @InjectMocks
    CustomAccessDeniedHandler customAccessDeniedHandler;

    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    AccessDeniedException exception;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
    }

    @Test
    @DisplayName("CustomAccessDeniedHandler 타입이 AccessDeniedHandler 이다.")
    void instanceOf() {
        boolean actualType = customAccessDeniedHandler instanceof AccessDeniedHandler;
        Assertions.assertThat(actualType).isTrue();
    }

    @Test
    @DisplayName("handler 메서드가 호출될 때, redirect 를 진행한다.")
    void handlerTest() throws IOException, ServletException {
        //given
        doNothing().when(response).sendRedirect(any());
        given(exception.getMessage()).willReturn("invalid access user");
        //when
        customAccessDeniedHandler.setErrorPage("/denied");
        customAccessDeniedHandler.handle(request, response, exception);
        //then
        verify(response, times(1)).sendRedirect(any());
        verify(exception, times(1)).getMessage();
    }
}
