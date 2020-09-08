package com.epam.esm.security;

import com.epam.esm.jwt.JwtConfigurer;
import com.epam.esm.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    private static final String LOGIN_ENDPOINT = "/api/v1/auth/login";
    private static final String SIGN_UP_ENDPOINT = "/api/v1/users";
    private static final String FIND_CERTIFICATES_ENDPOINT = "/api/v1/certificates";
    private static final String FIND_TAGS_ENDPOINT = "/api/v1/tags";
    private static final String FIND_CERTIFICATE_BY_ID = "/api/v1/certificates/{\\d+}";

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,LOGIN_ENDPOINT).anonymous()
                .antMatchers(HttpMethod.POST, SIGN_UP_ENDPOINT).anonymous()
                .antMatchers(HttpMethod.GET, FIND_CERTIFICATES_ENDPOINT).permitAll()
                .antMatchers(HttpMethod.GET, FIND_CERTIFICATE_BY_ID).permitAll()
                .antMatchers(HttpMethod.GET, FIND_TAGS_ENDPOINT).permitAll()
                .anyRequest().fullyAuthenticated()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider))
                .and().cors();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("*");
    }
}
