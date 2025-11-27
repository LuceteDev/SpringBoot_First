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

@DisplayName("UserLoginRequest DTO 유효성 검사 최종 테스트")
class UserLoginRequestTest {

    private static Validator validator;

    // 테스트 클래스 실행 전 Validator 초기화
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // 기본적으로 유효한 DTO 빌더를 반환하는 헬퍼 메서드
    private UserLoginRequest.UserLoginRequestBuilder createValidRequestBuilder() {
        return UserLoginRequest.builder()
                .emailOrIdOrPhone("valid_user") // 최소 4자 이상
                .password("StrongPass1234!"); // 최소 8자 이상
    }

    // --- 1. 유효성 성공 테스트 ---
    @Test
    @DisplayName("DTO_유효성_성공: 모든 필드가 유효하면 위반이 없어야 한다.")
    void validation_success() {
        // given
        UserLoginRequest request = createValidRequestBuilder().build();

        // when
        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    // --- 2. ID/Email/Phone 필드 검증 실패 테스트 ---

    @ParameterizedTest(name = "로그인 ID/이메일/전화번호 공백 실패: 입력값 '{0}'")
    @ValueSource(strings = {"", " ", "\t"})
    @DisplayName("DTO_유효성_실패: ID/Email/Phone 필드가 공백이면 @NotBlank와 @Size(min=4) 위반이 발생해야 한다.")
    void validation_fail_when_login_id_is_blank_or_too_short(String blankValue) {
        // given
        // 빈 문자열("") 또는 공백 문자열(" ")은 @NotBlank와 @Size(min=4)를 모두 위반합니다.
        UserLoginRequest request = createValidRequestBuilder()
                .emailOrIdOrPhone(blankValue)
                .build();

        // when
        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(request);

        // then
        // 빈 문자열/공백은 보통 @NotBlank와 @Size(min) 두 가지 위반을 발생시킵니다.
        assertThat(violations.size()).as("ID/Email/Phone 필드 위반 개수가 2개여야 합니다.").isEqualTo(2);

        // @NotBlank 메시지 검증
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("필수 입력값입니다.")), 
                   "@NotBlank 메시지 포함 확인");
        // @Size(min) 메시지 검증
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("최소 4자 이상 최대 100자 이하로 입력해야 합니다.")), 
                   "@Size(min) 메시지 포함 확인");
    }

    @Test
    @DisplayName("DTO_유효성_실패: ID/Email/Phone 필드가 최소 길이(4자) 미만이면 @Size 위반이 발생해야 한다.")
    void validation_fail_when_login_id_is_too_short() {
        // given
        UserLoginRequest request = createValidRequestBuilder()
                .emailOrIdOrPhone("abc") // 3자 (4자 미만)
                .build();

        // when
        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.size()).as("ID/Email/Phone 필드 길이 위반이 1개 발생해야 합니다.").isEqualTo(1);
        ConstraintViolation<UserLoginRequest> violation = violations.iterator().next();
        assertThat(violation.getMessage()).contains("최소 4자 이상 최대 100자 이하로 입력해야 합니다.");
    }

    @Test
    @DisplayName("DTO_유효성_실패: ID/Email/Phone 필드가 최대 길이(100자)를 초과하면 @Size 위반이 발생해야 한다.")
    void validation_fail_when_login_id_is_too_long() {
        // given
        String tooLongId = "a".repeat(101); 
        UserLoginRequest request = createValidRequestBuilder()
                .emailOrIdOrPhone(tooLongId)
                .build();

        // when
        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.size()).as("ID/Email/Phone 필드 길이 위반이 1개 발생해야 한다.").isEqualTo(1);
        ConstraintViolation<UserLoginRequest> violation = violations.iterator().next();
        assertThat(violation.getMessage()).contains("최소 4자 이상 최대 100자 이하로 입력해야 합니다.");
    }

    // --- 3. 비밀번호 필드 검증 실패 테스트 ---

    @ParameterizedTest(name = "비밀번호 공백 실패: 입력값 '{0}'")
    @ValueSource(strings = {"", " "})
    @DisplayName("DTO_유효성_실패: 비밀번호가 공백이면 @NotBlank와 @Size(min=8) 위반이 발생해야 한다.")
    void validation_fail_when_password_is_blank(String blankValue) {
        // given
        UserLoginRequest request = createValidRequestBuilder()
                .password(blankValue)
                .build();

        // when
        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.size()).as("비밀번호 필드 위반 개수가 2개여야 합니다.").isEqualTo(2);

        // @NotBlank 메시지 검증
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("필수 입력값입니다.")), 
                   "@NotBlank 메시지 포함 확인");
        // @Size(min) 메시지 검증
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("8자 이상 30자 이하로 입력해야 합니다.")), 
                   "@Size(min) 메시지 포함 확인");
    }

    @Test
    @DisplayName("DTO_유효성_실패: 비밀번호 길이가 최소 길이(8자) 미만이면 @Size 위반이 발생해야 한다.")
    void validation_fail_when_password_is_too_short() {
        // given
        UserLoginRequest request = createValidRequestBuilder()
                .password("short7") // 7자 (8자 미만)
                .build();

        // when
        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.size()).as("비밀번호 길이 위반이 1개 발생해야 합니다.").isEqualTo(1);
        ConstraintViolation<UserLoginRequest> violation = violations.iterator().next();
        assertThat(violation.getMessage()).contains("8자 이상 30자 이하로 입력해야 합니다.");
    }

    @Test
    @DisplayName("DTO_유효성_실패: 비밀번호 길이가 최대 길이(30자)를 초과하면 @Size 위반이 발생해야 한다.")
    void validation_fail_when_password_is_too_long() {
        // given
        String tooLongPassword = "a".repeat(31); 
        UserLoginRequest request = createValidRequestBuilder()
                .password(tooLongPassword)
                .build();

        // when
        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.size()).as("비밀번호 길이 위반이 1개 발생해야 합니다.").isEqualTo(1);
        ConstraintViolation<UserLoginRequest> violation = violations.iterator().next();
        assertThat(violation.getMessage()).contains("8자 이상 30자 이하로 입력해야 합니다.");
    }
}