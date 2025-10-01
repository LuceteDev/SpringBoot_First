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