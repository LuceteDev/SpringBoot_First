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


// [ID 찾기 요청 DTO]의 유효성 검사(@NotBlank, @Pattern) 규칙이 올바르게 작동하는지 확인하는 테스트

@DisplayName("UserIdFindRequest DTO 유효성 검사 테스트") // 1️⃣ 테스트 클래스에 이름 붙이기
class UserIdFindRequestTest {

    // 2️⃣ 유효성 검사를 수행할 Validator 객체 선언 및 정의
    private static Validator validator;

    // 3️⃣ 💡 @BeforeAll 선언하기 : 테스트 클래스가 시작되기 전, 딱 한 번만 실행되어 유효성 검사기를 초기화 역할
    @BeforeAll
    static void setUp() { // 2️⃣ 유효성 검사를 수행할 Validator 객체 선언 및 정의
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // 이렇게 한줄로도 작성 가능  : private final Validator 유효성_검사기 = Validation.buildDefaultValidatorFactory().getValidator();


    // ⚠️ 4️⃣ 테스트에 사용할 유효한 상수값 private 함수로 따로 빼기
    private final String VALID_PHONE = "010-1234-5678";
    private final String VALID_USERNAME = "홍길동";

    
    // 💡 5️⃣ 헬퍼 메서드: ✅ 유효한 기본 DTO 빌더를 생성하여 각 테스트의 중복 코드 줄이기
    private UserIdFindRequest.UserIdFindRequestBuilder createValidRequestBuilder() {
        return UserIdFindRequest.builder()
                .phoneNumber(VALID_PHONE)
                .username(VALID_USERNAME);
    }

    
    // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 영역 분리 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //


    // 6️⃣ ✅ 유효성 성공 테스트 작성하기 (@NotBlank, @Pattern 모두 통과)
    @Test //  이 메서드가 테스트 메서드임을 선언
    @DisplayName("성공: 휴대폰 번호가 유효한 형식(010-xxxx-xxxx)이면 위반이 없어야 한다.") // 테스트 클래스에 이름 붙이기
    void validation_success() {
        // given : DTO의 모든 규칙에 맞는 완벽한 형식의 요청
        UserIdFindRequest request = createValidRequestBuilder().build();

        // when : 유효성 검사 수행
        Set<ConstraintViolation<UserIdFindRequest>> violations = validator.validate(request);

        // then : 위반 사항이 0개여야 성공
        assertThat(violations).isEmpty();
    }


    // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 영역 분리 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //


    // 7️⃣ ❌ 휴대폰 번호 - @NotBlank 및 @Pattern 검증 실패

    @ParameterizedTest(name = "실패: 휴대폰 번호 입력값 '{0}'")
    // @ParameterizedTest: 여러 입력값(빈 문자열 "", 공백 문자열 " ")으로 반복 테스트 -> 어떻게 돌아가는지 알아보기
    @ValueSource(strings = {"", " "}) 
    // @ValueSource: 테스트에 사용할 입력값 목록 제공
    @DisplayName("실패: 휴대폰 번호가 공백이거나 빈 문자열이면 @NotBlank와 @Pattern 위반이 발생해야 한다.") // 테스트 클래스에 이름 붙이기
    void validation_fail_when_phone_number_is_blank(String blankValue) {
        // given : 휴대폰 번호만 공백/빈 값으로 설정. username은 유효하게 유지.
        UserIdFindRequest request = createValidRequestBuilder()
                .phoneNumber(blankValue)
                .build();

        // when : 유효성 검사 수행
        Set<ConstraintViolation<UserIdFindRequest>> violations = validator.validate(request);

        // then : 위반 사항 검증
        // ⚠️ 빈 문자열("")은 @NotBlank와 @Pattern 두 가지 모두 위반합니다.
        // 공백 문자열(" ")은 @NotBlank와 @Pattern 두 가지 모두 위반할 가능성이 높습니다.
        assertThat(violations.size()).as("휴대폰 번호 위반이 2개 발생해야 합니다.").isEqualTo(2);

        Set<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

        // @NotBlank 메시지 확인
        assertThat(messages).contains("휴대폰 번호는 필수 입력 값입니다.");
        // @Pattern 메시지 확인
        assertThat(messages).contains("유효한 휴대폰 번호 형식(010-xxxx-xxxx)이 아닙니다.");
    }


    // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 영역 분리 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //


    
    // 8️⃣ ❌ 휴대폰 번호 - @Pattern 검증 실패
    
    @ParameterizedTest(name = "실패: 휴대폰 번호 입력값 '{0}'")
    // DTO의 @Pattern 규칙 (^010-\d{4}-\d{4}$)에 어긋나는 값들
    @ValueSource(strings = {"01012345678",      // ❌ 하이픈 없음
                            "010-123-4567",     // ❌ 중간 숫자 3자리
                            "010-1234-567",     // ❌ 끝 숫자 3자리
                            "abc-1234-5678"})   // ❌ 숫자 외 문자 포함 
    @DisplayName("실패: 휴대폰 번호가 형식(@Pattern)에 맞지 않으면 위반이 발생해야 한다.") // 테스트 클래스에 이름 붙이기
    void validation_fail_when_phone_number_is_invalid_format(String invalidValue) {
        // given : 휴대폰 번호만 유효하지 않은 값으로 설정. (이는 @NotBlank는 통과합니다.)
        UserIdFindRequest request = createValidRequestBuilder()
                .phoneNumber(invalidValue)
                .build();

        // when : 유효성 검사 수행
        Set<ConstraintViolation<UserIdFindRequest>> violations = validator.validate(request);
        
        // then : @Pattern 위반만 1개 발생해야 합니다.
        assertThat(violations.size()).as("@Pattern 위반만 1개 발생해야 합니다.").isEqualTo(1);
        
        Set<String> messages = violations.stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.toSet());
            
        // @Pattern 메시지 확인
        assertThat(messages).contains("유효한 휴대폰 번호 형식(010-xxxx-xxxx)이 아닙니다.");
    }


    // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 영역 분리 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //


    // ❌ 사용자 이름 - @NotBlank 검증 실패
    @ParameterizedTest(name = "실패: 이름 입력값 '{0}'")
    @ValueSource(strings = {"", " "}) 
    @DisplayName("실패: 사용자 이름이 공백이거나 빈 문자열이면 @NotBlank 위반이 발생해야 한다.")
    void validation_fail_when_username_is_blank(String blankValue) {
        // given : 사용자 이름만 공백/빈 값으로 설정. phoneNumber는 유효하게 유지.
        UserIdFindRequest request = createValidRequestBuilder()
                .username(blankValue)
                .build();

        // when : 유효성 검사 수행
        Set<ConstraintViolation<UserIdFindRequest>> violations = validator.validate(request);

        // then : @NotBlank 위반 1개만 발생해야 합니다.
        assertThat(violations.size()).as("사용자 이름 위반 1개만 발생해야 합니다.").isEqualTo(1);
        
        Set<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

        // @NotBlank 메시지 확인
        assertThat(messages).contains("이름은 필수 입력 값입니다.");
    }


    // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 영역 분리 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //
    
    
    // ❌ 사용자 이름 - @Size 검증 실패
    @Test
    @DisplayName("실패: 사용자 이름이 최대 길이(50자)를 초과하면 위반이 발생해야 한다.")
    void validation_fail_when_username_is_too_long() {
        // given : 51자 이름 생성
        String tooLongUsername = "a".repeat(51);
        UserIdFindRequest request = createValidRequestBuilder()
                .username(tooLongUsername)
                .build();

        // when : 유효성 검사 수행
        Set<ConstraintViolation<UserIdFindRequest>> violations = validator.validate(request);

        // then : @Size 위반 1개만 발생해야 합니다.
        assertThat(violations.size()).as("사용자 이름 길이 위반 1개만 발생해야 합니다.").isEqualTo(1);
        
        Set<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        
        // @Size 메시지 확인
        assertThat(messages).contains("사용자 이름은 50자를 초과할 수 없습니다.");
    }

}