package io.security.corespringsecurity.security.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static io.security.corespringsecurity.constants.UrlConstant.ROOT_URL;

/**
 * {@link AuthenticationSuccessHandler} : 인증 성공시 호출되는 객체
 *
 * {@link RequestCache}
 *      : 요청 캐시
 *      : 이 객체를 사용해서 실제로 사용자가 인증에 성공한 이후에 그다음 이동할 페이지로 설정을 하려고 함
 *      : 사용자가 인증에 성공하지 못한상태
 *              => 로그인 페이지(/login?error)로 가고 (인증예외가 발생해서 로그인 페이지로)
 *      : 인증에 성공
 *              => 사용자가 원래 가고자 했던 url 정보를 담고있는 요청 캐시{@link RequestCache}를 조회
 *              => ({@link CustomAuthenticationSuccessHandler} extends {@link SimpleUrlAuthenticationSuccessHandler}) 에서 바로 이동할 수 있도록 처리하는 로직 작성
 *
 * {@link RedirectStrategy}
 *      : 최종 목적한 url 로 redirect 하기 위해 사용
 *
 * {@link AuthenticationSuccessHandler#onAuthenticationSuccess(HttpServletRequest, HttpServletResponse, Authentication)}
 *      : request, response, authentication(인증에 성공한 이후의 객체) 객체들이 있으므로 여러가지 작업이 가능
 *
 * {@link RequestCache#getRequest(HttpServletRequest, HttpServletResponse)}
 *      : 세션에 로그인이 성공하기 이전 요청 정보
 *      : 이전에 인증없이 인증이 필요한 페이지 접근 후 /login 으로 redirect 되었던 기록등을 저장하는 객체
 *      : 이전에 정보가 없는경우에는 이 객체에 정보가 없음.
 */
@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        setDefaultTargetUrl(ROOT_URL);
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        redirectStrategy = getRedirectStrategy();
        if (savedRequest != null) {
            redirectStrategy.sendRedirect(request, response, savedRequest.getRedirectUrl());
        }
        if (savedRequest == null) {
            redirectStrategy.sendRedirect(request, response, getDefaultTargetUrl());
        }
    }
}
