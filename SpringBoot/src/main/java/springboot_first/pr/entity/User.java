package springboot_first.pr.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import springboot_first.pr.dto.userDTO.request.UserRegisterRequest;

// 1️⃣ 어노테이션 선언
@Entity // 해당 클래스가 엔티티임을 선언, 클래스 필드를 바탕으로 DB에 테이블 생성
@Getter // 각 필드 값을 조회할 수 있는 Getter 메서드 자동 생성
@ToString(exclude = "password") // pw 필드를 제외하고 모든 필드를 출력할 수 있는 toString 메서드 자동 생성
@Builder // 서비스에서 엔티티 생성 시 훨씬 편함
@Slf4j // 로깅 추가
@Table(name = "users") // 👈 (중요) 실제 DB 테이블 이름인 "users"를 지정했습니다.
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 모든 필드를 매개변수로 갖는 생성자 자동 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 매개변수가 아예 없는 기본 생성자 자동 생성
// ✔ JPA 규칙 준수, 엔티티 생성 ∙ 수정 규칙 강제, 나중에 유지보수할 때 버그 확률 급감

public class User {

// ⚠️ Entity (`User`)의 역할 (최종 방어선 역할)
// ⚠️ DTO 검증을 통과한 데이터가 DB에 저장될 때, DB가 "이 필드는 무조건 값이 있어야 하고, 중복되면 안 된다"는 것을 강제

    // 2️⃣ 필드 선언
    // 3️⃣ id필드에 @Id 붙여서 대표키 선언하기
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 4️⃣ @GeneratedValue 붙여서 autoincrement 역할하도록 선언하기, (데이터를 생성할 때마다 +1 되도록 설정)
    private Long id; // 대표키

    // 5️⃣ 해당 필드를 테이블의 속성으로 매핑
    // 실제 로그인 ID
    @Column(name = "user_id", nullable = false, unique = true, length = 50)
    private String userId;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    // [유지] 사용자 이름 (본명)
    @NotBlank
    @Column(nullable = false, length = 100)
    private String username;

    // @Size(min = 8)
    @Column(nullable = false)
    @JsonIgnore // ⚠️ JSON 응답에 포함 안 되게
    private String password;
    //⚠️ JSON 응답에 포함 안 되게 하는 것! 또는 DTO(UserResponseDTO)를 만들어서 password 필드 자체를 빼버리기.

    // @NotBlank(message = "휴대폰 번호는 필수 입력 값입니다.")
    // 대한민국 일반 휴대폰 번호 형식 (010-XXXX-XXXX)만 허용
    // @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "유효한 휴대폰 번호 형식(010-xxxx-xxxx)이 아닙니다.")

    // ⚠️ 입력값 형식을 엔티티에 강제하면 유연성이 떨어지고, 이러한 패턴, 검증 옵션은 DTO에서 처리해야함 ‼️
    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;


    // 💡 [추가] 권한 필드 (AuthService 로직 준수)
    @Column(nullable = false, length = 10)
    private String role;
    
    // 〰️〰️〰️〰️〰️〰️〰️〰️ 6️⃣ UserRegisterRequest DTO 생성 하러 이동 〰️〰️〰️〰️〰️〰️〰️〰️ //

    //    Entity.from()은 외부 데이터(DTO)를 DB에 저장할 Entity 객체로 변환할 때 사용됨 ‼️

	// 💡 정적 팩토리 메서드: DTO, 암호화된 비밀번호, 그리고 (서비스에서 구성된) 완전한 이메일 주소를 인수로 받음
	public static User from(UserRegisterRequest requestDto, String encodedPassword, String fullEmail, String role) {
		
		log.info("User Entity from() 메서드 호출, userId: {}, 전달받은 email: {}", requestDto.getUserId(), fullEmail); 

		return User.builder() 
				.userId(requestDto.getUserId()) 
				.email(fullEmail) // 💡 서비스에서 완성된 전체 이메일 주소를 사용
				.username(requestDto.getUsername())
				.password(encodedPassword) 
				.phoneNumber(requestDto.getPhoneNumber())
                .role(role != null ? role : "USER") // 기본값 설정
				.build();
	}

	// 💡 비밀번호 재설정을 위한 setter 대용 메서드
	public void setPassword(String newEncodedPassword) {
		this.password = newEncodedPassword;
	}

    // 💡 비밀번호 재설정을 위한 전용 메서드 (Setter 사용을 지양하고 의도를 명확히 함)
    public void updatePassword(String encodeNewPassword) {
        this.password = encodeNewPassword; // 👈 비밀번호 필드 업데이트 구현
        log.debug("User 엔티티 비밀번호 필드 업데이트 완료");
    }
}