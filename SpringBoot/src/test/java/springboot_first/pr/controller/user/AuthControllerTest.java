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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import springboot_first.pr.controller.user.AuthController;
import springboot_first.pr.dto.userDTO.request.UserLoginRequest;
import springboot_first.pr.dto.userDTO.request.UserRegisterRequest;
import springboot_first.pr.dto.userDTO.response.UserLoginResponse;
import springboot_first.pr.dto.userDTO.response.UserRegisterResponse;
import springboot_first.pr.service.auth.AuthService;

/**
 * AuthController WebMvc 테스트
 */
// 💡 [수정] WebMvcTest에서 SecurityAutoConfiguration을 제외하여 403 에러 방지
@WebMvcTest(
    controllers = AuthController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class 
)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    // 테스트용 데이터
    private UserRegisterRequest validRegisterRequest;
    private UserRegisterResponse successRegisterResponse;
    private UserLoginRequest validLoginRequest;
    private UserLoginResponse successLoginResponse;


    @BeforeEach
    void setUp() {
        // [회원가입] 유효한 요청 DTO
        validRegisterRequest = UserRegisterRequest.builder()
                .userId("testId")
                .email("test@email.com")
                .username("Tester")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .build();

        // [회원가입] 성공 응답 DTO
        // UserRegisterResponse DTO의 필드 순서가 변경되었다면 생성자 호출 순서 확인 필요
        successRegisterResponse = new UserRegisterResponse(1L, "testId", "Tester"); 
        
        // [로그인] 유효한 요청 DTO
        validLoginRequest = new UserLoginRequest("testId", "password123");

        // [로그인] 성공 응답 DTO (토큰은 임의로 설정)
        successLoginResponse = UserLoginResponse.builder()
                .id(1L)
                .userId("testId")
                .username("Tester")
                .accessToken("mock-access-token-1234")
                .refreshToken("mock-refresh-token-5678")
                .build();
    }
    
    // =================================================================================
    // 💡 헬퍼 메서드 1: 회원가입 POST 요청 시뮬레이션
    // =================================================================================

    /**
     * /api/auth/register 경로로 POST 요청을 보내는 공통 로직
     */
    private ResultActions performRegisterPost(UserRegisterRequest requestDto) throws Exception {
        return mockMvc.perform(post("/api/auth/register") 
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)));
    }

    // =================================================================================
    // 💡 헬퍼 메서드 2: 로그인 POST 요청 시뮬레이션
    // =================================================================================

    /**
     * /api/auth/login 경로로 POST 요청을 보내는 공통 로직
     */
    private ResultActions performLoginPost(UserLoginRequest requestDto) throws Exception {
        return mockMvc.perform(post("/api/auth/login") 
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)));
    }

    // =================================================================================
    // 💡 헬퍼 메서드 3: 회원가입 성공 응답 검증
    // =================================================================================

    private void assertRegisterSuccessResponse(ResultActions actions) throws Exception {
        // 201 Created 상태 코드 검증
        actions.andExpect(status().isCreated()) 
                .andExpect(jsonPath("$.id").value(successRegisterResponse.getId())) 
                .andExpect(jsonPath("$.userId").value(successRegisterResponse.getUserId()))
                .andExpect(jsonPath("$.username").value(successRegisterResponse.getUsername()));
    }

    // =================================================================================
    // 1. 회원가입 시나리오
    // =================================================================================

    @Test
    @DisplayName("회원가입_성공: 유효한 요청으로 201 CREATED를 반환한다")
    void register_success() throws Exception {
        // Given (준비)
        given(authService.register(any(UserRegisterRequest.class)))
                .willReturn(successRegisterResponse);

        // When & Then (실행 및 검증)
        assertRegisterSuccessResponse(performRegisterPost(validRegisterRequest).andDo(print())); 
    }

    @Test
    @DisplayName("회원가입_실패: DTO 유효성 검사 실패(ID 공백) 시 400 BAD REQUEST를 반환한다")
    void register_fail_validation_blank_id() throws Exception {
        // Given (준비)
        UserRegisterRequest invalidRequest = validRegisterRequest.withUserId(""); 

        // When & Then (실행 및 검증)
        performRegisterPost(invalidRequest)
                .andExpect(status().isBadRequest()) // 400 Bad Request 상태 코드 검증
                .andExpect(jsonPath("$.message").exists()) // 에러 메시지 필드 존재 검증
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입_실패: Service에서 비즈니스 예외(중복 ID) 발생 시 400 BAD REQUEST를 반환한다")
    void register_fail_service_exception_duplicate() throws Exception {
        // Given (준비)
        given(authService.register(any(UserRegisterRequest.class)))
                .willThrow(new RuntimeException("이미 존재하는 사용자 ID입니다."));

        // When & Then (실행 및 검증)
        performRegisterPost(validRegisterRequest)
                .andExpect(status().isBadRequest()) // 400 Bad Request 반환 검증
                .andExpect(jsonPath("$.message").value("이미 존재하는 사용자 ID입니다.")) // 에러 메시지 내용 검증
                .andDo(print());
    }
    
    // =================================================================================
    // 2. 로그인 시나리오 
    // =================================================================================

    // @Test
    // @DisplayName("로그인_성공: 유효한 요청으로 200 OK와 토큰을 반환한다")
    // void login_success() throws Exception {
    //     // Given (준비)
    //     given(authService.login(any(UserLoginRequest.class)))
    //             .willReturn(successLoginResponse);
        
    //     // When & Then (실행 및 검증)
    //     performLoginPost(validLoginRequest)
    //             .andExpect(status().isOk()) // 200 OK 상태 코드 검증
    //             .andExpect(jsonPath("$.accessToken").value(successLoginResponse.getAccessToken())) 
    //             .andExpect(jsonPath("$.refreshToken").value(successLoginResponse.getRefreshToken()))
    //             .andExpect(jsonPath("$.userId").value(successLoginResponse.getUserId()))
    //             .andDo(print());
    // }

    // @Test
    // @DisplayName("로그인_실패: DTO 유효성 검사 실패(ID 공백) 시 400 BAD REQUEST를 반환한다")
    // void login_fail_validation_blank_id() throws Exception {
    //     // Given (준비)
    //     UserLoginRequest invalidRequest = new UserLoginRequest("", "password123"); 
        
    //     // When & Then (실행 및 검증)
    //     performLoginPost(invalidRequest)
    //             .andExpect(status().isBadRequest()) // 400 Bad Request 상태 코드 검증
    //             .andExpect(jsonPath("$.message").exists()) 
    //             .andDo(print());
    // }

    // @Test
    // @DisplayName("로그인_실패: Service에서 비즈니스 예외(유효하지 않은 인증 정보) 발생 시 401 UNAUTHORIZED를 반환한다")
    // void login_fail_service_exception_invalid_credential() throws Exception {
    //     // Given (준비)
    //     given(authService.login(any(UserLoginRequest.class)))
    //             .willThrow(new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));
        
    //     // When & Then (실행 및 검증)
    //     // 💡 [참고] Service에서 IllegalArgumentException을 던지면 GlobalExceptionHandler가 401로 변환해야 함
    //     performLoginPost(validLoginRequest)
    //             .andExpect(status().isUnauthorized()) 
    //             .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 일치하지 않습니다."))
    //             .andDo(print());
    // }
}