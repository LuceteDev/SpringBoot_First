package springboot_first.pr.dto.userDTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

public class UserIdFindRequest {

    // 1️⃣ @NotBlank: 휴대폰 번호는 필수 입력값 (공백/빈 문자열 불가)
    @NotBlank(message = "휴대폰 번호는 필수 입력 값입니다.")

    // 2️⃣ @Pattern: 대한민국 일반 휴대폰 번호 형식 (010-XXXX-XXXX)만 허용
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", 
             message = "유효한 휴대폰 번호 형식(010-xxxx-xxxx)이 아닙니다.")
    private String phoneNumber;

    // 2️⃣ 본명 (사용자 이름)
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @Size(max = 50, message = "사용자 이름은 50자를 초과할 수 없습니다.")
    private String username; // 본명
}
