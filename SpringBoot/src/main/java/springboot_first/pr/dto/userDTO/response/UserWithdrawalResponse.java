package springboot_first.pr.dto.userDTO.response;

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
public class UserWithdrawalResponse {

    private boolean success;

    private String userId;

    public static UserWithdrawalResponse success(String userId) {
      log.debug("UserWithdrawalResponse 메서드 호출, Entity -> DTO 변환 시작"); // 💡 [로깅] 응답 DTO 변환 시작
        return UserWithdrawalResponse.builder()
                .success(true)
                .userId(userId)
                .build();
    }
}