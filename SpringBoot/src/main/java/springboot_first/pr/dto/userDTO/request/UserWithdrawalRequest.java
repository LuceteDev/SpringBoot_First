package springboot_first.pr.dto.userDTO.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

// 1️⃣ 어노테이션 선언
@AllArgsConstructor // 모든 필드를 매개변수로 갖는 생성자 자동 생성
@NoArgsConstructor // 매개변수가 아예 없는 기본 생성자 자동 생성
@Getter // 각 필드 값을 조회할 수 있는 Getter 메서드 자동 생성
@ToString // 모든 필드를 출력할 수 있는 toString 메서드 자동 생성
@Builder // DTO 생성을 위한 빌더 패턴 추가
public class UserWithdrawalRequest {

    // 💡 보안 강화를 위해 현재 비밀번호 확인을 포함합니다.
    @NotBlank(message = "비밀번호는 필수로 입력해야 합니다.")
    private String currentPassword;
}