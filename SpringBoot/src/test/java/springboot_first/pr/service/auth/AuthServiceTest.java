package springboot_first.pr.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given; // Mockito의 BDDMokito를 사용하여 given 사용
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

// 필요한 DTO 및 Entity, Security Import
import springboot_first.pr.dto.userDTO.request.UserLoginRequest;
import springboot_first.pr.dto.userDTO.request.UserRegisterRequest;
import springboot_first.pr.dto.userDTO.request.UserIdFindRequest; // 💡 [추가] ID 찾기 요청 DTO
import springboot_first.pr.dto.userDTO.response.UserIdFindResponse;
import springboot_first.pr.dto.userDTO.response.UserLoginResponse;
import springboot_first.pr.dto.userDTO.response.UserRegisterResponse;
import springboot_first.pr.entity.User;
import springboot_first.pr.exception.AuthenticationException; // 예외 클래스 가정
import springboot_first.pr.repository.UserRepository;
import springboot_first.pr.security.TokenProvider; 

@ExtendWith(MockitoExtension.class)
@DisplayName("Service 단위 테스트: AuthService - 계정 및 인증 관련 로직")
class AuthServiceTest {

    // 1. 가짜(Mock) 객체 선언: 외부 의존성
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenProvider tokenProvider; 
    
    // 2. 테스트 대상(Service)에 Mock 객체를 주입합니다.
    @InjectMocks
    private AuthService authService;
    
    // 테스트용 상수 및 변수 선언
    private UserRegisterRequest validRegisterRequest;
    private UserLoginRequest validLoginRequest;
    private UserIdFindRequest validFindRequest; // 💡 [추가] ID 찾기 요청 DTO
    private User mockUser;
    
    private final String RAW_PASSWORD = "password123";
    private final String ENCODED_PASSWORD = "hashed_and_salted_pw";
    private final String TEST_EMAIL = "test@email.com";
    private final String TEST_USER_ID = "testuser123"; // 💡 마스킹 테스트를 위해 길게 변경
    private final String TEST_PHONE_NUMBER = "010-1234-5678";
    private final String MOCK_ACCESS_TOKEN = "mock-access-token-123";
    private final String MOCK_REFRESH_TOKEN = "mock-refresh-token-456";
    private final String TEST_USERNAME = "홍길동";

    /**
     * 💡 헬퍼 메서드: 기본적으로 유효한 UserRegisterRequest 객체를 생성하여 반환 - 요청
     */
    private UserRegisterRequest createValidRegisterRequest() {
     return UserRegisterRequest.builder()
       .email(TEST_EMAIL) 
       .username(TEST_USERNAME)
       .password(RAW_PASSWORD)
       .phoneNumber(TEST_PHONE_NUMBER)
       .build();
    }
    
    /**
     * 💡 헬퍼 메서드: ID 찾기 요청 DTO 생성
     */
    private UserIdFindRequest createValidFindRequest() {
     return new UserIdFindRequest(TEST_PHONE_NUMBER, TEST_USERNAME);
    }

    /**
     * 💡 헬퍼 메서드: 로그인 요청 DTO 생성 (Email을 식별자로 사용)
     */
    private UserLoginRequest createValidLoginRequest() {
     return UserLoginRequest.builder()
       .emailOrIdOrPhone(TEST_EMAIL)
       .password(RAW_PASSWORD)
       .build();
    }
    
    /**
     * 💡 헬퍼 메서드: 저장된 Mock User 엔티티 생성
     */
    private User createMockSavedUser(String encodedPw) {
     return User.builder()
       .id(1L) // DB ID 부여
       .userId(TEST_USER_ID) 
       .email(TEST_EMAIL)
       .username(TEST_USERNAME)
       .password(encodedPw)
       .phoneNumber(TEST_PHONE_NUMBER)
       .build();
    }
    
    @BeforeEach
    void setup() {
     this.validRegisterRequest = createValidRegisterRequest();
     this.validLoginRequest = createValidLoginRequest();
     this.validFindRequest = createValidFindRequest(); // 💡 [추가] 초기화
     this.mockUser = createMockSavedUser(ENCODED_PASSWORD);
    }

    // =================================================================================
    // 회원가입 (Register) 테스트 (기존 코드 유지)
    // =================================================================================

