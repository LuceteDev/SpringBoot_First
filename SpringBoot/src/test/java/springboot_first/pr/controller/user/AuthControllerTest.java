package springboot_first.pr.controller.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
// Spring Security 자동 설정을 제외하기 위해 Import
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration; 
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import springboot_first.pr.dto.userDTO.request.UserLoginRequest;
import springboot_first.pr.dto.userDTO.request.UserRegisterRequest;
import springboot_first.pr.dto.userDTO.request.UserIdFindRequest;
import springboot_first.pr.dto.userDTO.response.UserLoginResponse;
import springboot_first.pr.dto.userDTO.response.UserRegisterResponse;
import springboot_first.pr.dto.userDTO.response.UserIdFindResponse;
// 커스텀 예외 클래스 임포트
import springboot_first.pr.exception.AuthenticationException; 
import springboot_first.pr.exception.DuplicateUserException;
import springboot_first.pr.exception.InvalidCredentialException;
// ⭐️ 통역가(에러 핸들러) 클래스 임포트
import springboot_first.pr.handler.GlobalExceptionHandler;
import springboot_first.pr.service.auth.AuthService;

/**
 * AuthController WebMvc 테스트
 * - Service에서 발생하는 예외를 GlobalExceptionHandler가 HTTP 상태 코드로 잘 변환하는지 확인합니다.
 */
