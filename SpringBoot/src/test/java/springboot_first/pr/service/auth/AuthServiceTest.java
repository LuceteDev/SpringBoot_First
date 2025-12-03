package springboot_first.pr.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

// 필요한 DTO 및 Entity, Security Import
import springboot_first.pr.dto.userDTO.request.UserLoginRequest;
import springboot_first.pr.dto.userDTO.request.UserRegisterRequest;
import springboot_first.pr.dto.userDTO.request.UserIdFindRequest; 
import springboot_first.pr.dto.userDTO.response.UserIdFindResponse;
import springboot_first.pr.dto.userDTO.response.UserLoginResponse;
import springboot_first.pr.dto.userDTO.response.UserRegisterResponse;
import springboot_first.pr.entity.User;
import springboot_first.pr.exception.AuthenticationException;
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
 private UserLoginRequest validLoginRequestById;
 private UserLoginRequest validLoginRequestByEmail;
 private UserLoginRequest validLoginRequestByPhone;
 private UserIdFindRequest validIdFindRequest;
 private User mockUser;

 
 private final String RAW_PASSWORD = "password123!";
 private final String ENCODED_PASSWORD = "hashed_and_salted_pw";
 // Service가 userId 기반으로 생성할 것으로 예상되는 이메일 주소
 private final String TEST_EMAIL = "test@email.com"; 
 private final String TEST_USER_ID = "testuser123"; 
 private final String TEST_PHONE_NUMBER = "010-1234-5678";
 private final String TEST_USERNAME = "홍길동";
 private final String MOCK_ACCESS_TOKEN = "mock-access-token-123";
 private final String MOCK_REFRESH_TOKEN = "mock-refresh-token-456";

 /**
 * 💡 헬퍼 메서드: 기본적으로 유효한 UserRegisterRequest 객체를 생성하여 반환 - 요청
 */
 private UserRegisterRequest createValidRegisterRequest() {
  return UserRegisterRequest.builder()
  .userId(TEST_USER_ID)
  .username(TEST_USERNAME)
  .password(RAW_PASSWORD)
  .phoneNumber(TEST_PHONE_NUMBER)
  .build();
 }
 
 /**
 * 💡 헬퍼 메서드: ID 찾기 요청 DTO 생성
 */
 private UserIdFindRequest createValidIdFindRequest() {
  return new UserIdFindRequest(TEST_PHONE_NUMBER, TEST_USERNAME);
 }
 

 /**
 * 💡 헬퍼 메서드: 로그인 요청 DTO 생성 (식별자별)
 */
 private UserLoginRequest createValidLoginRequest(String identifier) {
  return UserLoginRequest.builder()
  .emailOrIdOrPhone(identifier)
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
  .email(TEST_EMAIL) // 저장된 User 엔티티에는 전체 이메일 주소가 있어야 함
  .username(TEST_USERNAME)
  .password(encodedPw)
  .phoneNumber(TEST_PHONE_NUMBER)
  .build();
 }

 
 @BeforeEach
 void setup() {
  this.validRegisterRequest = createValidRegisterRequest();
  this.validLoginRequestById = createValidLoginRequest(TEST_USER_ID);
  this.validLoginRequestByEmail = createValidLoginRequest(TEST_EMAIL);
  this.validLoginRequestByPhone = createValidLoginRequest(TEST_PHONE_NUMBER);
  this.validIdFindRequest = createValidIdFindRequest();
  this.mockUser = createMockSavedUser(ENCODED_PASSWORD);
 }


  // =================================================================================
  // 회원가입 성공(✅) (Register) 테스트 
  // =================================================================================

