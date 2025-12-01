package springboot_first.pr.dto.userDTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import springboot_first.pr.entity.User;

// ⚠️ 로그인 성공 시 응답 DTO (현업 표준: JWT 토큰 포함)
// 1️⃣ 어노테이션 선언
@AllArgsConstructor // 모든 필드를 매개변수로 갖는 생성자 자동 생성
@NoArgsConstructor // 매개변수가 아예 없는 기본 생성자 자동 생성
@Getter // 각 필드 값을 조회할 수 있는 Getter 메서드 자동 생성
@ToString // 모든 필드를 출력할 수 있는 toString 메서드 자동 생성, ✅ 로깅과 디버깅을 위해 추가
@Builder // DTO 생성을 위한 빌더 패턴 추가 (테스트 코드 작성에 용이하다고 한다 ✅)
@Slf4j // 로깅 사용 -> 이 로깅 메시지에 객체의 상태를 담기 위해 @ToString을 함께 사용

public class UserLoginResponse {
    // ✅ 로그인 응답 ⚠️ 비밀번호 제외, 조회한 id 포함

    // ✅ [필수] 인증 토큰 - ⚠️ 반환 금지 ❌
    private String accessToken;
    
    private String refreshToken; 

    // ✅ [실용] 사용자 식별 정보
    private Long id; // 자동증가 기본키. 클라이언트 상태 관리 및 조회에 유용.
    private String userId; // 아이디 (외부에 노출되는 식별자)
    private String username; // ⚠️ 사용자 이름 (로그인 직후 환영 메시지 등에 사용)

    // ⚠️ 보안 및 실용성 때문에 email, phoneNumber는 생략합니다.

    // 💡 [현업 패턴] Service 계층에서 Entity와 토큰을 받아 응답 DTO로 변환하는 정적 팩토리 메서드
    public static UserLoginResponse from(User user, String accessToken, String refreshToken) { // ✅ 토큰 인자 추가 및 반환 타입 수정
        log.debug("UserLoginResponse from() 메서드 호출, Entity와 Token -> DTO 변환 시작");
        
        return UserLoginResponse.builder()
                .accessToken(accessToken) // ✅ 핵심: accessToken 주입
                .refreshToken(refreshToken) // ✅ 핵심: refreshToken 주입
                .id(user.getId())
                .userId(user.getUserId())
                .username(user.getUsername()) // 사용자 이름 포함
                .build();
    }
}