@WebMvcTest(
    controllers = AuthController.class,
    // 보안 설정을 제외합니다.
    excludeAutoConfiguration = SecurityAutoConfiguration.class 
)
// ⭐️ @Import를 사용하여 에러 처리기(통역가)를 테스트 환경에 명시적으로 추가합니다.
@Import(GlobalExceptionHandler.class) 
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    // 테스트용 상수
    private final String TEST_USER_ID = "testuser123"; 
    private final String FIXED_EMAIL_DOMAIN = "@email.com"; // 💡 서비스에서 사용하는 고정 도메인
    private final String TEST_PASSWORD = "Password123!"; // 💡 패턴에 맞게 수정
    private final String TEST_USERNAME = "Tester";
    private final String TEST_PHONE_NUMBER = "010-1234-5678";
    private final String MASKED_USER_ID = "t**********";

    // 💡 파생된 이메일: "testuser123@email.com"
    private final String DERIVED_EMAIL = TEST_USER_ID + FIXED_EMAIL_DOMAIN; 

    // 테스트용 DTO
    private UserRegisterRequest validRegisterRequest;
    private UserRegisterResponse successRegisterResponse;
    private UserLoginRequest validLoginRequest;
    private UserLoginResponse successLoginResponse;
    private UserIdFindRequest validFindIdRequest;

    @BeforeEach
    void setUp() {
        // [회원가입] 유효한 요청 DTO
        // 💡 수정: UserRegisterRequest는 이메일 대신 userId를 받도록 수정해야 합니다. (기존 코드를 따라 email -> userId로 변경 가정)
        // 만약 DTO에 email 필드가 없다면, 아래 코드를 사용해야 합니다.
        validRegisterRequest = UserRegisterRequest.builder()
            .userId(TEST_USER_ID) // 💡 userId로 변경
            .username(TEST_USERNAME)
            .password(TEST_PASSWORD)
            .phoneNumber(TEST_PHONE_NUMBER)
            .build();

        // [회원가입] 성공 응답 DTO (Service에서 생성되어 반환되는 값)
        // 💡 수정: 응답 DTO의 email은 서비스에서 파생된 전체 이메일 주소여야 합니다.
        successRegisterResponse = new UserRegisterResponse(
            1L, 
            TEST_USER_ID, 
            TEST_USERNAME, 
            DERIVED_EMAIL // 💡 파생된 전체 이메일 사용
        ); 
        
        // [로그인] 유효한 요청 DTO (식별자로 파생된 이메일 사용)
        // 💡 DTO의 필드명은 emailOrIdOrPhone이므로, 실제 로그인에 사용할 식별자(파생된 이메일 또는 ID)를 전달
        validLoginRequest = UserLoginRequest.builder()
            .emailOrIdOrPhone(DERIVED_EMAIL) // 💡 파생된 이메일로 로그인 요청 (또는 TEST_USER_ID 사용 가능)
            .password(TEST_PASSWORD)
            .build();

        // [로그인] 성공 응답 DTO 
        successLoginResponse = UserLoginResponse.builder()
            .id(1L)
            .userId(TEST_USER_ID)
            .username(TEST_USERNAME)
            .accessToken("mock-access-token-1234")
            .refreshToken("mock-refresh-token-5678")
            .build();

        // [ID 찾기] 유효한 요청 DTO 초기화
        validFindIdRequest = new UserIdFindRequest(TEST_PHONE_NUMBER, TEST_USERNAME);
    }
    
    // =================================================================================
    // 💡 헬퍼 메서드: POST 요청 시뮬레이션
    // =================================================================================

    private ResultActions performRegisterPost(Object requestDto) throws Exception {
        return mockMvc.perform(post("/api/auth/register") 
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)));
    }

    private ResultActions performLoginPost(Object requestDto) throws Exception {
        return mockMvc.perform(post("/api/auth/login") 
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)));
    }

    private ResultActions performFindIdPost(Object requestDto) throws Exception {
        return mockMvc.perform(post("/api/auth/find-id")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)));
    }


    // =================================================================================
    // 1. 회원가입 시나리오 (POST /api/auth/register)
    // =================================================================================

    @Test
    @DisplayName("회원가입_성공: 유효한 요청으로 201 CREATED를 반환한다")
    void register_success() throws Exception {
        // Given (준비): Service가 성공 응답을 반환하도록 Mocking
        given(authService.register(any(UserRegisterRequest.class)))
            .willReturn(successRegisterResponse);

        // When & Then (실행 및 검증): 201 Created 상태 코드 및 응답 필드 검증
        performRegisterPost(validRegisterRequest)
            .andExpect(status().isCreated()) 
            .andExpect(jsonPath("$.id").value(successRegisterResponse.getId())) 
            .andExpect(jsonPath("$.userId").value(successRegisterResponse.getUserId()))
            .andExpect(jsonPath("$.username").value(successRegisterResponse.getUsername()))
            // 💡 검증 추가: 파생된 이메일 주소가 응답에 포함되었는지 확인
            .andExpect(jsonPath("$.email").value(DERIVED_EMAIL)) 
            .andDo(print());
    }

    @Test
    @DisplayName("회원가입_실패: DTO 유효성 검사 실패(userId 공백) 시 400 BAD REQUEST를 반환한다")
    void register_fail_validation_blank_userId() throws Exception {
        // Given (준비): userId를 공백으로 설정하여 유효성 검사 실패 유도
        UserRegisterRequest invalidRequest = UserRegisterRequest.builder()
            .userId(" ") // 💡 공백 userId로 변경
            .username(TEST_USERNAME)
            .password(TEST_PASSWORD)
            .phoneNumber(TEST_PHONE_NUMBER)
            .build();
        
        // When & Then (실행 및 검증): 400 Bad Request와 에러 메시지 검증
        performRegisterPost(invalidRequest)
            .andExpect(status().isBadRequest()) 
            .andExpect(jsonPath("$.message").exists()) 
            .andDo(print());
    }

    @Test
    @DisplayName("회원가입_실패: 서비스에서 중복 예외 발생 시 400 BAD REQUEST를 반환해야 한다")
    void register_fail_service_exception_duplicate() throws Exception {
        // Given (약속): '가짜 AuthService'에게 "register 메서드가 호출되면 'DuplicateUserException' 에러를 던져줘"라고 약속합니다.
        String errorMessage = "회원가입 실패: 이미 가입된 사용자 ID 또는 이메일입니다.";
        given(authService.register(any(UserRegisterRequest.class)))
            // 이 에러가 던져지면 GlobalExceptionHandler가 400으로 바꿔줍니다.
            .willThrow(new DuplicateUserException(errorMessage));

        // When & Then (실행 및 검증): 400 Bad Request와 서비스가 던진 메시지를 확인합니다.
        performRegisterPost(validRegisterRequest)
            .andExpect(status().isBadRequest()) // HTTP 상태 코드가 400인지 확인
            .andExpect(jsonPath("$.message").value(errorMessage)) // 에러 메시지가 일치하는지 확인
            .andDo(print());
    }
    
    // =================================================================================
    // 2. 로그인 시나리오 (POST /api/auth/login)
    // =================================================================================

    @Test
    @DisplayName("로그인_성공: 유효한 요청으로 200 OK와 Access/Refresh 토큰을 반환한다")
    void login_success() throws Exception {
        // Given (준비): Service가 성공 응답을 반환하도록 Mocking
        given(authService.login(any(UserLoginRequest.class)))
            .willReturn(successLoginResponse);
        
        // When & Then (실행 및 검증): 200 OK 상태 코드와 응답 필드 검증
        performLoginPost(validLoginRequest)
            .andExpect(status().isOk()) 
            .andExpect(jsonPath("$.accessToken").value(successLoginResponse.getAccessToken())) 
            .andExpect(jsonPath("$.refreshToken").value(successLoginResponse.getRefreshToken()))
            .andExpect(jsonPath("$.userId").value(successLoginResponse.getUserId()))
            .andDo(print());
    }

    @Test
    @DisplayName("로그인_실패: DTO 유효성 검사 실패(식별자 공백) 시 400 BAD REQUEST를 반환한다")
    void login_fail_validation_blank_identifier() throws Exception {
        // Given (준비): 식별자 필드를 공백으로 설정하여 유효성 검사 실패 유도
        UserLoginRequest invalidRequest = UserLoginRequest.builder()
            .emailOrIdOrPhone(" ") 
            .password(TEST_PASSWORD)
            .build();
        
        // When & Then (실행 및 검증): 400 Bad Request와 에러 메시지 존재 여부 검증
        performLoginPost(invalidRequest)
            .andExpect(status().isBadRequest()) 
            .andExpect(jsonPath("$.message").exists()) 
            .andDo(print());
    }

    @Test
    @DisplayName("로그인 실패: 서비스에서 인증 실패 예외 발생 시 401 UNAUTHORIZED를 반환해야 한다")
    void login_fail_service_exception_invalid_credential() throws Exception {
        // (약속): 가짜 AuthService에게 "login"이 호출되면 InvalidCredentialException 에러를 던지도록 약속합니다.
        String errorMessage = "유효하지 않은 식별자 또는 비밀번호입니다.";

        given(authService.login(any(UserLoginRequest.class)))
            .willThrow(new InvalidCredentialException(errorMessage)); 

        // When & Then (실행 및 검증): 401 Unauthorized와 서비스가 던진 메시지를 확인합니다.
        performLoginPost(validLoginRequest)
            .andExpect(status().isUnauthorized()) // HTTP 상태 코드가 401 Unauthorized인지 확인
            .andExpect(jsonPath("$.message").value(errorMessage)) // 에러 메시지가 일치하는지 확인
            .andDo(print());
    }

    // =================================================================================
    // 3. ID 찾기 시나리오 (POST /api/auth/find-id)
    // =================================================================================
    
    @Test
    @DisplayName("ID 찾기_성공: 유효한 정보로 요청 시 200 OK와 마스킹된 ID를 반환해야 한다")
    void findId_Success() throws Exception {
        // given (준비)
        UserIdFindResponse mockResponse = UserIdFindResponse.builder()
            .maskedUserId(MASKED_USER_ID)
            .message("성공적으로 회원님의 ID를 찾았습니다. 마스킹된 ID를 확인해주세요.")
            .build();
        
        // Mock 설정: Service가 성공 응답 DTO를 반환하도록 설정
        given(authService.findIdByPhoneAndUsername(any(UserIdFindRequest.class))).willReturn(mockResponse);

        // when & then (실행 및 검증)
        performFindIdPost(validFindIdRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.maskedUserId").value(MASKED_USER_ID))
            .andExpect(jsonPath("$.message").exists())
            .andDo(print());
    }

    @Test
    @DisplayName("ID 찾기_실패: 정보 불일치 시 Service가 AuthenticationException을 던지고 401 Unauthorized를 반환해야 한다")
    void findId_Fail_AuthenticationException() throws Exception {
        // given (준비)
        String errorMessage = "입력 정보와 일치하는 계정이 없습니다.";
        
        // Mock 설정: Service가 AuthenticationException을 던지도록 설정
        given(authService.findIdByPhoneAndUsername(any(UserIdFindRequest.class)))
            .willThrow(new AuthenticationException(errorMessage)); 

        // when & then (실행 및 검증)
        performFindIdPost(validFindIdRequest)
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(errorMessage))
            .andDo(print());
    }
}