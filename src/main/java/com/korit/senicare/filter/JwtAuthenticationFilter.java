package com.korit.senicare.filter;

import java.io.IOException;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.korit.senicare.provider.JwtProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

// JWT 검증 및 Security Context에 접근 제어자 추가 필터
// - request의 header에서 토큰 추출 검증
// - security context에 접근 제어자 정보 등록
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;      // final이 붙은 필수 멤버 변수들에 대해서만 생성자를 만들도록 함

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
                    String userId = jwtProvider.validate(token);
                    if (userId == null) {
                        filterChain.doFilter(request, response);
                        return;
                    }

                    // security context에 등록
                    setContext(request, userId);


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

    // security context 생성 및 등록
    private void setContext(HttpServletRequest request, String userId) {

        // 접근 주체에 대한 인증 토큰 생성
        AbstractAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(userId,null, AuthorityUtils.NO_AUTHORITIES);        // 접근 주체에 대한 정보를 userId로 잡고, 비밀번호는 따로 사용하지 않을 예정이라 null 기입, 일단 권한은 없다고 기입

        // 생성한 인증 토큰이 어떤 요청에 대한 내용인 상세 정보 추가
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // 빈 security context 생성
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();       // 빈 영역이라 empty 사용

        // 생성한 빈 security context에 authenticationToken 주입
        securityContext.setAuthentication(authenticationToken);

        // 생성한 security context 등록
        SecurityContextHolder.setContext(securityContext);
        
    }
    
}
