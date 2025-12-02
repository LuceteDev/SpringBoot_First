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

@DisplayName("DTO 단위 테스트: UserPasswordChangeRequest - 유효성 검사")
class UserPasswordChangeRequestTest {

    // 1️⃣ DTO의 @Valid 어노테이션 검사를 수동으로 수행할 Validator
    private static Validator 유효성_검사기;

    // 2️⃣ 테스트에 사용할 유효한/무효한 상수
    private static final String 유효한_비밀번호 = "StrongPass123!";
    private static final String 짧은_비밀번호 = "Short1!";
    private static final String 대문자_없는_비밀번호 = "strongpass123!";
    private static final String 공백_포함_비밀번호 = "Strong Pass123!";

    @BeforeAll
    static void 전체_테스트_설정() {
        ValidatorFactory 팩토리 = Validation.buildDefaultValidatorFactory();
        유효성_검사기 = 팩토리.getValidator();
    }

    /**
     * 기본적으로 유효한 DTO 빌더를 반환하는 헬퍼 메서드.
     */
    private UserPasswordChangeRequest.UserPasswordChangeRequestBuilder 유효한_요청_빌더_생성() {
        return UserPasswordChangeRequest.builder()
                .oldPassword(유효한_비밀번호)
                .newPassword(유효한_비밀번호);
    }

    // =================================================================================
    // 1. 전체 성공 케이스
    // =================================================================================

    @Test
    @DisplayName("성공_케이스: 모든 필드가 유효하면 위반이 없어야 한다.")
    void 성공_유효성_검사_통과() {
        // given
        UserPasswordChangeRequest 유효한_요청 = 유효한_요청_빌더_생성().build();

        // when
        Set<ConstraintViolation<UserPasswordChangeRequest>> 위반_목록 = 유효성_검사기.validate(유효한_요청);

        // then
        assertThat(위반_목록).isEmpty();
    }

    // =================================================================================
    // 2. oldPassword 유효성 실패 테스트 (NotBlank)
    // =================================================================================

    @ParameterizedTest(name = "실패_oldPassword: 입력값 '{0}' (NotBlank 위반)")
    @ValueSource(strings = {"", " "}) // 빈 문자열("")과 공백 문자열(" ")으로 테스트
    @DisplayName("실패_oldPassword: 현재 비밀번호가 공백 또는 빈 문자열이면 위반이 발생해야 한다.")
    void 실패_oldPassword_필수값_위반(String 무효한_현재_비밀번호) {
        // given
        UserPasswordChangeRequest 무효한_요청 = 유효한_요청_빌더_생성()
                .oldPassword(무효한_현재_비밀번호)
                .build();

        // when
        Set<ConstraintViolation<UserPasswordChangeRequest>> 위반_목록 = 유효성_검사기.validate(무효한_요청);

        // then
        assertThat(위반_목록).isNotEmpty();

        // ⚠️ 수정 로직: AssertJ extracting 대신 Stream을 사용하여 경로와 메시지를 직접 검증
        Set<String> 위반_경로 = 위반_목록.stream().map(v -> v.getPropertyPath().toString()).collect(Collectors.toSet());
        Set<String> 위반_메시지 = 위반_목록.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());

        assertTrue(위반_경로.contains("oldPassword"), "위반 필드가 'oldPassword'여야 합니다. (실제 경로: " + 위반_경로 + ")");
        assertTrue(위반_메시지.contains("현재 비밀번호를 입력해주세요."), "NotBlank 메시지가 포함되어야 합니다.");
    }

    // =================================================================================
    // 3. newPassword 유효성 실패 테스트 (NotBlank, Pattern 위반)
    // =================================================================================

    @ParameterizedTest(name = "실패_newPassword_NotBlank: 입력값 '{0}'")
    @ValueSource(strings = {"", " "}) // 빈 문자열("")과 공백 문자열(" ")으로 테스트
    @DisplayName("실패_newPassword: 새 비밀번호가 공백 또는 빈 문자열이면 위반이 발생해야 한다.")
    void 실패_newPassword_필수값_위반(String 무효한_새_비밀번호) {
        // given
        UserPasswordChangeRequest 무효한_요청 = 유효한_요청_빌더_생성()
                .newPassword(무효한_새_비밀번호)
                .build();

        // when
        Set<ConstraintViolation<UserPasswordChangeRequest>> 위반_목록 = 유효성_검사기.validate(무효한_요청);

        // then
        assertThat(위반_목록).isNotEmpty();

        // ⚠️ 수정 로직: 위반 개수가 2개 이상임을 확인하고, 두 메시지 모두 포함되었는지 검증
        Set<String> 위반_경로 = 위반_목록.stream().map(v -> v.getPropertyPath().toString()).collect(Collectors.toSet());
        Set<String> 위반_메시지 = 위반_목록.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());

        assertTrue(위반_경로.contains("newPassword"), "위반 필드가 'newPassword'여야 합니다. (실제 경로: " + 위반_경로 + ")");

        // @NotBlank 메시지 확인
        assertTrue(위반_메시지.contains("새로운 비밀번호를 입력해주세요."),
                   "NotBlank 메시지가 포함되어야 합니다.");
                   
        // @Pattern 메시지 확인 (복잡성 규칙)
        assertTrue(위반_메시지.stream().anyMatch(m -> m.contains("8~20자의 영문 대소문자")),
                   "Pattern 메시지가 포함되어야 합니다.");
    }

    @ParameterizedTest(name = "실패_newPassword_Pattern: 입력값 '{0}'")
    @ValueSource(strings = {
            짧은_비밀번호,              // 길이 미달
            대문자_없는_비밀번호,        // 대문자 없음
            "NoNumberPass!",          // 숫자 없음
            "NoSymbolPass1234",       // 특수 문자 없음
            공백_포함_비밀번호          // 공백 포함 (새로운 규칙 검증)
    })
    @DisplayName("실패_newPassword: 새 비밀번호가 복잡성 또는 길이 패턴을 위반하면 위반이 발생해야 한다.")
    void 실패_newPassword_패턴_위반(String 패턴_위반_비밀번호) {
        // given
        UserPasswordChangeRequest 무효한_요청 = 유효한_요청_빌더_생성()
                .newPassword(패턴_위반_비밀번호)
                .build();

        // when
        Set<ConstraintViolation<UserPasswordChangeRequest>> 위반_목록 = 유효성_검사기.validate(무효한_요청);

        // then
        assertThat(위반_목록).isNotEmpty();

        Set<String> 위반_경로 = 위반_목록.stream().map(v -> v.getPropertyPath().toString()).collect(Collectors.toSet());
        Set<String> 위반_메시지 = 위반_목록.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());

        assertTrue(위반_경로.contains("newPassword"), "위반 필드가 'newPassword'여야 합니다. (실제 경로: " + 위반_경로 + ")");

        // @Pattern 메시지가 포함되어 있는지 확인
        assertTrue(위반_메시지.stream().anyMatch(m -> m.contains("8~20자의 영문 대소문자")),
                   "Pattern 메시지가 포함되어야 합니다.");
    }
}