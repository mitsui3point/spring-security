package io.security.corespringsecurity.security.handler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Form 인증 - Access Denied
 *
 * 인증을 시도하다가 발생한 예외는 인증 필터{@link AbstractAuthenticationProcessingFilter} 가 받게된다.
 *
 * 인증을 성공.
 *      그 후 자원에 접근하려고 할때, 자원에 대한 권한이 없는(아닌)경우, 그떄에는 인가 예외는 인가 필터(ex. {@link ExceptionTranslationFilter})
 *
 * 인가 예외는 어떤 필터가 발생시키냐
 *      : {@link FilterSecurityInterceptor} 의 {@link AbstractSecurityInterceptor}
 *      => accessDecisionManager 시도 실패
 *      => throw {@link AccessDeniedException}
 *      => {@link ExceptionTranslationFilter#handleSpringSecurityException(HttpServletRequest, HttpServletResponse, FilterChain, RuntimeException)} (인증예외 혹은 인가예외를 처리하는 필터)로 throw 되어 처리된다.
 *      => 해당 메서드는 {@link AuthenticationException}(인증 예외) 과 {@link AccessDeniedException}(인가 예외) 을 처리한다.
 *      => 해당 메서드 안에 {@link AccessDeniedHandler#handle(HttpServletRequest, HttpServletResponse, AccessDeniedException)} 메서드가 accessDenideHandler 객체에서 예외처리를 하도록 예외를 전달
 *
 * 인증시도 시 예외
 *      => 해당인증을 처리하고 있는 필터가 받아서 처리
 *
 * 인가예외
 *      => {@link ExceptionTranslationFilter} 가 {@link AccessDeniedHandler} 에 전달하여 처리
 *
 * 우리는 인가예외를 처리하기 위해
 *      {@link AccessDeniedHandler} 구현체 를 만들어서,
 *      설정 클래스에 {@link AccessDeniedHandler} 구현체를 설정하고,
 *      스프링 시큐리티가 인가 예외가 발생하였을 경우에 우리가 만든 {@link AccessDeniedHandler} 구현체 를 호출해서
 *      그 handler 안에서 우리가 관련된 작업들을 처리할 수가 있다.
 */
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private String errorPage;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String deniedUrl = errorPage + "?exception=" + accessDeniedException.getMessage();
        response.sendRedirect(deniedUrl);
    }

    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }
}
