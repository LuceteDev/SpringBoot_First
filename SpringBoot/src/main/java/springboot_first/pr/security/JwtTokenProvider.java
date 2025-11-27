package springboot_first.pr.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import springboot_first.pr.entity.User;

import java.security.Key;
import java.util.Date;

// 💡 JWT 라이브러리를 사용하여 토큰 생성 및 유효성 검증을 수행하는 실제 구현체
@Slf4j
@Service 
public class JwtTokenProvider implements TokenProvider {

    // application.properties에서 주입받을 JWT 비밀 키
    private final Key key;
    // application.properties에서 주입받을 토큰 만료 시간 (밀리초)
    private final long tokenExpirationTime;

    // 생성자를 통해 설정 파일의 값을 주입받고, 비밀 키를 초기화합니다.
    public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey,
                            @Value("${jwt.expiration-time}") long tokenExpirationTime) {
        // Base64 인코딩된 비밀 키 문자열을 바이트 배열로 디코딩하여 Key 객체로 만듭니다.
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.tokenExpirationTime = tokenExpirationTime;
        log.info("JWT TokenProvider 초기화 완료. 만료 시간: {}ms", tokenExpirationTime);
    }

    /**
     * 사용자 정보를 기반으로 Access Token을 생성합니다.
     */
    @Override
    public String createToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenExpirationTime);

        // JWT 생성 로직
        String token = Jwts.builder()
                .setSubject(user.getUserId()) // 토큰의 제목(Subject)으로 userId를 사용
                .setIssuedAt(now) // 토큰 발급 시간
                .setExpiration(expiryDate) // 토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // HS256 알고리즘과 비밀 키로 서명
                .compact();
        
        log.info("JWT Access Token 생성 완료: UserId: {}, 만료 시간: {}", user.getUserId(), expiryDate);
        
        return token;
    }

    /**
     * 토큰에서 userId를 추출하고 토큰의 유효성을 검증합니다.
     */
    @Override
    public String getUserIdFromToken(String token) {
        try {
            // 토큰을 파싱하고 Claims(페이로드)를 추출합니다.
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key) // 서버의 비밀 키로 서명 검증
                    .build()
                    .parseClaimsJws(token) // 토큰 유효성 검사 및 파싱
                    .getBody();

            // Subject에 저장했던 userId를 반환
            return claims.getSubject();
            
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
            throw new IllegalArgumentException("Invalid JWT signature.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
            throw new IllegalArgumentException("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
            throw new IllegalArgumentException("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
            throw new IllegalArgumentException("JWT claims string is empty or invalid.");
        }
    }
}