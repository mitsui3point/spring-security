package io.security.corespringsecurity.configs;

import antlr.BaseAST;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String USER_ID = "user";
    public static final String PASSWORD = "1111";
    public static final String USER_ROLE = "USER";
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String ADMIN_ID = "admin";
    public static final String MANAGER_ID = "manager";
    public static final String MANAGER_ROLE = "MANAGER";
    public static final String MYPAGE_URL = "/mypage";
    public static final String MESSAGES_URL = "/messages";
    public static final String CONFIG_URL = "/config";
    public static final String ROOT_URL = "/";

    /**
     * 현재 이 웹 서버가 제공하는 http 요청을 접근할 수 있는 user 목록
     *
     * @param auth the {@link AuthenticationManagerBuilder} to use
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        //PasswordEncoder 라는 클래스가 있다. password 암호화된 방식으로 저장 및 조회해야 한다.(그래야 오류가 발생하지 않는다.)
        String password = passwordEncoder().encode(PASSWORD);

        auth.inMemoryAuthentication()
                .withUser(USER_ID)
                .password(password)
                .roles(USER_ROLE);

        auth.inMemoryAuthentication()
                .withUser(MANAGER_ID)
                .password(password)
                .roles(MANAGER_ROLE);

        auth.inMemoryAuthentication()
                .withUser(ADMIN_ID)
                .password(password)
                .roles(ADMIN_ROLE);
    }

    /**
     * 평문인 password 를 encode 할 수 있는 method 를 제공하는 spring bean 이 등록된다.
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * 현재 이 웹 서버가 제공하는 http 요청에 대한 인증설정
     * @param http the {@link HttpSecurity} to modify
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //현재 이 시스템에 요청으로 접근하려면 모든 요청에 대해 인증을 요구함
        http
                .authorizeRequests()

                .antMatchers(ROOT_URL).permitAll()
                .antMatchers(MYPAGE_URL).hasRole(USER_ROLE)
                .antMatchers(MESSAGES_URL).hasRole(MANAGER_ROLE)
                .antMatchers(CONFIG_URL).hasRole(ADMIN_ROLE)

                .anyRequest()
                .authenticated()

                .and()
                .formLogin();//기본 인증방식; form login
    }
}
