package springboot_first.pr.dto.userDTO.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserPasswordResetRequest 유효성 검사 테스트")
class UserPasswordResetRequestTest {

    // 🔧 검사 도구 (고정)
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    // 📌 상수: 유효한 테스트 데이터
    private static final String 유효한_USER_ID = "testUser123";
    private static final String 유효한_휴대폰번호 = "010-1234-5678";
    private static final String 유효한_비밀번호 = "Test1234!@#";

    // 🛠️ MethodSource: 잘못된 휴대폰 번호 목록
    private static Stream<String> 잘못된_휴대폰번호_목록() {
        return Stream.of(
                "01012345678",      // 하이픈 없음
                "010-123-5678",     // 중간 자리수 부족
                "010-12345-678",    // 마지막 자리수 부족
                "011-1234-5678",    // 010이 아님
                "010-abcd-5678",    // 숫자가 아님
                "010-1234-567"      // 마지막 자리수 부족
        );
    }

    // 🛠️ MethodSource: 잘못된 비밀번호 목록
    private static Stream<String> 잘못된_비밀번호_목록() {
        return Stream.of(
                "Test1!",           // 8자 미만
                "test1234!@#",      // 대문자 없음
                "TEST1234!@#",      // 소문자 없음
                "TestTest!@#",      // 숫자 없음
                "Test12345678",     // 특수문자 없음
                "Test 1234!@#",     // 공백 포함
                "a".repeat(21)      // 20자 초과
        );
    }

    // 🛠️ 기본 유효한 DTO 빌더
    private UserPasswordResetRequest.UserPasswordResetRequestBuilder 유효한_DTO_빌더() {
        return UserPasswordResetRequest.builder()
                .userId(유효한_USER_ID)
                .phoneNumber(유효한_휴대폰번호)
                .newPassword(유효한_비밀번호);
    }

    // ✅ 테스트 1: 성공 케이스
    @Test
    @DisplayName("성공: 모든 필드가 유효하면 통과한다")
    void 유효한_데이터_테스트() {
        // given
        UserPasswordResetRequest request = 유효한_DTO_빌더().build();

        // when
        Set<ConstraintViolation<UserPasswordResetRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    // ❌ 테스트 2: userId 실패 케이스
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    @DisplayName("실패: userId가 null, 빈 문자열, 공백이면 실패한다")
    void userId_필수값_검증(String invalidUserId) {
        // given
        UserPasswordResetRequest request = 유효한_DTO_빌더()
                .userId(invalidUserId)
                .build();

        // when
        Set<ConstraintViolation<UserPasswordResetRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("userId");
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .anyMatch(message -> message.contains("사용자 ID는 필수 입력값입니다"));
    }

    @Test
    @DisplayName("실패: userId가 20자를 초과하면 실패한다")
    void userId_길이초과_검증() {
        // given
        UserPasswordResetRequest request = 유효한_DTO_빌더()
                .userId("a".repeat(21))
                .build();

        // when
        Set<ConstraintViolation<UserPasswordResetRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("사용자 ID는 20자를 초과할 수 없습니다.");
    }

    // ❌ 테스트 3: 휴대폰 번호 실패 케이스
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    @DisplayName("실패: 휴대폰 번호가 null, 빈 문자열, 공백이면 실패한다")
    void 휴대폰번호_필수값_검증(String invalidPhone) {
        // given
        UserPasswordResetRequest request = 유효한_DTO_빌더()
                .phoneNumber(invalidPhone)
                .build();

        // when
        Set<ConstraintViolation<UserPasswordResetRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("phoneNumber");
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .anyMatch(message -> message.contains("휴대폰 번호는 필수 입력 값입니다"));
    }

    @ParameterizedTest
    @MethodSource("잘못된_휴대폰번호_목록") // 👈 수정됨!
    @DisplayName("실패: 휴대폰 번호 형식이 잘못되면 실패한다")
    void 휴대폰번호_형식_검증(String invalidPhone) {
        // given
        UserPasswordResetRequest request = 유효한_DTO_빌더()
                .phoneNumber(invalidPhone)
                .build();

        // when
        Set<ConstraintViolation<UserPasswordResetRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("유효한 휴대폰 번호 형식(010-xxxx-xxxx)이 아닙니다.");
    }

    // ❌ 테스트 4: 비밀번호 실패 케이스
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    @DisplayName("실패: 비밀번호가 null, 빈 문자열, 공백이면 실패한다")
    void 비밀번호_필수값_검증(String invalidPassword) {
        // given
        UserPasswordResetRequest request = 유효한_DTO_빌더()
                .newPassword(invalidPassword)
                .build();

        // when
        Set<ConstraintViolation<UserPasswordResetRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("newPassword");
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .anyMatch(message -> message.contains("새 비밀번호는 필수 입력값입니다"));
    }

    @ParameterizedTest
    @MethodSource("잘못된_비밀번호_목록") // 👈 이건 정상!
    @DisplayName("실패: 비밀번호가 규칙에 맞지 않으면 실패한다")
    void 비밀번호_형식_검증(String invalidPassword) {
        // given
        UserPasswordResetRequest request = 유효한_DTO_빌더()
                .newPassword(invalidPassword)
                .build();

        // when
        Set<ConstraintViolation<UserPasswordResetRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .anyMatch(message -> 
                    message.contains("비밀번호는 8~20자의 영문 대소문자, 숫자, 특수 문자") ||
                    message.contains("새 비밀번호는 필수 입력값입니다")
                );
    }

    // 🎯 경계값 테스트
    @Test
    @DisplayName("성공: 비밀번호가 정확히 8자이면 통과한다")
    void 비밀번호_최소길이_경계값_검증() {
        // given
        UserPasswordResetRequest request = 유효한_DTO_빌더()
                .newPassword("Test123!")
                .build();

        // when
        Set<ConstraintViolation<UserPasswordResetRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("성공: 비밀번호가 정확히 20자이면 통과한다")
    void 비밀번호_최대길이_경계값_검증() {
        // given
        UserPasswordResetRequest request = 유효한_DTO_빌더()
                .newPassword("Test1234!@#Test1234!")
                .build();

        // when
        Set<ConstraintViolation<UserPasswordResetRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }
}

