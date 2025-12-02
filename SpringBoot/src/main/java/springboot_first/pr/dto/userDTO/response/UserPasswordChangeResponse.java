package springboot_first.pr.dto.userDTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

// 1️⃣ 어노테이션 선언
@AllArgsConstructor // 모든 필드를 매개변수로 갖는 생성자 자동 생성
@NoArgsConstructor // 매개변수가 아예 없는 기본 생성자 자동 생성
@Getter // 각 필드 값을 조회할 수 있는 Getter 메서드 자동 생성
@ToString // 모든 필드를 출력할 수 있는 toString 메서드 자동 생성
@Builder // DTO 생성을 위한 빌더 패턴 추가
@Slf4j // 로깅 사용

public class UserPasswordChangeResponse {

    // ✅ 필수: 작업 성공 여부
    private boolean success;

    // ✅ 필수: 사용자에게 보여줄 결과 메시지
    private String message;

    // 💡 선택: 응답 발생 시점 (로깅 및 추적에 유용)
    private LocalDateTime timestamp;


    // 정적 헬퍼 메서드 : 성공 응답 객체를 생성

    public static UserPasswordChangeResponse success() {
        return UserPasswordChangeResponse.builder()
                .success(true)
                .message("비밀번호가 성공적으로 변경되었습니다.")
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 정적 헬퍼 메서드: 실패 응답 객체를 생성
    public static UserPasswordChangeResponse fail(String errorMessage) {
        return UserPasswordChangeResponse.builder()
                .success(false)
                .message(errorMessage)
                .timestamp(LocalDateTime.now())
                .build();
    }
}