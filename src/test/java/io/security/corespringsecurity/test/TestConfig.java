package io.security.corespringsecurity.test;

import io.security.corespringsecurity.repository.UserRepository;
import io.security.corespringsecurity.security.common.FormWebAuthenticationDetailsSource;
import io.security.corespringsecurity.security.configs.SecurityConfig;
import io.security.corespringsecurity.security.handler.CustomAccessDeniedHandler;
import io.security.corespringsecurity.security.handler.CustomAuthenticationFailureHandler;
import io.security.corespringsecurity.security.handler.CustomAuthenticationSuccessHandler;
import io.security.corespringsecurity.security.service.CustomUsersDetailsService;
import io.security.corespringsecurity.service.UserService;
import io.security.corespringsecurity.service.UserServiceImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import static io.security.corespringsecurity.constants.UrlConstant.DENIED_URL;

@TestConfiguration
public class TestConfig {
    @MockBean
    UserRepository userRepository;

    @Bean
    public SecurityConfig securityConfig() {
        return new SecurityConfig(
                customUsersDetailsService(),
                formWebAuthenticationDetailsSource(),
                customAuthenticationSuccessHandler(),
                customAuthenticationFailureHandler()
        );
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        CustomAccessDeniedHandler customAccessDeniedHandler = new CustomAccessDeniedHandler();
        customAccessDeniedHandler.setErrorPage(DENIED_URL);
        return customAccessDeniedHandler;
    }

    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    public FormWebAuthenticationDetailsSource formWebAuthenticationDetailsSource() {
        return new FormWebAuthenticationDetailsSource();
    }

    @Bean
    public CustomUsersDetailsService customUsersDetailsService() {
        return new CustomUsersDetailsService(userRepository);
    }

    @Bean
    public UserService userService() {
        return new UserServiceImpl(userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return securityConfig().passwordEncoder();
    }
}
