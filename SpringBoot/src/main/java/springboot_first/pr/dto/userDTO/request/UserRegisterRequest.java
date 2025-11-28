// package springboot_first.pr.dto.userDTO.request;

// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.ToString;

// // 1️⃣ 어노테이션 선언
// @AllArgsConstructor // 모든 필드를 매개변수로 갖는 생성자 자동 생성
// @NoArgsConstructor // 매개변수가 아예 없는 기본 생성자 자동 생성
// @Getter // 각 필드 값을 조회할 수 있는 Getter 메서드 자동 생성
// @ToString // 모든 필드를 출력할 수 있는 toString 메서드 자동 생성, ✅ 로깅과 디버깅을 위해 추가
// @Builder // DTO 생성을 위한 빌더 패턴 추가 (테스트 코드 작성에 용이하다고 한다 ✅)

// public class UserRegisterRequest {
    
//     // ✅ 회원가입 시 요청받는 필드
//     private String userId; // 아이디
//     private String email; // 이메일
//     private String username; // 본명
//     private String password; // 비밀번호
//     private String phoneNumber; // 휴대폰 번호
// }


package springboot_first.pr.dto.userDTO.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

// 1️⃣ 어노테이션 선언
@AllArgsConstructor // 모든 필드를 매개변수로 갖는 생성자 자동 생성
@NoArgsConstructor // 매개변수가 아예 없는 기본 생성자 자동 생성
@Getter // 각 필드 값을 조회할 수 있는 Getter 메서드 자동 생성
@ToString // 모든 필드를 출력할 수 있는 toString 메서드 자동 생성
@Builder // DTO 생성을 위한 빌더 패턴 추가

@With 
public class UserRegisterRequest {
// ⚠️ DTO (`UserRegisterRequest`)의 역할 (사용자 친화적인 에러 메시지 제공)
// ⚠️ 예를 들어 userId의 경우 API 요청 단계에서 입력 데이터가 "비어 있지 않고, 4~20자이며, 이메일 형식이 맞는지"를 검증 

    // ✅ 회원가입 시 요청받는 필드 (Bean Validation 추가)

    // [1] 사용자 ID (로그인 아이디)
    // @NotBlank(message = "사용자 ID는 필수 입력 값입니다.") // null, "", " "을 허용하지 않음
    // @Size(min = 4, max = 20, message = "사용자 ID는 4자 이상 20자 이하로 입력해야 합니다.")
    // private String userId; // 아이디

    // [2] 이메일
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.") // @와 .을 포함하는 유효한 이메일 형식인지 검사
    @Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다.")
    // 💡 [핵심 변경] 구글식 도메인 및 로컬파트 패턴 강제 (a-z, 0-9, 점(.)만 허용)
    // ^[a-zA-Z0-9.]+@email\\.com$
    // - @ 앞부분은 영문, 숫자, 점(.)만 허용
    // - 반드시 @email.com으로 끝나야 함
    @Pattern(regexp = "^[a-zA-Z0-9.]+@email\\.com$",
             message = "이메일은 영문, 숫자, 마침표(.)로 구성되어야 하며 도메인은 '@email.com'이어야 합니다.")
    private String email; // 이메일

    // [3] 본명 (사용자 이름)
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @Size(max = 50, message = "사용자 이름은 50자를 초과할 수 없습니다.")
    private String username; // 본명

    // [4] 비밀번호
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, max = 30, message = "비밀번호는 8자 이상 30자 이하로 입력해야 합니다.")
    // TODO: 현업에서는 아래와 같이 복잡성 패턴도 추가할 수 있습니다.
    // @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
    //          message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.")
    private String password; // 비밀번호

    // [5] 휴대폰 번호
    @NotBlank(message = "휴대폰 번호는 필수 입력 값입니다.")
    // 대한민국 일반 휴대폰 번호 형식 (010-XXXX-XXXX)만 허용
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$",
             message = "유효한 휴대폰 번호 형식(010-xxxx-xxxx)이 아닙니다.")
    private String phoneNumber; // 휴대폰 번호
}

