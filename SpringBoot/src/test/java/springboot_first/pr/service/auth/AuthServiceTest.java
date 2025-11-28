package springboot_first.pr.service.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import springboot_first.pr.dto.userDTO.request.UserRegisterRequest;
import springboot_first.pr.dto.userDTO.response.UserRegisterResponse;
import springboot_first.pr.entity.User;
import springboot_first.pr.repository.UserRepository;

// Mockito 확장 기능을 사용하여 Mock 객체를 활성화합니다.
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    // 1. 가짜(Mock) 객체 선언: 외부 의존성
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    
    // 2. 테스트 대상(Service)에 Mock 객체를 주입합니다.
    @InjectMocks
    private AuthService authService;
    
       // 테스트용 요청 DTO 및 암호화된 비밀번호 상수를 미리 선언
    private UserRegisterRequest validRequest;
    private final String ENCODED_PASSWORD = "hashed_and_salted_pw";
    private final String TEST_EMAIL = "test@email.com";
    private final String TEST_USER_ID_PREFIX = "test"; // Mocking 시 이메일에서 추출될 값

    /**
     * 💡 헬퍼 메서드: 기본적으로 유효한 UserRegisterRequest 객체를 생성하여 반환 - 요청
     * 💡 [변경] userId 필드가 제거된 DTO 구조를 반영합니다.
     */
    private UserRegisterRequest createValidRequest() {
        return UserRegisterRequest.builder()
                .email(TEST_EMAIL) 
                .username("Tester")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .build();
    }
    
    /**
     * 💡 헬퍼 메서드: 저장 후 반환될 Mock User 엔티티를 생성 (DB ID 1L 부여) - 응답
     * 💡 [변경] userId는 email에서 파생된 값(prefix)으로 설정합니다.
     */
    private User createMockSavedUser(UserRegisterRequest request, String encodedPw) {
        return User.builder()
                .id(1L) // DB ID 부여
                .userId(TEST_USER_ID_PREFIX) // 💡 이메일에서 추출한 값 (Service에서 처리할 로직을 Mock에 반영)
                .email(request.getEmail())
                .username(request.getUsername())
                .password(encodedPw)
                .phoneNumber(request.getPhoneNumber())
                .build();
    }
    
    /**
     * ✅ @BeforeEach: 각 테스트 메서드가 실행되기 전에 항상 실행되어 객체를 초기화합니다.
     */
    @BeforeEach
    void setup() {
        this.validRequest = createValidRequest();
    }

    // =================================================================================
    // 2. 실패 시나리오
    // =================================================================================

    // ❌ [제거됨] userId는 이제 고유하지 않으므로 userId 중복 테스트는 제거됩니다.

    @Test
    @DisplayName("회원가입_실패: 이메일이 중복되면_예외가_발생해야_하며_저장은_안_된다")
    void register_fail_duplicate_email() {
        // given
        // 1. 이메일 중복은 True로 실패
        given(userRepository.existsByEmail(validRequest.getEmail())).willReturn(true);

        // when & then
        assertThrows(RuntimeException.class, () -> {
            authService.register(validRequest);
        }, "Email이 중복되면 RuntimeException이 발생해야 합니다.");

        // 검증: 핵심 로직(save, encode)이 호출되면 안 됩니다.
        verify(userRepository, times(0)).save(any(User.class));
        verify(passwordEncoder, times(0)).encode(any(String.class));
    }

    @Test
    @DisplayName("회원가입_실패: 전화번호가 중복되면_예외가_발생해야_하며_저장은_안_된다")
    void register_fail_duplicate_phoneNumber() {
        // given
        // 1. Email 중복은 False로 통과 (먼저 체크되어야 함)
        given(userRepository.existsByEmail(validRequest.getEmail())).willReturn(false);
        // 2. 전화번호 중복은 True로 실패
        given(userRepository.existsByPhoneNumber(validRequest.getPhoneNumber())).willReturn(true);

        // when & then
        assertThrows(RuntimeException.class, () -> {
            authService.register(validRequest);
        }, "전화번호가 중복되면 RuntimeException이 발생해야 합니다.");

        // 검증: 핵심 로직(save, encode)이 호출되면 안 됩니다.
        verify(userRepository, times(0)).save(any(User.class));
        verify(passwordEncoder, times(0)).encode(any(String.class));
    }


    // =================================================================================
    // 1. 성공 시나리오
    // =================================================================================

    @Test
    @DisplayName("회원가입_성공: 중복이 없으면 정상적으로 저장하고 응답을 반환한다")
    void register_success() {
        // given (준비)

        // 1-1. 중복 확인은 모두 False
        // 💡 [변경] existsByUserId는 제거되고, existsByEmail이 먼저 체크됩니다.
        given(userRepository.existsByEmail(any())).willReturn(false);
        given(userRepository.existsByPhoneNumber(any())).willReturn(false);

        // 1-2. 비밀번호 인코딩 Mocking
        given(passwordEncoder.encode(validRequest.getPassword())).willReturn(ENCODED_PASSWORD);

        // 1-3. 저장 Mocking: 헬퍼 메서드를 사용해 저장 후 User 객체 반환 가정
        User savedUser = createMockSavedUser(validRequest, ENCODED_PASSWORD);
        given(userRepository.save(any(User.class))).willReturn(savedUser);

        // when (실행)
        UserRegisterResponse response = authService.register(validRequest);

        // then (검증)
        assertNotNull(response); 
        assertEquals(TEST_USER_ID_PREFIX, response.getUserId(), "userId는 email prefix와 일치해야 합니다.");
        assertEquals(TEST_EMAIL, response.getEmail(), "email이 응답에 포함되어야 합니다."); // 💡 응답 DTO에 email이 포함되었는지 확인
        assertEquals(1L, response.getId()); 

        // 검증: 핵심 로직 호출 확인
        verify(passwordEncoder, times(1)).encode(validRequest.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    // 테스트용 요청 DTO 및 암호화된 비밀번호 상수를 미리 선언
    // private UserRegisterRequest validRequest;
    // private final String ENCODED_PASSWORD = "hashed_and_salted_pw";

    // /**
    //  * 💡 헬퍼 메서드: 기본적으로 유효한 UserRegisterRequest 객체를 생성하여 반환 - 요청
    //  */
    // private UserRegisterRequest createValidRequest() {
    //     return new UserRegisterRequest("testId", "test@email.com", "Tester", "password123", "010-1234-5678");
    // }
    
    // /**
    //  * 💡 헬퍼 메서드: 저장 후 반환될 Mock User 엔티티를 생성 (DB ID 1L 부여) - 응답 DTO에 있는 Entity -> DTO 변환해서 User 객체로 반환하는 것
    //  */
    // private User createMockSavedUser(UserRegisterRequest request, String encodedPw) {
    //     return User.builder()
    //             .id(1L) // DB ID 부여
    //             .userId(request.getUserId())
    //             .email(request.getEmail())
    //             .username(request.getUsername())
    //             .password(encodedPw)
    //             .phoneNumber(request.getPhoneNumber())
    //             .build();
    // }
    
    // /**
    //  * ✅ @BeforeEach: 각 테스트 메서드가 실행되기 전에 항상 실행되어 객체를 초기화합니다.
    //  */
    // @BeforeEach
    // void setup() {
    //     this.validRequest = createValidRequest();
    // }

    // // =================================================================================
    // // 2. 실패 시나리오
    // // =================================================================================

    // @Test
    // @DisplayName("회원가입_실패: 아이디가 중복되면_예외가_발생해야_하며_저장은_안_된다")
    // void register_fail_duplicate_id() {
    //   // given
    //   // ID 중복 확인 쿼리가 True를 반환하도록 설정
    //   given(userRepository.existsByUserId(validRequest.getUserId())).willReturn(true);

    //   // when & then (실행 시 예외 발생 검증)
    //   assertThrows(RuntimeException.class, () -> {
    //   authService.register(validRequest);
    //   }, "ID가 중복되면 RuntimeException이 발생해야 합니다.");

    //   // 검증: 핵심 로직(save, encode)이 호출되면 안 됩니다.
    //   verify(userRepository, times(0)).save(any(User.class));
    //   verify(passwordEncoder, times(0)).encode(any(String.class));
    // }

    // @Test
    // @DisplayName("회원가입_실패: 이메일이 중복되면_예외가_발생해야_하며_저장은_안_된다")
    // void register_fail_duplicate_email() {
    //   // given
    //   // 1. ID 중복은 False로 통과
    //   given(userRepository.existsByUserId(validRequest.getUserId())).willReturn(false);
    //   // 2. 이메일 중복은 True로 실패
    //   given(userRepository.existsByEmail(validRequest.getEmail())).willReturn(true);

    //   // when & then
    //   assertThrows(RuntimeException.class, () -> {
    //   authService.register(validRequest);
    //   }, "Email이 중복되면 RuntimeException이 발생해야 합니다.");

    //   // 검증: 핵심 로직(save, encode)이 호출되면 안 됩니다.
    //   verify(userRepository, times(0)).save(any(User.class));
    //   verify(passwordEncoder, times(0)).encode(any(String.class));
    // }

    // @Test
    // @DisplayName("회원가입_실패: 전화번호가 중복되면_예외가_발생해야_하며_저장은_안_된다")
    // void register_fail_duplicate_phoneNumber() {
    //   // given
    //   // 1. ID, Email 중복은 False로 통과
    //   given(userRepository.existsByUserId(validRequest.getUserId())).willReturn(false);
    //   given(userRepository.existsByEmail(validRequest.getEmail())).willReturn(false);
    //   // 2. 전화번호 중복은 True로 실패
    //   given(userRepository.existsByPhoneNumber(validRequest.getPhoneNumber())).willReturn(true);

    //   // when & then
    //   assertThrows(RuntimeException.class, () -> {
    //   authService.register(validRequest);
    //   }, "전화번호가 중복되면 RuntimeException이 발생해야 합니다.");

    //   // 검증: 핵심 로직(save, encode)이 호출되면 안 됩니다.
    //   verify(userRepository, times(0)).save(any(User.class));
    //   verify(passwordEncoder, times(0)).encode(any(String.class));
    // }


    // // =================================================================================
    // // 1. 성공 시나리오
    // // =================================================================================

    // @Test
    // @DisplayName("회원가입_성공: 중복이 없으면 정상적으로 저장하고 응답을 반환한다")
    // void register_success() {
    //   // given (준비)

    //   // 1-1. 중복 확인은 모두 False
    //   given(userRepository.existsByUserId(any())).willReturn(false);
    //   given(userRepository.existsByEmail(any())).willReturn(false);
    //   given(userRepository.existsByPhoneNumber(any())).willReturn(false);

    //   // 1-2. 비밀번호 인코딩 Mocking
    //   given(passwordEncoder.encode(validRequest.getPassword())).willReturn(ENCODED_PASSWORD);

    //   // 1-3. 저장 Mocking: 헬퍼 메서드를 사용해 저장 후 User 객체 반환 가정
    //   User savedUser = createMockSavedUser(validRequest, ENCODED_PASSWORD);
    //   given(userRepository.save(any(User.class))).willReturn(savedUser);

    //   // when (실행)
    //   UserRegisterResponse response = authService.register(validRequest);

    //   // then (검증)
    //   assertNotNull(response); 
    //   assertEquals(validRequest.getUserId(), response.getUserId());
    //   assertEquals(1L, response.getId()); 

    //   // 검증: 핵심 로직 호출 확인
    //   verify(passwordEncoder, times(1)).encode(validRequest.getPassword());
    //   verify(userRepository, times(1)).save(any(User.class));
    // }

}