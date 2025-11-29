package springboot_first.pr.security;

import springboot_first.pr.entity.User;

// 💡 [현업 패턴] 토큰 생성/유효성 검증 등의 보안 로직을 담당하는 인터페이스
public interface TokenProvider {
    
    /**
     * 사용자 정보를 기반으로 Access Token을 생성합니다.
     * @param user 인증된 사용자 Entity
     * @return 생성된 Access JWT 문자열
     */
    String createAccessToken(User user);
    
    /**
     * 사용자 정보를 기반으로 Refresh Token을 생성합니다.
     * @param user 인증된 사용자 Entity
     * @return 생성된 Refresh JWT 문자열
     */
    String createRefreshToken(User user);
    
    /**
     * 주어진 JWT 토큰의 유효성을 검증하고, 토큰 내부에서 userId를 추출합니다.
     * @param token JWT 문자열
     * @return 토큰에서 추출된 사용자 ID (String)
     */
    String getUserIdFromToken(String token);
}