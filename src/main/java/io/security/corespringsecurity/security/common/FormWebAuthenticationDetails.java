package io.security.corespringsecurity.security.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * {@link WebAuthenticationDetails}
 *      사용자가 전송하는 인증 외 추가적인 파라미터들을 저장하는 클래스
 *      인증 과정 중 전달된 데이터를 저장
 *      {@link Authentication} 의 details 속성에 저장
 */
public class FormWebAuthenticationDetails extends WebAuthenticationDetails {

    private String secretKey;
    public FormWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        secretKey = request.getParameter("secret_key");
    }

    public String getSecretKey() {
        return secretKey;
    }
}
