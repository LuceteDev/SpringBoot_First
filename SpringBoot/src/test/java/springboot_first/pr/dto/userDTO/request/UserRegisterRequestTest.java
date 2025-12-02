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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UserRegisterRequest DTO 유효성 검사 (ID 기반)")
class UserRegisterRequestTest {

    private static Validator validator;
    
    // 유효한 기본 상수 정의
    private static final String VALID_USER_ID = "test_user123";
    private static final String VALID_USERNAME = "홍길동";
    private static final String VALID_PASSWORD = "StrongPass1234!"; // 복잡성 패턴 만족
    private static final String VALID_PHONE = "010-1234-5678";


    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    /**
     * 기본적으로 유효한 DTO 빌더를 반환하는 헬퍼 메서드.
     * toBuilder()를 사용하면 특정 필드만 변경하여 실패 케이스를 쉽게 만들 수 있습니다.
     */
    private UserRegisterRequest.UserRegisterRequestBuilder createValidRequestBuilder() {
        return UserRegisterRequest.builder()
                .userId(VALID_USER_ID)
                .username(VALID_USERNAME)
                .password(VALID_PASSWORD)
                .phoneNumber(VALID_PHONE);
    }

    // =================================================================================
    // 1. 전체 성공 케이스
    // =================================================================================
    
    @Test
    @DisplayName("성공: 모든 필드가 유효하면 위반이 없어야 한다.")
    void validation_success_all_fields_valid() {
        // given
        UserRegisterRequest request = createValidRequestBuilder().build();
        
        // when
        Set<ConstraintViolation<UserRegisterRequest>> violations = validator.validate(request);
        
        // then
        assertThat(violations).isEmpty();
    }
    
    // =================================================================================
    // 2. UserId 유효성 실패 테스트 (새로 추가됨)
    // =================================================================================
    
    @ParameterizedTest(name = "실패_UserId: 입력값 '{0}'")
    @ValueSource(strings = {
            "", " ",                       // @NotBlank 위반
            "usr",                         // 4자 미만 (@Size/Pattern 위반)
            "too_long_user_id_21_chars_x", // 20자 초과 (@Size/Pattern 위반)
            "USER_CAPS",                   // 대문자 포함 (@Pattern 위반 - 소문자 강제)
            "user.with.dot",               // 점(.) 포함 (@Pattern 위반)
            "한글id"                         // 한글 포함 (@Pattern 위반)
    })
    @DisplayName("실패_UserId: ID가 필수값, 길이(4~20), 또는 패턴(영소문자/숫자/-/_ 만)을 위반하면 위반이 발생해야 한다.")
    void validation_fail_invalid_userId_format(String invalidUserId) {
        // given
        UserRegisterRequest request = createValidRequestBuilder()
                .userId(invalidUserId) 
                .build();
        
        // when
        Set<ConstraintViolation<UserRegisterRequest>> violations = validator.validate(request);
        
        // then
        assertThat(violations).isNotEmpty();
        
        // 예상 메시지 검증 (두 개의 패턴 중 하나라도 걸려야 함)
        Set<String> messages = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
        assertTrue(messages.stream().anyMatch(m -> 
                        m.contains("필수 입력") || 
                        m.contains("4~20자의 영문 소문자, 숫자, 특수 기호(-, _)만 사용할 수 있습니다.")), 
                "UserId 관련 유효성 위반 메시지가 포함되어야 합니다.");
    }

    // =================================================================================
    // 3. Password 유효성 실패 테스트
    // =================================================================================
    
    @ParameterizedTest(name = "실패_Password: 입력값 '{0}'")
    @ValueSource(strings = {
            "", " ",                       // @NotBlank 위반
            "short",                       // 8자 미만 (@Size 위반)
            "nouppercase1234!",            // 대문자 없음 (@Pattern 위반)
            "NOLOWERCASE1234!",            // 소문자 없음 (@Pattern 위반)
            "NoSymbol1234Ab",              // 특수문자 없음 (@Pattern 위반)
            "StrongPass!!"                 // 숫자 없음 (@Pattern 위반 - 추가 보완)
    })
    @DisplayName("실패_Password: 비밀번호가 길이 또는 복잡성 패턴을 위반하면 위반이 발생해야 한다.")
    void validation_fail_password_format_and_size(String invalidPassword) {
        // given
        UserRegisterRequest request = createValidRequestBuilder()
                .password(invalidPassword)
                .build();
        
        // when
        Set<ConstraintViolation<UserRegisterRequest>> violations = validator.validate(request);
        
        // then
        assertThat(violations).isNotEmpty();
        
        // 위반 메시지 중 핵심 내용이 포함되어 있는지 확인
        Set<String> messages = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
        assertTrue(messages.stream().anyMatch(m -> 
                        m.contains("8자 이상") || 
                        m.contains("특수 문자") || 
                        m.contains("필수 입력")),
                "Password 관련 유효성 위반 메시지(길이/복잡성)가 포함되어야 합니다.");
    }
    
    // =================================================================================
    // 4. PhoneNumber 유효성 실패 테스트
    // =================================================================================

    @ParameterizedTest(name = "실패_PhoneNumber: 입력값 '{0}'")
    @ValueSource(strings = {
            "", " ",                       // @NotBlank 위반
            "01012345678",                  // @Pattern 위반 (하이픈 누락)
            "010-123-4567",                 // @Pattern 위반 (중간 3자리)
            "011-1234-5678"                 // @Pattern 위반 (앞자리 010 아님)
    })
    @DisplayName("실패_PhoneNumber: 휴대폰 번호가 필수값(@NotBlank)이거나 형식(@Pattern)에 맞지 않으면 위반이 발생해야 한다.")
    void validation_fail_invalid_phone_number_format(String invalidPhone) {
        // given
        UserRegisterRequest request = createValidRequestBuilder()
                .phoneNumber(invalidPhone)
                .build();
        
        // when
        Set<ConstraintViolation<UserRegisterRequest>> violations = validator.validate(request);
        
        // then
        assertThat(violations).isNotEmpty();
        
        Set<String> messages = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
        assertTrue(messages.stream().anyMatch(m -> 
                        m.contains("휴대폰 번호는 필수 입력") || 
                        m.contains("유효한 휴대폰 번호 형식")),
                "PhoneNumber 관련 유효성 위반 메시지가 포함되어야 합니다.");
    }
    
    // =================================================================================
    // 5. Username 유효성 실패 테스트 (간단)
    // =================================================================================
    
    @Test
    @DisplayName("실패_Username: 사용자 이름이 공백이면 위반이 발생해야 한다.")
    void validation_fail_username_blank() {
        // given
        UserRegisterRequest request = createValidRequestBuilder().username(" ").build();
        
        // when
        Set<ConstraintViolation<UserRegisterRequest>> violations = validator.validate(request);
        
        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).contains("이름은 필수 입력 값입니다.");
    }
}