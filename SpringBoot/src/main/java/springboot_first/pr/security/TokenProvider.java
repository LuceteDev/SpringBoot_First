package springboot_first.pr.security;

import springboot_first.pr.entity.User;

// 💡 [현업 패턴] 토큰 생성/유효성 검증 등의 보안 로직을 담당하는 인터페이스
public interface TokenProvider {
    
    /**
     * 사용자 정보를 기반으로 인증 토큰을 생성합니다.
     * @param user 인증된 사용자 Entity
     * @return 생성된 JWT 문자열
     */
    String createToken(User user);
    
}