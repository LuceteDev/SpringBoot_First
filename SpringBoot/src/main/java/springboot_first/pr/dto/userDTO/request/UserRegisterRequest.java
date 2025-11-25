package springboot_first.pr.dto.userDTO.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

// 1️⃣ 어노테이션 선언
@AllArgsConstructor // 모든 필드를 매개변수로 갖는 생성자 자동 생성
@NoArgsConstructor // 매개변수가 아예 없는 기본 생성자 자동 생성
@Getter // 각 필드 값을 조회할 수 있는 Getter 메서드 자동 생성
@ToString // 모든 필드를 출력할 수 있는 toString 메서드 자동 생성

public class UserRegisterRequest {
    
    // ✅ 회원가입 시 요청받는 필드
    private String userId; // 아이디
    private String email; // 이메일
    private String username; // 본명
    private String password; // 비밀번호
    private String phoneNumber; // 휴대폰 번호
}
