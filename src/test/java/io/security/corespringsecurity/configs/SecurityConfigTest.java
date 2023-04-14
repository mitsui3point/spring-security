package io.security.corespringsecurity.configs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SecurityConfigTest {

    @Autowired
    ApplicationContext context;

    @Test
    void registeredConfigBean() {
        SecurityConfig securityConfig = context.getBean(SecurityConfig.class);
        //then
        assertThat(securityConfig).isNotNull();
        assertThat(securityConfig).isInstanceOf(WebSecurityConfigurerAdapter.class);
    }
}
