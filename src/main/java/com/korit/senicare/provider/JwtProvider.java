package com.korit.senicare.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.security.Key;
import java.nio.charset.StandardCharsets;


// class: JWT 생성 및 검증 기능 제공자
// - JWT 암호화 알고리즘 HS256
// - 비밀키 환경변수에 있는 jwt.secret
// - JWT 만료기간 10시간

@Component  // 인스턴스 생성 권한을 sping.ioc에 넘겨버림
public class JwtProvider {
    
    @Value("${jwt.secret}")      // application.properties에 있는 비밀키 불러옴
    private String secretKey;

    // JWT 생성 메서드
    public String create(String userId) {

        // 만료시간 = 현재 시간 + 10시간
        Date expiredDate = Date.from(Instant.now().plus(10, ChronoUnit.HOURS));

        String jwt = null;

        try {
            // JWT 암호화에 사용할 Key 생성
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));      // 바이트로 변형
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
        return jwt;
    }

}
