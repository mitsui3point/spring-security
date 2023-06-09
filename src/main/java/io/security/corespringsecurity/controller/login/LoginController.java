package io.security.corespringsecurity.controller.login;

import io.security.corespringsecurity.domain.Account;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "exception", required = false) String exception,
                        Model model) {
        model.addAttribute("error", error);
        model.addAttribute("exception", exception);

        return "user/login/login";
    }

    /**
     * 로그아웃 방법
     * - {@code <form>} 태그를 사용해서 POST 로 요청
     * ; {@link LogoutFilter} 가 POST 로그아웃 요청을 처리함
     * - {@code <a>} 태크를 사용해서 GET 으로 요청 – {@link SecurityContextLogoutHandler} 활용
     * ; {@link LogoutFilter} 가 POST 로그아웃 요청을 처리하지 않고, {@link SecurityContextLogoutHandler} 를 활용하여 자체적으로 로그아웃을 할 수 있는 기능을 구현해야 로그아웃 처리가 가능함
     * <p>
     * 인증 여부에 따라 로그인/로그아웃 표현(타임리프에서 제공하는 메서드; 태그노출에 자주 사용되는 메서드)
     * {@code <li sec:authorize="isAnonymous()"><a th:href="@{/login}">로그인</a></li>}
     * {@code <li sec:authorize="isAuthenticated()"><a th:href="@{/logout}">로그아웃</a></li>}
     *
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();//인증객체
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);//session.invalidate();, 인증객체 비움
        }
        return "redirect:/login";
    }

    @GetMapping("/denied")
    public String accessDenied(@RequestParam(value = "exception", required = false) String exception,
                               Model model) {
        //현재 사용자 이름
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        Account account = new ModelMapper().map(authentication.getPrincipal(), Account.class);

        //사용자가 자원을 체크하지 못하는 message 출력
        model.addAttribute("username", account.getUsername());
        model.addAttribute("exception", exception);
        return "user/login/denied";
    }
}
