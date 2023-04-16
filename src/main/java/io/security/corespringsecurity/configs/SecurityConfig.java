package io.security.corespringsecurity.configs;

import org.springframework.boot.autoconfigure.security.StaticResourceLocation;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.autoconfigure.security.servlet.StaticResourceRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import static io.security.corespringsecurity.constants.RoleConstant.*;
import static io.security.corespringsecurity.constants.UrlConstant.*;
import static io.security.corespringsecurity.constants.UserConstant.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


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
     *
     * {@link PasswordEncoderFactories#createDelegatingPasswordEncoder()}
     *      : 여러개의 {@link PasswordEncoder} 유형을 선언한 뒤, 상황에 맞게 선택해서 사용할 수 있도록 지원하는 Encoder 이다.
     *
     * 암호화 포맷
     *      : {id}encodedPassword
     *      : bcrypt, noop, pbkdf2, scrypt, sha256 (기본 포맷은 Bcrypt : {bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG)
     *
     * {@link PasswordEncoder}
     *      : encode(password); 패스워드 암호화
     *      : matches(rawPassword, encodedPassword); 패스워드 비교
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * web static resources 들은 security filter 를 거치지 않고 통과가 된다.
     * 목록
     *      {@link StaticResourceRequest#atCommonLocations()} => {@link StaticResourceLocation}
     * HttpSecurity.antMatchers() vs WebSecurity.ignoring() 차이
     *      antMathcers() : {@link FilterSecurityInterceptor#invoke(FilterInvocation)} => InterceptorStatusToken token = super.beforeInvocation(fi); 로직을 수행함
     *      ignoring(): {@link FilterSecurityInterceptor#invoke(FilterInvocation)} => InterceptorStatusToken token = super.beforeInvocation(fi); 로직을 수행하지 않음
     * 
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .requestMatchers(PathRequest
                        .toStaticResources()
                        .atCommonLocations());
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

                .antMatchers(ROOT_URL, USERS_URL).permitAll()
                .antMatchers(MYPAGE_URL).hasRole(USER_ROLE)
                .antMatchers(MESSAGES_URL).hasRole(MANAGER_ROLE)
                .antMatchers(CONFIG_URL).hasRole(ADMIN_ROLE)

                .anyRequest()
                .authenticated()

                .and()
                .formLogin();//기본 인증방식; form login
    }
}
