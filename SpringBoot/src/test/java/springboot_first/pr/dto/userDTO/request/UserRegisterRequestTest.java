package springboot_first.pr.dto.userDTO.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UserRegisterRequest DTO 유효성 검사 최종 테스트")
class UserRegisterRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // // 기본적으로 유효한 DTO 빌더를 반환하는 헬퍼 메서드
    // private UserRegisterRequest.UserRegisterRequestBuilder createValidRequestBuilder() {
    //     return UserRegisterRequest.builder()
    //             .userId("valid_user") // 4자 이상 20자 이하
    //             .email("test@example.com")
    //             .username("홍길동")
    //             .password("StrongPass1234!") // 8자 이상
    //             .phoneNumber("010-1234-5678");
    // }

    // // --- 1. ID 공백 및 최소 길이 실패 테스트 (Expected Count: 2로 수정) ---
    // @Test
    // @DisplayName("DTO_유효성_실패: ID 공백 시 @NotBlank와 @Size(min=4) 두 가지 위반이 발생해야 한다.")
    // void validation_fail_when_userId_is_blank() {
    //     // given
    //     // userId를 빈 문자열로 설정하면 @NotBlank와 @Size(min=4) 두 가지 모두 위반됨
    //     UserRegisterRequest request = createValidRequestBuilder().userId("").build();

    //     // when
    //     Set<ConstraintViolation<UserRegisterRequest>> violations = validator.validate(request);

    //     // then
    //     // 🚨 수정된 기대값: @NotBlank와 @Size(min=4)가 동시에 작동하는 것이 정상입니다. 2개를 기대합니다.
    //     assertThat(violations.size()).as("ID 공백 위반 테스트: 총 위반 개수가 2개여야 합니다.").isEqualTo(2); 

    //     // 메시지 검증
    //     assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("필수 입력 값입니다.")), "@NotBlank 메시지 포함 확인");
    //     assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("4자 이상 20자 이하로 입력해야 합니다.")), "@Size(min) 메시지 포함 확인");
    // }
    
    // // --- 2. ID 최대 길이 초과 실패 테스트 (Expected Count: 1 유지) ---
    // @Test
    // @DisplayName("DTO_유효성_실패: 사용자 ID가 최대 길이(20자)를 초과하면 위반이 발생해야 한다")
    // void validation_fail_userId_too_long() {
    //     // given
    //     String tooLongId = "a".repeat(21); 
    //     UserRegisterRequest request = createValidRequestBuilder().userId(tooLongId).build();

    //     // when
    //     Set<ConstraintViolation<UserRegisterRequest>> violations = validator.validate(request);

    //     // then
    //     assertThat(violations.size()).as("사용자 ID 길이 위반이 1개 발생해야 합니다.").isEqualTo(1);
        
    //     ConstraintViolation<UserRegisterRequest> violation = violations.iterator().next();
    //     assertThat(violation.getMessage()).contains("4자 이상 20자 이하로 입력해야 합니다.");
    // }

    // // --- 3. 이메일 형식 실패 테스트 (Expected Count: 1 유지) ---
    // @ParameterizedTest(name = "이메일 형식 실패: {0}")
    // @ValueSource(strings = {"invalid-email", "test@", "@domain.com"}) // ⚠️ a@a 이러한 유형은 통과 되버림
    // @DisplayName("DTO_유효성_실패: 이메일 형식이 잘못되면 위반이 발생해야 한다")
    // void validation_fail_invalid_email_format(String invalidEmail) {
    //     // given
    //     UserRegisterRequest request = createValidRequestBuilder()
    //             .email(invalidEmail) 
    //             .build();

    //     // when
    //     Set<ConstraintViolation<UserRegisterRequest>> violations = validator.validate(request);

    //     // then
    //     assertEquals(1, violations.size(), "이메일 형식 위반이 1개 발생해야 합니다.");
    //     assertEquals("email", violations.iterator().next().getPropertyPath().toString(), "email 필드에서 위반이 발생해야 합니다.");
    //     assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("올바른 이메일 형식이 아닙니다.")), "이메일 형식 위반 메시지를 포함해야 합니다.");
    // }

        // 기본적으로 유효한 DTO 빌더를 반환하는 헬퍼 메서드
    private UserRegisterRequest.UserRegisterRequestBuilder createValidRequestBuilder() {
        // 💡 [변경] userId 필드 제거
        return UserRegisterRequest.builder()
                .email("test.id.123@email.com") // 💡 [변경] 유효성 검사 통과 이메일 (영문, 숫자, 점(.)만 포함, @email.com 도메인)
                .username("홍길동")
                .password("StrongPass1234!") // 8자 이상
                .phoneNumber("010-1234-5678");
    }

    // 💡 [제거됨] 기존의 ID 공백 및 최소/최대 길이 실패 테스트는 DTO에서 userId가 사라져서 함께 제거되었습니다.


    // --- 1. 이메일 형식 실패 테스트 (기본 @Email + @NotBlank 테스트 유지) ---
    @ParameterizedTest(name = "이메일 형식 실패 (기본 검사): {0}")
    @ValueSource(strings = {"invalid-email", "test@", "@domain.com"}) 
    @DisplayName("DTO_유효성_실패: 기본 이메일 형식이 잘못되면 위반이 발생해야 한다")
    void validation_fail_invalid_email_format_basic(String invalidEmail) {
        // given
        UserRegisterRequest request = createValidRequestBuilder()
                .email(invalidEmail) 
                .build();

        // when
        Set<ConstraintViolation<UserRegisterRequest>> violations = validator.validate(request);

        // then
        // @Email 위반 메시지 검증
        assertThat(violations).isNotEmpty();
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("올바른 이메일 형식이 아닙니다.")), "이메일 형식 위반 메시지를 포함해야 합니다.");
    }
    
    // --- 2. 이메일 패턴 실패 테스트 (새로 추가된 @Pattern 검증) ---
    @ParameterizedTest(name = "이메일 패턴 실패 (구글식 검사): {0}")
    @ValueSource(strings = {
            "test@naver.com",        // 💡 [변경] 도메인 불일치 (@email.com 아님)
            "test_hyphen@email.com", // 💡 [변경] 특수 문자 포함 (언더바 '_' 포함)
            "한글id@email.com"       // 💡 [변경] 한글 포함
            // "test..test@email.com" // 우리 패턴은 허용함
    })
    @DisplayName("DTO_유효성_실패: 이메일이 구글식 패턴(영문,숫자,점, @email.com)을 위반하면 발생해야 한다.")
    void validation_fail_invalid_email_pattern_google_style(String invalidEmail) {
        // given
        UserRegisterRequest request = createValidRequestBuilder()
                .email(invalidEmail) 
                .build();

        // when
        Set<ConstraintViolation<UserRegisterRequest>> violations = validator.validate(request);

        // then
        // @Pattern 위반 메시지 검증
        assertThat(violations).isNotEmpty();
        assertTrue(violations.stream().anyMatch(v -> 
                v.getMessage().contains("'@email.com'이어야 합니다.")), 
                "@Pattern 위반 메시지를 포함해야 합니다.");
    }

    // --- 3. 이메일 패턴 성공 테스트 (새로 추가된 @Pattern 검증) ---
    @ParameterizedTest(name = "이메일 패턴 성공 (구글식 검사): {0}")
    @ValueSource(strings = {
            "abc1234@email.com",       // 영문+숫자
            "dot.test.id@email.com",   // 점(.) 포함
            "only.dots@email.com",     // 점(.)과 영문만
            "12345@email.com"          // 숫자만
    })
    @DisplayName("DTO_유효성_성공: 이메일이 구글식 패턴을 모두 만족하면 위반이 없어야 한다.")
    void validation_success_email_pattern_google_style(String validEmail) {
        // given
        UserRegisterRequest request = createValidRequestBuilder()
                .email(validEmail) 
                .build();

        // when
        Set<ConstraintViolation<UserRegisterRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }
    // --- 기타 테스트 케이스 ---

    @Test
    @DisplayName("DTO_유효성_성공: 모든 필드가 유효하면 위반이 없어야 한다.")
    void validation_success() {
        UserRegisterRequest request = createValidRequestBuilder().build();
        Set<ConstraintViolation<UserRegisterRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }
    
    @Test
    @DisplayName("DTO_유효성_실패: 비밀번호 길이가 8자 미만이면 위반이 발생해야 한다.")
    void validation_fail_password_size() {
        UserRegisterRequest request = createValidRequestBuilder().password("short").build();
        Set<ConstraintViolation<UserRegisterRequest>> violations = validator.validate(request);
        
        assertThat(violations.size()).isEqualTo(1);
        ConstraintViolation<UserRegisterRequest> violation = violations.iterator().next();
        assertThat(violation.getMessage()).contains("8자 이상 30자 이하");
    }
    
    @Test
    @DisplayName("DTO_유효성_실패: 휴대폰 번호 형식이 유효하지 않으면 위반이 발생해야 한다.")
    void validation_fail_invalid_phone_number_format() {
        UserRegisterRequest request = createValidRequestBuilder().phoneNumber("01012345678").build();
        Set<ConstraintViolation<UserRegisterRequest>> violations = validator.validate(request);
        
        assertThat(violations.size()).isEqualTo(1);
        ConstraintViolation<UserRegisterRequest> violation = violations.iterator().next();
        assertThat(violation.getMessage()).contains("유효한 휴대폰 번호 형식(010-xxxx-xxxx)이 아닙니다.");
    }
}