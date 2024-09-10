package com.korit.senicare.filter;

import java.io.IOException;

import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// JWT 검증 및 Security Context에 접근 제어자 추가 필터
// - request의 header에서 토큰 추출 검증
// - security context에 접근 제어자 정보 등록
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

                try {
                    
                    // request 객체에서 Bearer 토큰 추출
                    String token = parseBearerToken(request);
                    if(token == null) {
                        filterChain.doFilter(request, response);
                        return;
                    }

                    // 토큰 검증

                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                filterChain.doFilter(request, response);    // 다음 필터에 전달

    }
    
    // request로부터 토큰 추출
    private String parseBearerToken(HttpServletRequest request) {

        // Request 객체의 Header에서 Authorization 필드 값을 추출
        String authorization = request.getHeader("Authorization");

        // 추출한 authorization 값이 실제로 존재하는 문자열인지 확인(문자열 포함하고 있는지 확인)
        boolean hasAuthorization = StringUtils.hasText(authorization);
        if (!hasAuthorization) return null;

        // Bearer 인증 방식인지 확인
        boolean isBearer = authorization.startsWith("Bearer ");
        if (!isBearer) return null;

        // Authorization 필드 값에서 토큰 추출
        String token = authorization.substring(7);
        return token;
    }
    
}