@Test
 @DisplayName("회원가입_성공: 중복이 없으면 정상적으로 저장하고 응답을 반환한다")
 void register_success() {
  // given (준비)
  // 모든 중복 검사 통과 설정
  given(userRepository.existsByUserId(anyString())).willReturn(false);
  given(userRepository.existsByEmail(anyString())).willReturn(false);
  given(userRepository.existsByPhoneNumber(any())).willReturn(false);
  
  given(passwordEncoder.encode(validRegisterRequest.getPassword())).willReturn(ENCODED_PASSWORD);
  User savedUser = createMockSavedUser(ENCODED_PASSWORD);
  given(userRepository.save(any(User.class))).willReturn(savedUser);

  // when (실행)
  UserRegisterResponse response = authService.register(validRegisterRequest);

  // then (검증)
  assertNotNull(response); 
  assertEquals(TEST_USER_ID, response.getUserId());
  // 검증: save가 1회 호출되어야 함
  verify(userRepository, times(1)).save(any(User.class));
 }


 // =================================================================================
 // 회원가입 실패(❌) (Register) 테스트 
 // =================================================================================

  @Test
  @DisplayName("회원가입_실패: 사용자 ID가 중복되면_예외가_발생해야_하며_저장은_안_된다")
  void register_fail_duplicate_userId() {
    // given (준비)
    given(userRepository.existsByUserId(validRegisterRequest.getUserId())).willReturn(true);

    // when & then (실행 및 검증)
    assertThrows(AuthenticationException.class, () -> { 
    authService.register(validRegisterRequest);
    }, "사용자 ID가 중복되면 AuthenticationException이 발생해야 합니다.");

    // 검증: save는 호출되지 않아야 함
    verify(userRepository, times(0)).save(any(User.class));
  }


  @Test
  @DisplayName("회원가입_실패: 이메일(파생값)이 중복되면_예외가_발생해야_하며_저장은_안_된다")
  void register_fail_duplicate_email() {
      // given (준비)
      String expectedEmail = validRegisterRequest.getUserId() + "@email.com"; // 서비스 로직을 따른 예상 이메일

      // 1. userId 중복 없음 가정
      given(userRepository.existsByUserId(validRegisterRequest.getUserId())).willReturn(false); 
      // 2. 파생될 이메일(expectedEmail)이 이미 존재한다고 Mock 설정
      given(userRepository.existsByEmail(expectedEmail)).willReturn(true); 

      // when & then (실행 및 검증)
      assertThrows(AuthenticationException.class, () -> { 
          authService.register(validRegisterRequest);
      }, "Email이 중복되면 AuthenticationException이 발생해야 합니다.");

      // 검증: save는 호출되지 않아야 함
      verify(userRepository, times(0)).save(any(User.class));
      // verify: userId 검사 후 email 검사가 호출되었는지 확인
      verify(userRepository, times(1)).existsByUserId(validRegisterRequest.getUserId());
      verify(userRepository, times(1)).existsByEmail(expectedEmail);
  }
  
  @Test
  @DisplayName("회원가입_실패: 휴대폰 번호가 중복되면_예외가_발생해야_하며_저장은_안_된다")
  void register_fail_duplicate_phone() {
    // given (준비)
    given(userRepository.existsByUserId(anyString())).willReturn(false); 
    given(userRepository.existsByEmail(anyString())).willReturn(false); 
    given(userRepository.existsByPhoneNumber(validRegisterRequest.getPhoneNumber())).willReturn(true); 

    // when & then (실행 및 검증)
    assertThrows(AuthenticationException.class, () -> { 
    authService.register(validRegisterRequest);
    }, "휴대폰 번호가 중복되면 AuthenticationException이 발생해야 합니다.");

    // 검증: save는 호출되지 않아야 함
    verify(userRepository, times(0)).save(any(User.class));
  }

 
 // =================================================================================
 // 로그인 (Login) 성공(✅) 3가지 방식 테스트 (식별자 Mocking 확장)
 // =================================================================================

  @Test
  @DisplayName("로그인_성공: 1순위 식별자인 [ID]로 로그인 시 Access와 Refresh 토큰을 반환한다")
  void login_success_by_id() {
      // given (ID로 바로 찾았다고 가정: 1순위에서 성공)
      given(userRepository.findByUserId(validLoginRequestById.getEmailOrIdOrPhone()))
      .willReturn(Optional.of(mockUser));
      
      given(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).willReturn(true);
      given(tokenProvider.createAccessToken(mockUser)).willReturn(MOCK_ACCESS_TOKEN);
      given(tokenProvider.createRefreshToken(mockUser)).willReturn(MOCK_REFRESH_TOKEN);

      // when
      UserLoginResponse response = authService.login(validLoginRequestById);

      // then
      assertThat(response.getAccessToken()).isEqualTo(MOCK_ACCESS_TOKEN);
      // 검증: findByUserId만 호출되었는지 확인 (나머지는 호출되지 않아야 함: 숏 서킷)
      verify(userRepository, times(1)).findByUserId(anyString()); 
      verify(userRepository, times(0)).findByEmail(anyString());
      verify(userRepository, times(0)).findByPhoneNumber(anyString());
  }

  @Test
  @DisplayName("로그인_성공: 2순위 식별자인 [Email]로 로그인 시 토큰을 반환한다")
  void login_success_by_email() {
      // given (ID 실패 후 Email에서 성공: 2순위에서 성공)
      // 1. ID로 검색 시 실패 (Optional.empty())
      given(userRepository.findByUserId(anyString())).willReturn(Optional.empty()); 
      // 2. Email로 검색 시 성공
      given(userRepository.findByEmail(validLoginRequestByEmail.getEmailOrIdOrPhone()))
      .willReturn(Optional.of(mockUser));
      
      given(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).willReturn(true);
      given(tokenProvider.createAccessToken(mockUser)).willReturn(MOCK_ACCESS_TOKEN);
      given(tokenProvider.createRefreshToken(mockUser)).willReturn(MOCK_REFRESH_TOKEN);

      // when
      UserLoginResponse response = authService.login(validLoginRequestByEmail);

      // then
      assertThat(response.getAccessToken()).isEqualTo(MOCK_ACCESS_TOKEN);
      // 검증: findByUserId (1회) -> findByEmail (1회) 호출되었는지 확인. Phone은 호출되지 않아야 함.
      verify(userRepository, times(1)).findByUserId(anyString()); 
      verify(userRepository, times(1)).findByEmail(anyString());
      verify(userRepository, times(0)).findByPhoneNumber(anyString());
  }

  @Test
  @DisplayName("로그인_성공: 3순위 식별자인 [Phone Number]로 로그인 시 토큰을 반환한다")
  void login_success_by_phone() {
      // given (ID, Email 실패 후 Phone에서 성공: 3순위에서 성공)
      // 1. ID로 검색 시 실패
      given(userRepository.findByUserId(anyString())).willReturn(Optional.empty()); 
      // 2. Email로 검색 시 실패
      given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
      // 3. Phone Number로 검색 시 성공
      given(userRepository.findByPhoneNumber(validLoginRequestByPhone.getEmailOrIdOrPhone()))
      .willReturn(Optional.of(mockUser));
      
      given(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).willReturn(true);
      given(tokenProvider.createAccessToken(mockUser)).willReturn(MOCK_ACCESS_TOKEN);
      given(tokenProvider.createRefreshToken(mockUser)).willReturn(MOCK_REFRESH_TOKEN);

      // when
      UserLoginResponse response = authService.login(validLoginRequestByPhone);

      // then
      assertThat(response.getAccessToken()).isEqualTo(MOCK_ACCESS_TOKEN);
      // 검증: findByUserId (1회) -> findByEmail (1회) -> findByPhoneNumber (1회) 호출 확인
      verify(userRepository, times(1)).findByUserId(anyString()); 
      verify(userRepository, times(1)).findByEmail(anyString());
      verify(userRepository, times(1)).findByPhoneNumber(anyString());
  }

    // =================================================================================
    // 로그인 (Login) 실패(❌) 테스트 (식별자 Mocking 확장)
    // =================================================================================

    @Test
    @DisplayName("로그인_실패: 사용자를 찾을 수 없을 때_예외가_발생해야_한다")
    void login_fail_user_not_found() {
        // 모든 findBy... 메서드가 Optional.empty()를 반환하도록 설정
        given(userRepository.findByUserId(anyString())).willReturn(Optional.empty());
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(userRepository.findByPhoneNumber(anyString())).willReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () -> {
            authService.login(validLoginRequestByEmail);
        }, "모든 식별자로도 사용자를 찾을 수 없으면 AuthenticationException이 발생해야 합니다.");
        
        verify(tokenProvider, times(0)).createAccessToken(any());
    }

    @Test
    @DisplayName("로그인_실패: 비밀번호가 일치하지 않을 때_예외가_발생해야_한다")
    void login_fail_wrong_password() {
        // given (사용자는 ID로 찾았으나)
        given(userRepository.findByUserId(anyString()))
        .willReturn(Optional.of(mockUser));
        // 비밀번호가 일치하지 않음
        given(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).willReturn(false);

        // when & then
        assertThrows(AuthenticationException.class, () -> {
            authService.login(validLoginRequestById);
        }, "비밀번호가 일치하지 않으면 AuthenticationException이 발생해야 합니다.");
        
        verify(tokenProvider, times(0)).createAccessToken(any());
    }

 // =================================================================================
 // 계정 찾기 (Find Id) 성공(✅) 테스트 
 // =================================================================================
 
 @Test
 @DisplayName("ID찾기_성공: 휴대폰 번호와 본명이 일치하면 ID를 찾고 마스킹하여 반환해야 한다")
 void findIdByPhoneAndUsername_Success() {
  // given (준비)
  given(userRepository.findByPhoneNumberAndUsername(TEST_PHONE_NUMBER, TEST_USERNAME))
    .willReturn(Optional.of(mockUser));
  
  // 예상 마스킹 ID 계산 (TEST_USER_ID = "testuser123" -> t**********)
  String originalId = TEST_USER_ID; 
  String expectedMaskedId = originalId.substring(0, 1) + "*".repeat(originalId.length() - 1);

  // when (실행)
  UserIdFindResponse response = authService.findIdByPhoneAndUsername(validIdFindRequest);

  // then (검증)
  assertThat(response).isNotNull();
  assertThat(response.getMaskedUserId()).isEqualTo(expectedMaskedId);
  
  verify(userRepository, times(1)).findByPhoneNumberAndUsername(TEST_PHONE_NUMBER, TEST_USERNAME);
 }

  // =================================================================================
  // 계정 찾기 (Find Id) 실패(❌) 테스트 
  // =================================================================================

 @Test
 @DisplayName("ID찾기_실패: 정보가 불일치하여 사용자를 찾을 수 없을 때_예외가 발생해야 한다")
 void findIdByPhoneAndUsername_Fail_UserNotFound() {
  // given (준비)
  given(userRepository.findByPhoneNumberAndUsername(anyString(), anyString()))
    .willReturn(Optional.empty()); 
  
  // when (실행) & then (검증)
  assertThrows(AuthenticationException.class, () -> {
   authService.findIdByPhoneAndUsername(validIdFindRequest);
  }, "사용자 정보 불일치 시 AuthenticationException이 발생해야 합니다.");

  verify(userRepository, times(1)).findByPhoneNumberAndUsername(validIdFindRequest.getPhoneNumber(), validIdFindRequest.getUsername());
 }
 

}