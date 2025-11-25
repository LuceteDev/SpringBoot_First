package springboot_first.pr.dto.userDTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import springboot_first.pr.entity.User;

@AllArgsConstructor // 모든 필드를 매개변수로 갖는 생성자 자동 생성
@NoArgsConstructor // 매개변수가 아예 없는 기본 생성자 자동 생성
@Getter // 각 필드 값을 조회할 수 있는 Getter 메서드 자동 생성
@Builder // 서비스에서 엔티티 생성 시 훨씬 편함
// @ToString 응답에는 얘가 필요없나?

public class UserRegisterResponse { 
  // ✅ 회원가입 응답 ⚠️ 비밀번호 제외, 조회한 id 포함

    private Long id; // 자동증가하는 기본키
    private String userId; // 아이디
    private String email; // 이메일
    private String username; // 본명
    private String phoneNumber; // 휴대폰 번호


    // 서비스에서 return UserRegisterResponse.from(savedUser); 호출 했기 때문에 메서드 정의해주기
    public static UserRegisterResponse from(User savedUser) {
      return UserRegisterResponse.builder()
      .id(savedUser.getId())
      .userId(savedUser.getUserId())
      .email(savedUser.getEmail())
      .username(savedUser.getUsername())
      .phoneNumber(savedUser.getPhoneNumber())
      .build();
    }
}
