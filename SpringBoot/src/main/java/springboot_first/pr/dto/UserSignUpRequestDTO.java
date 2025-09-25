package springboot_first.pr.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor; // 디폴트 생성자를 위해 추가 (선택적이지만 안전)

@Getter
@NoArgsConstructor // JSON 바인딩을 위해 디폴트 생성자를 추가하는 것이 좋습니다.
// @Builder // Lombok의 Builder 패턴을 사용하여 쉽게 객체 생성
public class UserSignUpRequestDTO {
    // 회원가입 시 요청받는 필드=
    private String userId;
    private String email;
    private String username;
    private String address;
    private String phoneNumber;
    private String password;
}