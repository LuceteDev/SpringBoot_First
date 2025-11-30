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

    // ✅ 유효성 성공 테스트 작성하기 (@NotBlank, @Pattern 모두 통과)
    @Test // 4️⃣ 이 메서드가 테스트 메서드임을 선언
    @DisplayName("성공: 휴대폰 번호가 유효한 형식(010-xxxx-xxxx)이면 위반이 없어야 한다.") // 테스트 클래스에 이름 붙이기
    void validation_success() {
        // 5️⃣ given (준비) : DTO의 @Pattern 규칙에 맞는 완벽한 형식의 휴대폰 번호
        UserIdFindRequest request = UserIdFindRequest.builder()
                .phoneNumber("010-1234-5678") // 💡 하이픈을 포함한 올바른 형식으로 수정
                .build();

        // 6️⃣ when (실행) : 유효성 검사 수행 
        // ⚠️ assertThat(messages).contains(...) 항상 사용하기 ‼️ : 검증 메시지를 비교해 정확한 원인으로 실패했는지 확인 가능
        Set<ConstraintViolation<UserIdFindRequest>> violations = validator.validate(request);

        // 7️⃣ then (검증) : 위반 사항이 0개여야 성공
        assertThat(violations).isEmpty();
    }

    // 8️⃣ 유효성 실패 테스트 작성하기 : @NotBlank 검증

    @ParameterizedTest(name = "실패: 입력값 '{0}'")
    // @ParameterizedTest: 여러 입력값(빈 문자열 "", 공백 문자열 " ")으로 반복 테스트 -> 어떻게 돌아가는지 알아보기
    @ValueSource(strings = {"", " "}) 
    // @ValueSource: 테스트에 사용할 입력값 목록 제공
    @DisplayName("실패: 휴대폰 번호가 공백이거나 빈 문자열이면 @NotBlank 위반이 발생해야 한다.") // 테스트 클래스에 이름 붙이기
    void validation_fail_when_phone_number_is_blank(String blankValue) {
        // 9️⃣ given (준비) : 공백 또는 빈 값으로 DTO 생성
        UserIdFindRequest request = UserIdFindRequest.builder()
                .phoneNumber(blankValue)
                .build();

        // 🔟 when (실행) : 유효성 검사 수행
        Set<ConstraintViolation<UserIdFindRequest>> violations = validator.validate(request);

        // 1️⃣1️⃣ then (검증) : @NotBlank 위반이 발생해야 한다.
        assertThat(violations).isNotEmpty();
        
        // 1️⃣2️⃣ @NotBlank 위반이 발생하고 메시지가 일치하는지 확인
        Set<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

        // ⚠️ 반드시 메시지를 비교해 정확한 원인(@NotBlank)으로 실패했는지 확인
        assertThat(messages).contains("휴대폰 번호는 필수 입력 값입니다.");

        // 💡 추가 검증: 빈 문자열("")의 경우 @NotBlank와 @Pattern 위반이 모두 발생해야 하므로 2개인지 확인
        // (공백(" ")의 경우도 패턴 위반으로 잡힐 가능성이 높음)
        // 위반 개수가 2개 이상인지 확인하는 것이 더 견고합니다.
        assertThat(violations.size()).as("@NotBlank와 @Pattern 모두 위반되어야 합니다.").isGreaterThanOrEqualTo(1);
    }
    
    // 1️⃣3️⃣ 유효성 실패 테스트 (Pattern 검증)
    
    @ParameterizedTest(name = "실패: 입력값 '{0}'")
    // DTO의 @Pattern 규칙 (^010-\d{4}-\d{4}$)에 어긋나는 값들
    @ValueSource(strings = {"01012345678",      // ❌ 하이픈 없음
                            "010-123-4567",     // ❌ 중간 숫자 3자리
                            "010-1234-567",     // ❌ 끝 숫자 3자리
                            "abc-1234-5678"})   // ❌ 숫자 외 문자 포함 
    @DisplayName("실패: 휴대폰 번호가 형식(@Pattern)에 맞지 않으면 위반이 발생해야 한다.") // 테스트 클래스에 이름 붙이기
    void validation_fail_when_phone_number_is_invalid_format(String invalidValue) {
        // 1️⃣4️⃣ given (준비) : 유효하지 않은 값으로 DTO 생성
        UserIdFindRequest request = UserIdFindRequest.builder()
                .phoneNumber(invalidValue)
                .build();

        // 1️⃣5️⃣ when (실행) : 유효성 검사 수행
        Set<ConstraintViolation<UserIdFindRequest>> violations = validator.validate(request);
        
        // 1️⃣6️⃣ then (검증) : 위반 사항이 1개 발생하고 메시지가 @Pattern 메시지와 일치해야 한다.
        assertThat(violations).isNotEmpty(); 

        // 1️⃣7️⃣ 위반 메시지를 Set으로 추출
        Set<String> messages = violations.stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.toSet());
            
        // 1️⃣8️⃣ 추출된 메시지 집합에 예상 메시지(@Pattern)가 포함되어 있는지 확인
        assertThat(messages).contains("유효한 휴대폰 번호 형식(010-xxxx-xxxx)이 아닙니다.");
        
        // 1️⃣9️⃣ 추가 검증: 이 테스트는 @NotBlank는 통과하고 @Pattern만 실패해야 하므로, 위반 개수가 1개인지 확인
        assertThat(violations.size()).as("@Pattern 위반만 1개 발생해야 합니다.").isEqualTo(1);
    }
}