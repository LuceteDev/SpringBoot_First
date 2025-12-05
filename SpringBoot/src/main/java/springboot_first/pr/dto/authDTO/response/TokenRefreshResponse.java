package springboot_first.pr.dto.authDTO.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Access Token 재발급 요청에 대한 응답 DTO입니다.
 * 클라이언트에게 새로 발급된 Access Token을 전달합니다.
 */
@Getter
@Builder
@ToString
public class TokenRefreshResponse {
    
    // 새로 발급된 Access Token
    private String accessToken;

    // 토큰 유형 (Bearer 등)
    @Builder.Default
    private String tokenType = "Bearer";

    // Access Token의 만료 시간 (ms 단위)
    private long expiresIn;
}