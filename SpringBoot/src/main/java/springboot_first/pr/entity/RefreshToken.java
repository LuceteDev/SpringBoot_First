package springboot_first.pr.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 1️⃣ 어노테이션 선언
@Entity // 해당 클래스가 엔티티임을 선언
@Getter // 필드 값을 조회할 수 있는 Getter 메서드 자동 생성
// 💡 @AllArgsConstructor의 접근 제한자를 PRIVATE으로 설정하여 Builder를 통한 생성만 허용 (권장)
@AllArgsConstructor(access = AccessLevel.PRIVATE) 
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 사용을 위해 PROTECTED 접근 레벨의 기본 생성자 필요
@Builder // 빌더 패턴 자동 생성
@Slf4j // 로깅 추가

public class RefreshToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // JWT의 Subject와 동일한 사용자 ID를 저장 (1:1 관계를 unique = true로 보장)
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    // Refresh Token 자체의 문자열. JWT는 길이가 길어 500자 확보.
    @Column(nullable = false, length = 500)
    private String tokenValue;

    /**
     * 💡 정적 팩토리 메서드: RefreshToken 객체 생성을 캡슐화합니다.
     * @param userId JWT의 Subject가 될 사용자 ID
     * @param tokenValue 생성된 Refresh Token 문자열
     * @return RefreshToken 객체
     */
    public static RefreshToken of(String userId, String tokenValue) {
        log.info("RefreshToken.of() 호출. userId: {}", userId);
        return RefreshToken.builder()
                     .userId(userId)
                     .tokenValue(tokenValue)
                     .build();
    }
    
    /**
     * 토큰 값을 갱신하는 메서드 (리프레시 시 재활용)
     * 이 메서드가 엔티티의 비즈니스 로직을 표현합니다.
     * @param newTokenValue 새로 발급된 Refresh Token 문자열
     */
    public void updateToken(String newTokenValue) {
        this.tokenValue = newTokenValue; // 👈 객체의 상태를 직접 변경
        log.debug("RefreshToken 값 업데이트 완료. User ID: {}", this.userId);
    }
}