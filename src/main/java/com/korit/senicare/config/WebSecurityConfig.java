package com.korit.senicare.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.korit.senicare.dto.response.ResponseCode;
import com.korit.senicare.dto.response.ResponseMessage;
import com.korit.senicare.filter.JwtAuthenticationFilter;
import com.korit.senicare.handler.OAuth2SuccessHandler;
import com.korit.senicare.service.implement.OAuth2UserServiceImplement;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

// Spring Web 보안 설정
@Configurable       // AutoWired를 달지 못하기에 Web을 spring bean으로 등록하지 않고 sping ioc로 의존성 주입 받을 수 있음
@Configuration      // @Configuration이 달려있어야 Bean으로 사용 가능
@EnableWebSecurity      // security 설정으로 사용 가능
@RequiredArgsConstructor
public class WebSecurityConfig {
    
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2UserServiceImplement oAuth2UserService;

    @Bean
    protected SecurityFilterChain configure(HttpSecurity security) throws Exception {

        security
            // basic 인증 방식 미사용
            .httpBasic(HttpBasicConfigurer::disable)        // 'HttpBasicConfigurer::disable이 사용하지 않겠다는 뜻
            // session 미사용 (유지 X)
            .sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)     // UI가 분리되어 있어 세션으로 유지하기 어렵기에 쿠키로 유지하려 함
            )
            // CSRF 취약점 대비 미지정
            .csrf(CsrfConfigurer::disable)
            // CORS 정책 설정 (아래 configurationSource()에 분리하여 작성)
            .cors(cors -> cors.configurationSource(configurationSource()))         // restAPI가 아니라 html이라면 모든 요청이 같은 출처에서 전송될 것이기에 사용할 필요 없으나, restAPI 사용 예정이므로 어떤 url에서 전송하는지 받을 수 있도록 함
            // URL 패턴 및 HTTP 메서드에 따라 인증 및 인가 여부 지정
            .authorizeHttpRequests(request -> request
                .requestMatchers("/api/v1/auth/**", "/oauth2/callback/*", "/file/*", "/").permitAll()       // "/file/*" 추가 작성 안 해주면 파일 post 할 때 AF 에러
                .anyRequest().authenticated()
            )
            // 인증 및 인가 작업 중 발생하는 예외 처리
            .exceptionHandling(exception -> exception
            .authenticationEntryPoint(new AuthenticationFailEntryPoint())
            )

            // oAuth2 로그인 적용
            .oauth2Login(oauth2 -> oauth2
                .redirectionEndpoint(endpoint -> endpoint.baseUri("/oauth2/callback/*"))
                .authorizationEndpoint(endpoint -> endpoint.baseUri("/api/v1/auth/sns-sign-in"))
                .userInfoEndpoint(endpoint -> endpoint.userService(oAuth2UserService))
                .successHandler(oAuth2SuccessHandler)
                )
            // 필터 등록
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

            return security.build();

    }

    @Bean
    protected CorsConfigurationSource configurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");         // 결론적으로 모든 곳에서 허용하겠다는 의미

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);         // 전체(모든) 곳에서 시작하도록 "/**" 작성

        return source;

    }

}

class AuthenticationFailEntryPoint implements AuthenticationEntryPoint {

    // @Override
    // public void commence(HttpServletRequest request, HttpServletResponse response,
    //                      AuthenticationException authException) throws IOException, ServletException {

    //     authException.printStackTrace();
    //     response.setContentType("application/json");
    //     response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    //     response.getWriter().write(
    //         "{ \"code\": \"" + ResponseCode.AUTHENTICATION_FAIL + "\", \"message\": \"" + ResponseMessage.AUTHENTICATION_FAIL + "\" }"
    //     );

    //     // { "code": "AF", "message": "Authentication fail." }
    // }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
                
                authException.printStackTrace();
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(
                    "{ \"code\": \"" + ResponseCode.AUTHENTICATION_FAIL + "\", \"message\": \"" + ResponseMessage.AUTHENTICATION_FAIL + "\" }"
                );
        
    }

}