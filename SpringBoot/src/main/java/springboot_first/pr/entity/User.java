package springboot_first.pr.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import springboot_first.pr.dto.userDTO.request.UserRegisterRequest;

// 1️⃣ 어노테이션 선언
@Entity // 해당 클래스가 엔티티임을 선언, 클래스 필드를 바탕으로 DB에 테이블 생성
@Getter // 각 필드 값을 조회할 수 있는 Getter 메서드 자동 생성
@ToString(exclude = "password") // pw 필드를 제외하고 모든 필드를 출력할 수 있는 toString 메서드 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 갖는 생성자 자동 생성
@NoArgsConstructor // 매개변수가 아예 없는 기본 생성자 자동 생성

@Builder // 서비스에서 엔티티 생성 시 훨씬 편함

public class User {
    // 2️⃣ 필드 선언
    // 3️⃣ id필드에 @Id 붙여서 대표키 선언하기
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 4️⃣ @GeneratedValue 붙여서 autoincrement 역할하도록 선언하기, (데이터를 생성할 때마다 +1 되도록 설정)
    private Long id; // 대표키

    // 5️⃣ 해당 필드를 테이블의 속성으로 매핑
    // 실제 로그인 ID + 닉네임 용도
    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String username;

    // @Size(min = 8)
    @Column(nullable = false)
    @JsonIgnore // ⚠️ JSON 응답에 포함 안 되게
    private String password;
    //⚠️ JSON 응답에 포함 안 되게 하는 것! 또는 DTO(UserResponseDTO)를 만들어서 password 필드 자체를 빼버리기.

    @Column(unique = true)
    private String phoneNumber;

    // 〰️〰️〰️〰️〰️〰️〰️〰️ 6️⃣ UserRegisterRequest DTO 생성 하러 이동 〰️〰️〰️〰️〰️〰️〰️〰️ //
    

    // 〰️〰️〰️ 6️⃣ 서비스 -> 엔티티로 요청하는 작업 (미리 작성 or 서비스 from 메서드 정의 후 작성하기) 〰️〰️〰️ //
    // 〰️〰️〰️〰️〰️〰️〰️〰️ 6️⃣ User 엔티티 생성 및 반환하는 정적 팩토리 메서드 추가하기 〰️〰️〰️〰️〰️〰️〰️〰️ //
    public static User from(UserRegisterRequest dto, String encodedPassword){ // 6️⃣-1️⃣ String encodedPassword는 서비스(Service) 계층에서 비밀번호를 암호화한 후, 그 결과 값(암호화된 문자열)을 엔티티의 정적 팩토리 메서드(from)로 전달했다는 것을 의미
        // ⚠️ 메서드명 from은 (가장 흔하게 사용되며, "DTO로부터 엔티티를 만든다"는 의미를 명확히 함) 라고 한다
        // ✅ 최종적으로 User (엔티티) 객체를 만들어서 반환하기 때문. (DTO → Entity 변환)

        return User.builder() // ⚠️ Service 계층의 핵심 로직을 담당하는 곳에서 가독성과 안전성을 높이기 위해
        .userId(dto.getUserId())
        .email(dto.getEmail())
        .username(dto.getUsername())
        .password(encodedPassword) // 반드시 암호화된 비밀번호 사용
        .phoneNumber(dto.getPhoneNumber())
        .build();
    } 
    
}

