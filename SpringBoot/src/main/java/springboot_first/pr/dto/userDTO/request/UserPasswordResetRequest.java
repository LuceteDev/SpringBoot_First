package springboot_first.pr.dto.userDTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.AllArgsConstructor;

// 1️⃣ 어노테이션 선언
@AllArgsConstructor // 모든 필드를 매개변수로 갖는 생성자 자동 생성
@NoArgsConstructor // 매개변수가 아예 없는 기본 생성자 자동 생성
@Getter // 각 필드 값을 조회할 수 있는 Getter 메서드 자동 생성
@ToString // 모든 필드를 출력할 수 있는 toString 메서드 자동 생성, ✅ 로깅과 디버깅을 위해 추가
@Builder // DTO 생성을 위한 빌더 패턴 추가 (테스트 코드 작성에 용이하다고 한다 ✅)

public class UserPasswordResetRequest {

    @NotBlank(message = "사용자 ID는 필수 입력값입니다.")
    @Size(max = 20, message = "사용자 ID는 20자를 초과할 수 없습니다.")
    private String userId; // 사용자 식별을 위한 ID

    
    @NotBlank(message = "휴대폰 번호는 필수 입력 값입니다.")
    // 대한민국 일반 휴대폰 번호 형식 (010-XXXX-XXXX)만 허용
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$",
             message = "유효한 휴대폰 번호 형식(010-xxxx-xxxx)이 아닙니다.")
    private String phoneNumber; // 휴대폰 번호

    
    @NotBlank(message = "새 비밀번호는 필수 입력값입니다.")
    // 💡 보안 강화를 위한 비밀번호 패턴 (대소문자, 숫자, 특수문자 포함, 8~20자)
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,20}", 
          message = "비밀번호는 8~20자의 영문 대소문자, 숫자, 특수 문자(!@#$%^&+=)를 모두 포함해야 합니다.")
    private String newPassword; // 새로 설정할 비밀번호

}