// package springboot_first.pr.dto;

// import lombok.Getter;
// import lombok.Setter;
// import lombok.NoArgsConstructor;
// import com.fasterxml.jackson.annotation.JsonProperty;

// @Getter
// @Setter
// @NoArgsConstructor // 💡 기본 생성자 추가
// public class UserLoginRequestDTO {
//     // 로그인 시 요청받는 필드
//     @JsonProperty("emailOrIdOrPhone") 
//     private String emailOrIdOrPhone; // 이메일, ID, 전화번호 중 하나를 받을 필드
//     private String password;
// }
// UserLoginRequestDTO.java
package springboot_first.pr.dto;

import com.fasterxml.jackson.annotation.JsonProperty; // 💡 이 import 중요
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class UserLoginRequestDTO {
    
    // 💡 클라이언트 JSON 키 이름("emailOrIdOrPhone")과 Java 필드명을 명시적으로 연결
    @JsonProperty("emailOrIdOrPhone") 
    private String emailOrIdOrPhone; 
    
    // 💡 "password" 키도 명시적으로 연결
    @JsonProperty("password")
    private String password;
}