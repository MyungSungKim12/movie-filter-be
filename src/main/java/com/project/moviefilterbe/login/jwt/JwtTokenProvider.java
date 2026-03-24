package com.project.moviefilterbe.login.jwt;

import com.project.moviefilterbe.login.dto.JwtTokenResponseDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(io.jsonwebtoken.io.Encoders.BASE64.encode(secretKey.getBytes()));
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public JwtTokenResponseDto generateToken(String uiId, String role) {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime accessTokenExpire = now.plusHours(1);
        OffsetDateTime refreshTokenExpire = now.plusHours(10);
        Date accessDate = Date.from(accessTokenExpire.toInstant());
        Date refreshDate = Date.from(refreshTokenExpire.toInstant());

        String accessToken = Jwts.builder()
                .setSubject(uiId)
                .claim("role", role)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(accessDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(uiId)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(refreshDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtTokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpires(accessTokenExpire.toInstant().toEpochMilli())
                .refreshTokenExpires(refreshTokenExpire.toInstant().toEpochMilli())
                .accessTokenExpiresDate(accessTokenExpire)
                .refreshTokenExpiresDate(refreshTokenExpire)
                .build();
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Object roleClaim = claims.get("role");
        Collection<? extends GrantedAuthority> authorities;
        if (roleClaim != null && StringUtils.hasText(roleClaim.toString())) {
            authorities = Arrays.stream(roleClaim.toString().split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } else {
            authorities = Collections.singletonList(new SimpleGrantedAuthority("USER"));
        }

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            System.out.println("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            System.out.println("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            System.out.println("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}