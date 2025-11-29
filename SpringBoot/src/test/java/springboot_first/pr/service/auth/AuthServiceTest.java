package springboot_first.pr.service.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
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

// 필요한 DTO 및 Entity, Security Import (실제 환경에 맞게 조정 필요)
import springboot_first.pr.dto.userDTO.request.UserLoginRequest;
import springboot_first.pr.dto.userDTO.request.UserRegisterRequest;
import springboot_first.pr.dto.userDTO.response.UserLoginResponse;
import springboot_first.pr.dto.userDTO.response.UserRegisterResponse;
import springboot_first.pr.entity.User;
import springboot_first.pr.exception.AuthenticationException; // 예외 클래스 가정
import springboot_first.pr.repository.UserRepository;
import springboot_first.pr.security.TokenProvider; // 💡 [추가] 토큰 프로바이더 Import

// Mockito 확장 기능을 사용하여 Mock 객체를 활성화합니다.
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

 // 1. 가짜(Mock) 객체 선언: 외부 의존성
 @Mock
 private UserRepository userRepository;
 @Mock
 private PasswordEncoder passwordEncoder;
 @Mock
 private TokenProvider tokenProvider; // 💡 [추가] 로그인 테스트를 위한 토큰 프로바이더 Mock
 
 // 2. 테스트 대상(Service)에 Mock 객체를 주입합니다.
 @InjectMocks
 private AuthService authService;
 
 // 테스트용 상수 및 변수 선언
 private UserRegisterRequest validRegisterRequest;
 private UserLoginRequest validLoginRequest;
 private User mockUser;
 private final String RAW_PASSWORD = "password123";
 private final String ENCODED_PASSWORD = "hashed_and_salted_pw";
 private final String TEST_EMAIL = "test@email.com";
 private final String TEST_USER_ID = "test"; 
 private final String MOCK_ACCESS_TOKEN = "mock-access-token-123";
 private final String MOCK_REFRESH_TOKEN = "mock-refresh-token-456";

 /**
  * 💡 헬퍼 메서드: 기본적으로 유효한 UserRegisterRequest 객체를 생성하여 반환 - 요청
  */
 private UserRegisterRequest createValidRegisterRequest() {
  return UserRegisterRequest.builder()
    .email(TEST_EMAIL) 
    .username("Tester")
    .password(RAW_PASSWORD)
    .phoneNumber("010-1234-5678")
    .build();
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
    .username("Tester")
    .password(encodedPw)
    .phoneNumber("010-1234-5678")
    .build();
 }
 
 /**
  * ✅ @BeforeEach: 각 테스트 메서드가 실행되기 전에 항상 실행되어 객체를 초기화합니다.
  */
 @BeforeEach
 void setup() {
  this.validRegisterRequest = createValidRegisterRequest();
  this.validLoginRequest = createValidLoginRequest();
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

 // ... (나머지 register 테스트는 생략되었으나 원본 파일에는 그대로 유지되어야 함)
 
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
 // 로그인 (Login) 테스트 (새로 추가)
 // =================================================================================

 @Test
 @DisplayName("로그인_성공: 유효한 사용자 정보와 비밀번호로_Access와_Refresh_토큰을_반환한다")
 void login_success() {
  // given
  // 1. 사용자 조회 성공 가정
  given(userRepository.findByEmail(validLoginRequest.getEmailOrIdOrPhone()))
   .willReturn(Optional.of(mockUser));
  // 2. 비밀번호 일치 가정
  given(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).willReturn(true);
  // 3. 토큰 발급 Mocking (가장 중요한 수정 부분)
  given(tokenProvider.createAccessToken(mockUser)).willReturn(MOCK_ACCESS_TOKEN);
  given(tokenProvider.createRefreshToken(mockUser)).willReturn(MOCK_REFRESH_TOKEN);

  // when
  UserLoginResponse response = authService.login(validLoginRequest);

  // then
  assertNotNull(response, "응답 DTO는 null이 아니어야 합니다.");
  
  // 💡 [검증] 토큰 2개 모두가 정확히 응답에 포함되었는지 확인
  assertEquals(MOCK_ACCESS_TOKEN, response.getAccessToken(), "Access Token이 일치해야 합니다.");
  assertEquals(MOCK_REFRESH_TOKEN, response.getRefreshToken(), "Refresh Token이 일치해야 합니다.");
  
  // 사용자 정보 확인
  assertEquals(mockUser.getUserId(), response.getUserId());
  
  // 검증: 토큰 생성 메서드 2개가 모두 호출되었는지 확인
  verify(tokenProvider, times(1)).createAccessToken(mockUser);
  verify(tokenProvider, times(1)).createRefreshToken(mockUser);
 }

 @Test
 @DisplayName("로그인_실패: 사용자를 찾을 수 없을 때_예외가_발생해야_한다")
 void login_fail_user_not_found() {
  // given: Email로 찾았을 때 Optional.empty() 반환 가정
  given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
  // 💡 [수정] findByPhoneNumber까지 체이닝되므로, 두 번째 쿼리도 실패해야 최종 실패
  given(userRepository.findByPhoneNumber(anyString())).willReturn(Optional.empty());


  // when & then
  assertThrows(AuthenticationException.class, () -> {
   authService.login(validLoginRequest);
  }, "사용자를 찾을 수 없으면 AuthenticationException이 발생해야 합니다.");
  
  // 검증: 토큰 생성은 호출되면 안 됩니다.
  verify(tokenProvider, times(0)).createAccessToken(any());
 }

 @Test
 @DisplayName("로그인_실패: 비밀번호가 일치하지 않을 때_예외가_발생해야_한다")
 void login_fail_wrong_password() {
  // given
  // 1. 사용자 조회 성공 가정
  given(userRepository.findByEmail(validLoginRequest.getEmailOrIdOrPhone()))
   .willReturn(Optional.of(mockUser));
  // 2. 비밀번호 불일치 가정
  given(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).willReturn(false);

  // when & then
  assertThrows(AuthenticationException.class, () -> {
   authService.login(validLoginRequest);
  }, "비밀번호가 일치하지 않으면 AuthenticationException이 발생해야 합니다.");
  
  // 검증: 토큰 생성은 호출되면 안 됩니다.
  verify(tokenProvider, times(0)).createAccessToken(any());
 }
}