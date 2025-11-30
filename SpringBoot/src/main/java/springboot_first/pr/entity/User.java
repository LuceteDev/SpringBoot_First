package springboot_first.pr.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
@AllArgsConstructor // 모든 필드를 매개변수로 갖는 생성자 자동 생성
@NoArgsConstructor // 매개변수가 아예 없는 기본 생성자 자동 생성
@Builder // 서비스에서 엔티티 생성 시 훨씬 편함
@Slf4j // 로깅 추가
@Table(name = "users") // 👈 (중요) 실제 DB 테이블 이름인 "users"를 지정했습니다.

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
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(nullable = false, unique = true)
    
    private String email;

    // [유지] 사용자 이름 (본명)
    @NotBlank
    @Column(nullable = false)
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
    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

        
    
    
    // 〰️〰️〰️〰️〰️〰️〰️〰️ 6️⃣ UserRegisterRequest DTO 생성 하러 이동 〰️〰️〰️〰️〰️〰️〰️〰️ //


    //    Entity.from()은 외부 데이터(DTO)를 DB에 저장할 Entity 객체로 변환할 때 사용됨 ‼️


    // 〰️〰️〰️ 6️⃣ 서비스 -> 엔티티로 요청하는 작업 (미리 작성 or 서비스 from 메서드 정의 후 작성하기) 〰️〰️〰️ //
    // 〰️〰️〰️〰️〰️〰️〰️〰️ 6️⃣ User 엔티티 생성 및 반환하는 정적 팩토리 메서드 추가하기 〰️〰️〰️〰️〰️〰️〰️〰️ //
    // public static User from(UserRegisterRequest dto, String encodedPassword){ // 6️⃣-1️⃣ String encodedPassword는 서비스(Service) 계층에서 비밀번호를 암호화한 후, 그 결과 값(암호화된 문자열)을 엔티티의 정적 팩토리 메서드(from)로 전달했다는 것을 의미
    //     // ⚠️ 메서드명 from은 (가장 흔하게 사용되며, "DTO로부터 엔티티를 만든다"는 의미를 명확히 함) 라고 한다
    //     // ✅ 최종적으로 User (엔티티) 객체를 만들어서 반환하기 때문. (DTO → Entity 변환)
        
    //     log.info("User Entity from() 메서드 호출"); // 💡 [로깅] Entity 생성 시작 👇아래 같은 형태
    //     // UserRegisterRequest DTO 내부 상태: UserRegisterRequest(userId=jk, email=jk@jk.com, username=jk, password=jk, phoneNumber=010-1231-1231)

    //     return User.builder() // ⚠️ Service 계층의 핵심 로직을 담당하는 곳에서 가독성과 안전성을 높이기 위해
    //     .userId(dto.getUserId())
    //     .email(dto.getEmail())
    //     .username(dto.getUsername())
    //     .password(encodedPassword) // 반드시 암호화된 비밀번호 사용
    //     .phoneNumber(dto.getPhoneNumber())
    //     .build();
    // } 

    // 〰️〰️〰️〰️〰️〰️〰️〰️ ⚠️ 위 반환은 아이디 필드를 입력 받는 경우 〰️〰️〰️〰️〰️〰️〰️〰️ //
    
    // 〰️〰️〰️ DTO -> Entity 변환 팩토리 메서드 수정 〰️〰️〰️ 
    public static User from(UserRegisterRequest dto, String encodedPassword){ 
        
        // 💡 [핵심 로직] 이메일에서 @ 앞부분을 추출하여 userId로 설정
        String userIdFromEmail = dto.getEmail().split("@")[0];

        log.info("User Entity from() 메서드 호출, userId를 이메일 접두사({})로 설정", userIdFromEmail); 

        return User.builder() 
        .userId(userIdFromEmail) // 💡 [변경] DTO의 userId 대신 이메일 접두사 사용
        .email(dto.getEmail())
        .username(dto.getUsername())
        .password(encodedPassword) 
        .phoneNumber(dto.getPhoneNumber())
        .build();
    }
}