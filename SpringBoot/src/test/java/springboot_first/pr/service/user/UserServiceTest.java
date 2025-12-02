package springboot_first.pr.service.user;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import springboot_first.pr.dto.userDTO.request.UserPasswordChangeRequest;
import springboot_first.pr.entity.User;
import springboot_first.pr.exception.AuthenticationException;
import springboot_first.pr.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Service 단위 테스트: UserService - 사용자 정보 변경 로직")
class UserServiceTest {

    // 1. Mock 객체 선언: UserService의 외부 의존성
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    // 2. 테스트 대상(SUT)에 Mock 객체를 주입합니다.
    @InjectMocks
    private UserService userService; // 💡 이제 이 인스턴스(userService)를 호출합니다.

    // 테스트용 상수 및 변수 선언
    private UserPasswordChangeRequest validPasswordChangeRequest;
    private User mockUser;

    private final String RAW_PASSWORD = "password123!";
    private final String ENCODED_PASSWORD = "hashed_and_salted_pw";
    private final String TEST_USER_ID = "testuser123";
    private final String NEW_ENCODED_PASSWORD = "new_hashed_and_salted_pw";
    private final String NEW_PASSWORD = "newsecurepassword456";

    /**
     * 💡 헬퍼 메서드: 비밀번호 변경 요청 DTO 생성
     */
    private UserPasswordChangeRequest createValidPasswordChangeRequest() {
        return UserPasswordChangeRequest.builder()
            .oldPassword(RAW_PASSWORD)
            .newPassword(NEW_PASSWORD)
            .build();
    }

    /**
     * 💡 헬퍼 메서드: 저장된 Mock User 엔티티 생성
     */
    private User createMockSavedUser(String encodedPw) {
        return User.builder()
            .id(1L)
            .userId(TEST_USER_ID)
            .password(encodedPw)
            .build();
    }

    @BeforeEach
    void setup() {
        this.validPasswordChangeRequest = createValidPasswordChangeRequest();
        this.mockUser = createMockSavedUser(ENCODED_PASSWORD);
    }

    // =================================================================================
    // 비밀번호 변경 (Password Change) 성공(✅) 테스트
    // =================================================================================
    @Test
    @DisplayName("비밀번호변경_성공: 유효한 사용자 ID와 기존 비밀번호로 비밀번호를 성공적으로 변경해야 한다")
    void password_change_success() {
        // given (준비)
        // 1. 유저 ID로 사용자를 찾음
        given(userRepository.findByUserId(TEST_USER_ID)).willReturn(Optional.of(mockUser));

        // 2. 기존 비밀번호 일치 (matches 호출 시 true 반환)
        given(passwordEncoder.matches(validPasswordChangeRequest.getOldPassword(), ENCODED_PASSWORD)).willReturn(true);

        // 3. 새 비밀번호를 인코딩함
        given(passwordEncoder.encode(NEW_PASSWORD)).willReturn(NEW_ENCODED_PASSWORD);

        // when (실행)
        // 💡 올바르게 초기화된 userService 인스턴스 사용
        userService.changePassword(TEST_USER_ID, validPasswordChangeRequest);

        // then (검증)
        // 1. findByUserId, matches, encode가 모두 호출되었는지 확인
        verify(userRepository, times(1)).findByUserId(TEST_USER_ID);
        verify(passwordEncoder, times(1)).matches(validPasswordChangeRequest.getOldPassword(), ENCODED_PASSWORD);
        verify(passwordEncoder, times(1)).encode(NEW_PASSWORD);

        // 2. save는 변경 감지(Dirty Checking)로 처리되므로 호출되지 않음을 가정하고 검증 제거
    }


    // =================================================================================
    // 비밀번호 변경 (Password Change) 실패(❌) 테스트
    // =================================================================================

    @Test
    @DisplayName("비밀번호변경_실패_1: 사용자 ID를 찾을 수 없을 때_예외가 발생해야 한다")
    void password_change_fail_user_not_found() {
        // given (준비)
        // 유효하지 않은 ID로 검색 시 Optional.empty() 반환
        given(userRepository.findByUserId(anyString())).willReturn(Optional.empty());

        // when & then (실행 및 검증)
        assertThrows(AuthenticationException.class, () -> {
            userService.changePassword(TEST_USER_ID, validPasswordChangeRequest);
        }, "사용자 ID를 찾을 수 없으면 AuthenticationException이 발생해야 합니다.");

        // 검증: 비밀번호 인코딩이나 저장은 호출되지 않아야 함
        verify(passwordEncoder, times(0)).encode(anyString());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    @DisplayName("비밀번호변경_실패_2: 기존 비밀번호가 일치하지 않을 때_예외가 발생해야 한다")
    void password_change_fail_password_mismatch() {
        // given (준비)
        // 1. 유저 ID로 사용자를 찾음
        given(userRepository.findByUserId(TEST_USER_ID)).willReturn(Optional.of(mockUser));

        // 2. 기존 비밀번호가 일치하지 않는다고 설정 (matches 호출 시 false 반환)
        given(passwordEncoder.matches(validPasswordChangeRequest.getOldPassword(), ENCODED_PASSWORD)).willReturn(false);

        // when & then (실행 및 검증)
        assertThrows(AuthenticationException.class, () -> {
            userService.changePassword(TEST_USER_ID, validPasswordChangeRequest);
        }, "기존 비밀번호가 일치하지 않으면 AuthenticationException이 발생해야 합니다.");

        // 검증:
        // 1. 기존 비밀번호 일치 확인(matches)까지는 호출되지만, 새 비밀번호 인코딩(encode)은 호출되지 않아야 함
        verify(passwordEncoder, times(1)).matches(validPasswordChangeRequest.getOldPassword(), ENCODED_PASSWORD);
        verify(passwordEncoder, times(0)).encode(anyString());
    }
}