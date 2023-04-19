package io.security.corespringsecurity.security.provider;

import io.security.corespringsecurity.security.service.AccountContext;
import io.security.corespringsecurity.security.service.CustomUsersDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;

/**
 * 사용자가 입력한 정보로 추가적인 인증절차를 SecurityConfig 에 설정하는 Service
 */
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final CustomUsersDetailsService customUsersDetailsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 추가적인 인증절차를 진행(id/password...)
     * 인증절차 이후 {@link UsernamePasswordAuthenticationToken} 으로 토큰을 생성
     *
     * @param authentication the authentication request object.(사용자가 입력한 id/password 등 인증에 필요한 정보가 넘어오는 인수)
     *
     * {@link UsernamePasswordAuthenticationToken#UsernamePasswordAuthenticationToken(Object, Object)}
     *  : authorities(권한정보) null
     *  : principle(사용자정보), credential(패스워드) 만 정보가 저장됨.
     *  : setAuthenticated(false)
     *  : 사용자가 인증을 시도할때, 첫 로그인을 해서 인증을 처리할때 인증 필드가 사용자정보나 패스워드를 {@link AuthenticationManager} 에 전달할때 사용
     *
     * {@link UsernamePasswordAuthenticationToken#UsernamePasswordAuthenticationToken(Object, Object, Collection)}
     *  : authorities(권한정보)
     *  : principle(사용자정보), credential(패스워드)
     *  : setAuthenticated(true)
     *  : 최종적으로 인증에 성공한 이후에는, 해당 생성자에게 정보를 전달하면 된다.
     *  : 여기에서는 이 생성자를 사용해서 {@link AuthenticationProvider}(현재 이곳) 을 호출한 {@link AuthenticationManager} 에 값을 리턴한다.
     *
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        AccountContext accountContext = (AccountContext) customUsersDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, accountContext.getAccount().getPassword())) {
            throw new BadCredentialsException("BadCredentialsException");
        }

        return new UsernamePasswordAuthenticationToken(
                accountContext,
                null,
                accountContext.getAuthorities());
    }

    /**
     * 전달받은 authentication 의 {@link UsernamePasswordAuthenticationToken} 과 현재 발급받은 {@link UsernamePasswordAuthenticationToken} 이 일치하는지 확인하는 메서드
     *
     * @param authentication
     *
     * @return
     */
    @Override
    public boolean supports(Class<?> authentication) {
        //AuthenticationProvider(Impl;ProviderManager#authenticate if(!provider.supports(TargetClass.class))
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
