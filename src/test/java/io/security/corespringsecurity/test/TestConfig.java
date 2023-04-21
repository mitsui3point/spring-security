package io.security.corespringsecurity.test;

import io.security.corespringsecurity.repository.UserRepository;
import io.security.corespringsecurity.security.common.FormWebAuthenticationDetailsSource;
import io.security.corespringsecurity.security.configs.SecurityConfig;
import io.security.corespringsecurity.security.handler.CustomAuthenticationSuccessHandler;
import io.security.corespringsecurity.security.service.CustomUsersDetailsService;
import io.security.corespringsecurity.service.UserService;
import io.security.corespringsecurity.service.UserServiceImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
public class TestConfig {
    @MockBean
    UserRepository userRepository;

    @Bean
    public SecurityConfig securityConfig() {
        return new SecurityConfig(customUsersDetailsService(), formWebAuthenticationDetailsSource(), customAuthenticationSuccessHandler());
    }

    @Bean
    public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler() {
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
