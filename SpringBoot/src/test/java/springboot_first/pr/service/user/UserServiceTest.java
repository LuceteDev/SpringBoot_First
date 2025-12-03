package springboot_first.pr.service.user;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;

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
import springboot_first.pr.dto.userDTO.request.UserPasswordResetRequest;
import springboot_first.pr.dto.userDTO.response.UserPasswordResetResponse; // 💡 응답 DTO 임포트
import springboot_first.pr.entity.User;
import springboot_first.pr.exception.AuthenticationException;
import springboot_first.pr.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Service 단위 테스트: UserService - 사용자 정보 변경 및 재설정 로직")
class UserServiceTest {

    // 1. Mock 객체 선언: UserService의 외부 의존성
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    // 2. 테스트 대상(SUT)에 Mock 객체를 주입합니다.
    @InjectMocks
    private UserService userService; 

    // 공통 테스트용 상수 및 변수 선언
    private UserPasswordResetRequest validPasswordResetRequest;
    private UserPasswordChangeRequest validPasswordChangeRequest;
    private User mockUser;

    private final String TEST_USER_ID = "testuser123";
    private final String TEST_PHONE_NUMBER = "010-1234-5678";
    
    // 비밀번호 변경 관련 상수
    private final String RAW_PASSWORD = "password123!";
    private final String ENCODED_PASSWORD = "hashed_and_salted_pw";
    private final String NEW_PASSWORD = "newsecurepassword456";
    private final String NEW_ENCODED_PASSWORD = "new_hashed_and_salted_pw";

    // 비밀번호 재설정 관련 상수
    private final String RESET_NEW_PASSWORD = "resetpass!@#";
    private final String RESET_ENCODED_PASSWORD = "reset_hashed_pw";


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
     * 💡 헬퍼 메서드: 비밀번호 재설정 요청 DTO 생성
     */
    private UserPasswordResetRequest createValidPasswordResetRequest() {
        return UserPasswordResetRequest.builder()
                .userId(TEST_USER_ID)
                .phoneNumber(TEST_PHONE_NUMBER)
                .newPassword(RESET_NEW_PASSWORD)
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
                .phoneNumber(TEST_PHONE_NUMBER)
        .build();
    }

    @BeforeEach
    void setup() {
        this.validPasswordChangeRequest = createValidPasswordChangeRequest();
            this.validPasswordResetRequest = createValidPasswordResetRequest();
        this.mockUser = createMockSavedUser(ENCODED_PASSWORD);
    }


    // =================================================================================
    // 1. 비밀번호 재설정 (Password Reset) 테스트 (핵심)
    // =================================================================================

    @Test
    @DisplayName("비밀번호재설정_성공: 유효한 ID/휴대폰 번호로 비밀번호를 재설정하고 성공 응답을 반환해야 한다.")
    void password_reset_success() {
        // given (준비)
        // 1. findByUserIdAndPhoneNumber Mocking: 사용자 찾기 성공
        given(userRepository.findByUserIdAndPhoneNumber(TEST_USER_ID, TEST_PHONE_NUMBER))
            .willReturn(Optional.of(mockUser));

        // 2. encode Mocking: 새 비밀번호 인코딩 처리
        given(passwordEncoder.encode(RESET_NEW_PASSWORD)).willReturn(RESET_ENCODED_PASSWORD);

        // when (실행)
        UserPasswordResetResponse response = userService.resetPassword(validPasswordResetRequest);

        // then (검증)
        // 1. Repository 호출 및 인코딩 호출 검증
        verify(userRepository, times(1)).findByUserIdAndPhoneNumber(TEST_USER_ID, TEST_PHONE_NUMBER);
        verify(passwordEncoder, times(1)).encode(RESET_NEW_PASSWORD);
        
        // 2. DB 업데이트 로직 검증: findUser 엔티티의 비밀번호가 변경되었는지 확인 (Dirty Checking/save()에 대한 결과)
        // 서비스 코드가 save()를 명시적으로 호출하므로, save 호출도 검증합니다.
        assertThat(mockUser.getPassword()).isEqualTo(RESET_ENCODED_PASSWORD);
        verify(userRepository, times(1)).save(mockUser);
        
        // 3. 응답 검증
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getUserId()).isEqualTo(TEST_USER_ID);
    }

    @Test
    @DisplayName("비밀번호재설정_실패_1: ID와 휴대폰 번호가 불일치할 때_AuthenticationException 발생")
    void password_reset_fail_user_not_found() {
        // given (준비)
        // findByUserIdAndPhoneNumber Mocking: Optional.empty() 반환 설정
        given(userRepository.findByUserIdAndPhoneNumber(anyString(), anyString()))
            .willReturn(Optional.empty());

        // when & then (실행 및 검증)
        assertThrows(AuthenticationException.class, () -> {
            userService.resetPassword(validPasswordResetRequest);
        }, "사용자 정보 불일치 시 AuthenticationException이 발생해야 합니다.");

        // 검증:
        // findByUserIdAndPhoneNumber만 호출되고, 후속 로직(인코딩, 저장)은 호출되지 않아야 함
        verify(userRepository, times(1)).findByUserIdAndPhoneNumber(anyString(), anyString());
        verify(passwordEncoder, times(0)).encode(anyString());
        verify(userRepository, times(0)).save(any(User.class));
    }


    // =================================================================================
    // 2. 비밀번호 변경 (Password Change) 테스트 (기존 기능 유지)
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
        userService.changePassword(TEST_USER_ID, validPasswordChangeRequest);

        // then (검증)
        // findByUserId, matches, encode가 모두 호출되었는지 확인
        verify(userRepository, times(1)).findByUserId(TEST_USER_ID);
        verify(passwordEncoder, times(1)).matches(validPasswordChangeRequest.getOldPassword(), ENCODED_PASSWORD);
        verify(passwordEncoder, times(1)).encode(NEW_PASSWORD);
            
            // 엔티티의 비밀번호 필드가 업데이트되었는지 확인
            assertThat(mockUser.getPassword()).isEqualTo(NEW_ENCODED_PASSWORD);

            // NOTE: changePassword는 보통 @Transactional 내부에서 변경 감지로 save()가 생략될 수 있지만,
            // resetPassword는 명시적으로 save()를 호출하므로, 여기서는 save() 검증을 생략하거나 로직에 맞게 조정해야 합니다.
            // 기존 코드의 가정을 따름: save 호출 검증 제거
    }


    @Test
    @DisplayName("비밀번호변경_실패_1: 사용자 ID를 찾을 수 없을 때_예외가 발생해야 한다")
    void password_change_fail_user_not_found() {
        // given (준비)
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
        given(userRepository.findByUserId(TEST_USER_ID)).willReturn(Optional.of(mockUser));
        given(passwordEncoder.matches(validPasswordChangeRequest.getOldPassword(), ENCODED_PASSWORD)).willReturn(false);

        // when & then (실행 및 검증)
        assertThrows(AuthenticationException.class, () -> {
        userService.changePassword(TEST_USER_ID, validPasswordChangeRequest);
        }, "기존 비밀번호가 일치하지 않으면 AuthenticationException이 발생해야 합니다.");

        // 검증:
        verify(passwordEncoder, times(1)).matches(validPasswordChangeRequest.getOldPassword(), ENCODED_PASSWORD);
        verify(passwordEncoder, times(0)).encode(anyString());
    }
}