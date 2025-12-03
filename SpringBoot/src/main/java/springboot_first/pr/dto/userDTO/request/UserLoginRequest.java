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