    @Test
    @DisplayName("회원가입_실패: 이메일이 중복되면_예외가_발생해야_하며_저장은_안_된다")
    void register_fail_duplicate_email() {
     given(userRepository.existsByEmail(validRegisterRequest.getEmail())).willReturn(true);

     assertThrows(RuntimeException.class, () -> {
        authService.register(validRegisterRequest);
     }, "Email이 중복되면 RuntimeException이 발생해야 합니다.");

     verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입_성공: 중복이 없으면 정상적으로 저장하고 응답을 반환한다")
    void register_success() {
     // given (준비)
     given(userRepository.existsByEmail(any())).willReturn(false);
     given(userRepository.existsByPhoneNumber(any())).willReturn(false);
     given(passwordEncoder.encode(validRegisterRequest.getPassword())).willReturn(ENCODED_PASSWORD);
     User savedUser = createMockSavedUser(ENCODED_PASSWORD);
     given(userRepository.save(any(User.class))).willReturn(savedUser);

     // when (실행)
     UserRegisterResponse response = authService.register(validRegisterRequest);

     // then (검증)
     assertNotNull(response); 
     assertEquals(TEST_USER_ID, response.getUserId());
     verify(userRepository, times(1)).save(any(User.class));
    }
    
    // =================================================================================
    // 로그인 (Login) 테스트 (기존 코드 유지)
    // =================================================================================

    @Test
    @DisplayName("로그인_성공: 유효한 사용자 정보와 비밀번호로_Access와_Refresh_토큰을_반환한다")
    void login_success() {
     // given
     given(userRepository.findByEmail(validLoginRequest.getEmailOrIdOrPhone()))
        .willReturn(Optional.of(mockUser));
     given(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).willReturn(true);
     given(tokenProvider.createAccessToken(mockUser)).willReturn(MOCK_ACCESS_TOKEN);
     given(tokenProvider.createRefreshToken(mockUser)).willReturn(MOCK_REFRESH_TOKEN);

     // when
     UserLoginResponse response = authService.login(validLoginRequest);

     // then
     assertNotNull(response, "응답 DTO는 null이 아니어야 합니다.");
     assertEquals(MOCK_ACCESS_TOKEN, response.getAccessToken(), "Access Token이 일치해야 합니다.");
     assertEquals(MOCK_REFRESH_TOKEN, response.getRefreshToken(), "Refresh Token이 일치해야 합니다.");
     assertEquals(mockUser.getUserId(), response.getUserId());
     verify(tokenProvider, times(1)).createAccessToken(mockUser);
     verify(tokenProvider, times(1)).createRefreshToken(mockUser);
    }

    @Test
    @DisplayName("로그인_실패: 사용자를 찾을 수 없을 때_예외가_발생해야_한다")
    void login_fail_user_not_found() {
     given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
     given(userRepository.findByPhoneNumber(anyString())).willReturn(Optional.empty());

     assertThrows(AuthenticationException.class, () -> {
        authService.login(validLoginRequest);
     }, "사용자를 찾을 수 없으면 AuthenticationException이 발생해야 합니다.");
     
     verify(tokenProvider, times(0)).createAccessToken(any());
    }

    @Test
    @DisplayName("로그인_실패: 비밀번호가 일치하지 않을 때_예외가_발생해야_한다")
    void login_fail_wrong_password() {
     // given
     given(userRepository.findByEmail(validLoginRequest.getEmailOrIdOrPhone()))
        .willReturn(Optional.of(mockUser));
     given(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).willReturn(false);

     // when & then
     assertThrows(AuthenticationException.class, () -> {
        authService.login(validLoginRequest);
     }, "비밀번호가 일치하지 않으면 AuthenticationException이 발생해야 합니다.");
     
     verify(tokenProvider, times(0)).createAccessToken(any());
    }

    // =================================================================================
    // 계정 찾기 (Find Id) 테스트 (새로운 로직 반영)
    // =================================================================================
    
    @Test
    @DisplayName("ID찾기_성공: 휴대폰 번호와 본명이 일치하면 ID를 찾고 마스킹하여 반환해야 한다")
    void findIdByPhoneAndUsername_Success() {
        // given (준비)
        // Mock 설정: Repository가 User를 반환하도록 설정
        given(userRepository.findByPhoneNumberAndUsername(TEST_PHONE_NUMBER, TEST_USERNAME))
                .willReturn(Optional.of(mockUser));
        
        // 예상 마스킹 ID 계산 (TEST_USER_ID = "testuser123" -> t************)
        String originalId = TEST_USER_ID; // "testuser123" (11글자)
        String expectedMaskedId = originalId.substring(0, 1) + "*".repeat(originalId.length() - 1); // "t**********"

        // when (실행)
        UserIdFindResponse response = authService.findIdByPhoneAndUsername(validFindRequest);

        // then (검증)
        // 1. 응답 DTO가 null이 아닌지 확인
        assertThat(response).isNotNull();
        // 2. 마스킹된 ID가 예상 값과 일치하는지 확인 (핵심 검증)
        assertThat(response.getMaskedUserId()).isEqualTo(expectedMaskedId);
        
        // 3. Mock 객체 호출 검증: Repository가 정확히 1번 호출되었는지 확인
        verify(userRepository, times(1)).findByPhoneNumberAndUsername(TEST_PHONE_NUMBER, TEST_USERNAME);
    }

    @Test
    @DisplayName("ID찾기_실패: 정보가 불일치하여 사용자를 찾을 수 없을 때_예외가 발생해야 한다")
    void findIdByPhoneAndUsername_Fail_UserNotFound() {
        // given (준비)
        // Mock 설정: Repository가 Optional.empty() 반환하도록 설정
        given(userRepository.findByPhoneNumberAndUsername(anyString(), anyString()))
                .willReturn(Optional.empty()); 
        
        // when (실행) & then (검증)
        // assertThrows 사용: 사용자 불일치 시 AuthenticationException이 발생하는지 확인
        assertThrows(AuthenticationException.class, () -> {
            authService.findIdByPhoneAndUsername(validFindRequest);
        }, "사용자 정보 불일치 시 AuthenticationException이 발생해야 합니다.");

        // 검증: Repository가 1번 호출되었는지 확인
        verify(userRepository, times(1)).findByPhoneNumberAndUsername(validFindRequest.getPhoneNumber(), validFindRequest.getUsername());
    }
}