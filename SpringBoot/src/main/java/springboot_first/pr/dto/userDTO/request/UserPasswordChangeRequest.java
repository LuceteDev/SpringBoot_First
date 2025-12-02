package springboot_first.pr.dto.userDTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

// 1️⃣ 어노테이션 선언
@AllArgsConstructor // 모든 필드를 매개변수로 갖는 생성자 자동 생성
@NoArgsConstructor // 매개변수가 아예 없는 기본 생성자 자동 생성
@Getter // 각 필드 값을 조회할 수 있는 Getter 메서드 자동 생성
@ToString // 모든 필드를 출력할 수 있는 toString 메서드 자동 생성, ✅ 로깅과 디버깅을 위해 추가
@Builder // DTO 생성을 위한 빌더 패턴 추가 (테스트 코드 작성에 용이하다고 한다 ✅)

public class UserPasswordChangeRequest {

// ⚠️ DTO의 역할 (사용자 친화적인 에러 메시지 제공)
// ⚠️ 예를 들어 userId의 경우 API 요청 단계에서 입력 데이터가 "비어 있지 않고, 4~20자이며, 이메일 형식이 맞는지"를 검증 

    // ✅ 비밀번호 재설정시 요청받는 필드 (Bean Validation 추가)

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String oldPassword;

    @NotBlank(message = "새로운 비밀번호를 입력해주세요.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,20}",
            message = "비밀번호는 8~20자의 영문 대소문자, 숫자, 특수 문자(!@#$%^&+=)를 모두 포함해야 합니다.")
    private String newPassword;


}
