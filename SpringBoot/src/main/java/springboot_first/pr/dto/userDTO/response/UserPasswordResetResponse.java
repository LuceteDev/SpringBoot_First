package springboot_first.pr.dto.userDTO.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

// 1️⃣ 어노테이션 선언
@AllArgsConstructor // 모든 필드를 매개변수로 갖는 생성자 자동 생성
@NoArgsConstructor // 매개변수가 아예 없는 기본 생성자 자동 생성
@Getter // 각 필드 값을 조회할 수 있는 Getter 메서드 자동 생성
@ToString // 모든 필드를 출력할 수 있는 toString 메서드 자동 생성, ✅ 로깅과 디버깅을 위해 추가
@Builder // DTO 생성을 위한 빌더 패턴 추가 (테스트 코드 작성에 용이하다고 한다 ✅)
@Slf4j // 로깅 사용 -> 이 로깅 메시지에 객체의 상태를 담기 위해 @ToString을 함께 사용


public class UserPasswordResetResponse {
  // ✅ 비밀번호 재설정 응답

    // ✅ 필수: 작업 성공 여부
    private boolean success;
    
    // 재설정된 사용자의 ID도 출력해보기
    private String userId;

    // ✅ 필수: 사용자에게 보여줄 결과 메시지
    private String message;


    // 💡 선택: 응답 발생 시점 (로깅 및 추적에 유용)
    private LocalDateTime timestamp;

    // 정적 팩토리 메서드: 성공 응답을 쉽게 생성하기 위한 패턴

    public static UserPasswordResetResponse success(String userId) {
      log.info("비밀번호 재설정 성공 응답 생성: success=true, userId={}", userId);
        return UserPasswordResetResponse.builder()
                .success(true)
                .userId(userId)
                .message("비밀번호가 성공적으로 재설정되었습니다.")
                .timestamp(LocalDateTime.now())
                .build();
    }
}

