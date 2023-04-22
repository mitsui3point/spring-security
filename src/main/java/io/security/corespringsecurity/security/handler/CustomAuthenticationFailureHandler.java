package io.security.corespringsecurity.security.handler;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * {@link CustomAuthenticationFailureHandler}: 인증 검증할때 인증 실패
 *      => 인증 예외(ex. {@link AuthenticationProvider}, {@link UserDetailsService} 등 검증 실패로 인한 예외 발생시)가 발생했을 경우 인증 필터({@link AuthenticationFilter}가 예외를 받고, 예외를 파라미터에 담아
 *      {@link CustomAuthenticationFailureHandler#onAuthenticationFailure(HttpServletRequest, HttpServletResponse, AuthenticationException)} 메서드를 호출함. 이때 호출되는 객체
 */
@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String errorMessage = "Invalid Username or Password";
        if (exception instanceof InsufficientAuthenticationException) {
            errorMessage = "Invalid Secret Key";
        }
        if (!(exception instanceof InsufficientAuthenticationException)) {
            errorMessage = "Invalid Username or Password";
        }
        super.setDefaultFailureUrl("/login?error=true&exception=" + errorMessage);
        super.onAuthenticationFailure(request, response, exception);
    }
}
