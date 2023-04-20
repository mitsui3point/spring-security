package io.security.corespringsecurity.controller.login;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login() {
        return "user/login/login";
    }

    /**
     * 로그아웃 방법
     *      - {@code <form>} 태그를 사용해서 POST 로 요청
     *          ; {@link LogoutFilter} 가 POST 로그아웃 요청을 처리함
     *      - {@code <a>} 태크를 사용해서 GET 으로 요청 – {@link SecurityContextLogoutHandler} 활용
     *          ; {@link LogoutFilter} 가 POST 로그아웃 요청을 처리하지 않고, {@link SecurityContextLogoutHandler} 를 활용하여 자체적으로 로그아웃을 할 수 있는 기능을 구현해야 로그아웃 처리가 가능함
     *
     * 인증 여부에 따라 로그인/로그아웃 표현(타임리프에서 제공하는 메서드; 태그노출에 자주 사용되는 메서드)
     *      {@code <li sec:authorize="isAnonymous()"><a th:href="@{/login}">로그인</a></li>}
     *      {@code <li sec:authorize="isAuthenticated()"><a th:href="@{/logout}">로그아웃</a></li>}
     *
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();//인증객체
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/login";
    }

}
