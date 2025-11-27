// package springboot_first.pr.dto.userDTO.request;

// import com.fasterxml.jackson.annotation.JsonProperty;

// import jakarta.validation.constraints.NotBlank;
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

// public class UserLoginRequest {
    
//   // ✅ 로그인 시 요청받는 필드
//   // private String userId; // 아이디
//   // private String email; // 이메일
//   // private String password; // 비밀번호
//   // private String phoneNumber; // 휴대폰 번호
//     @JsonProperty("emailOrIdOrPhone")
//     @NotBlank(message = "아이디, 이메일 또는 휴대폰 번호는 필수 입력값입니다.")
//     private String emailOrIdOrPhone; 
    
//     // 💡 "password" 키도 명시적으로 연결
//     @JsonProperty("password")
//     @NotBlank(message = "비밀번호는 필수 입력값입니다.")
//     private String password;
// }


package springboot_first.pr.dto.userDTO.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size; // 💡 Size 어노테이션 추가
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

// 1️⃣ 어노테이션 선언
@AllArgsConstructor 
@NoArgsConstructor 
@Getter 
@ToString 
@Builder 
public class UserLoginRequest {
    
    // 💡 [수정] ID, Email, Phone 번호 통합 필드에 최소/최대 길이 검증 추가
    // 로그인 ID는 보통 4자 이상이므로 최소 길이 검증 추가
    @JsonProperty("emailOrIdOrPhone")
    @NotBlank(message = "아이디, 이메일 또는 휴대폰 번호는 필수 입력값입니다.")
    @Size(min = 4, max = 100, message = "아이디/이메일은 최소 4자 이상 최대 100자 이하로 입력해야 합니다.")
    private String emailOrIdOrPhone; 
    
    // 💡 [수정] 비밀번호 필드에 최소/최대 길이 검증 추가
    // 회원가입 DTO와 일관성 유지 (8자 이상 30자 이하)
    @JsonProperty("password")
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, max = 30, message = "비밀번호는 8자 이상 30자 이하로 입력해야 합니다.")
    private String password;
}