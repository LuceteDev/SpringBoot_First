package springboot_first.pr.controller.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import springboot_first.pr.dto.userDTO.request.UserIdFindRequest;
import springboot_first.pr.dto.userDTO.request.UserLoginRequest;
import springboot_first.pr.dto.userDTO.request.UserPasswordResetRequest;
import springboot_first.pr.dto.userDTO.request.UserRegisterRequest;
import springboot_first.pr.dto.userDTO.response.UserIdFindResponse;
import springboot_first.pr.dto.userDTO.response.UserLoginResponse;
import springboot_first.pr.dto.userDTO.response.UserPasswordResetResponse;
import springboot_first.pr.dto.userDTO.response.UserRegisterResponse;
import springboot_first.pr.exception.AuthenticationException;
import springboot_first.pr.exception.DuplicateUserException;
import springboot_first.pr.exception.InvalidCredentialException;
import springboot_first.pr.handler.GlobalExceptionHandler;
import springboot_first.pr.security.TokenProvider;
import springboot_first.pr.service.auth.AuthService;



@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("AuthController 테스트")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private TokenProvider tokenProvider;


    // ==================== 테스트 데이터 ====================

    private final String TEST_USER_ID = "testuser123";
    private final String TEST_PASSWORD = "Password@123";
    private final String TEST_USERNAME = "테스트사용자";
    private final String TEST_PHONE = "010-1234-5678";
    private final String TEST_EMAIL = "testuser123@email.com";

    @BeforeEach
    void setUp() {
        // setUp에서는 테스트 데이터 초기화만 진행
        // 각 테스트에서 필요한 Mock 설정을 별도로 수행
    }

    // ==================== 💡 헬퍼 메서드 ====================

    private ResultActions performPostRequest(String url, Object requestDto) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print());
    }

    private ResultActions performPatchRequest(String url, Object requestDto) throws Exception {
        return mockMvc.perform(patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print());
    }

    // ==================== 📝 회원가입 테스트 ====================

    @Test
    @DisplayName("✅ 회원가입 성공 - 201 Created")
    void register_success() throws Exception {
        // given: 유효한 회원가입 요청 DTO
        UserRegisterRequest request = UserRegisterRequest.builder()
                .userId(TEST_USER_ID)
                .password(TEST_PASSWORD)
                .username(TEST_USERNAME)
                .phoneNumber(TEST_PHONE)
                .build();

        UserRegisterResponse response = UserRegisterResponse.builder()
                .userId(TEST_USER_ID)
                .username(TEST_USERNAME)
                .email(TEST_EMAIL)
                .build();

        given(authService.register(any(UserRegisterRequest.class)))
                .willReturn(response);

        // when & then
        performPostRequest("/api/auth/register", request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL));
    }

    @Test
    @DisplayName("❌ 회원가입 실패 - 중복된 사용자 (400)")
    void register_fail_duplicate() throws Exception {
        // given
        UserRegisterRequest request = UserRegisterRequest.builder()
                .userId(TEST_USER_ID)
                .password(TEST_PASSWORD)
                .username(TEST_USERNAME)
                .phoneNumber(TEST_PHONE)
                .build();

        given(authService.register(any(UserRegisterRequest.class)))
                .willThrow(new DuplicateUserException("이미 존재하는 사용자입니다."));

        // when & then
        performPostRequest("/api/auth/register", request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 존재하는 사용자입니다."));
    }

    @Test
    @DisplayName("❌ 회원가입 실패 - userId 유효성 검사 실패 (400)")
    void register_fail_validation_userId() throws Exception {
        // given: userId가 4자 미만 (패턴 위반)
        UserRegisterRequest invalidRequest = UserRegisterRequest.builder()
                .userId("ab")  // 4자 미만
                .password(TEST_PASSWORD)
                .username(TEST_USERNAME)
                .phoneNumber(TEST_PHONE)
                .build();

        // when & then
        performPostRequest("/api/auth/register", invalidRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("❌ 회원가입 실패 - password 유효성 검사 실패 (400)")
    void register_fail_validation_password() throws Exception {
        // given: password가 패턴 미충족 (특수문자 없음)
        UserRegisterRequest invalidRequest = UserRegisterRequest.builder()
                .userId(TEST_USER_ID)
                .password("password123")  // 특수문자 없음
                .username(TEST_USERNAME)
                .phoneNumber(TEST_PHONE)
                .build();

        // when & then
        performPostRequest("/api/auth/register", invalidRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("❌ 회원가입 실패 - phoneNumber 유효성 검사 실패 (400)")
    void register_fail_validation_phone() throws Exception {
        // given: phoneNumber가 패턴 미충족
        UserRegisterRequest invalidRequest = UserRegisterRequest.builder()
                .userId(TEST_USER_ID)
                .password(TEST_PASSWORD)
                .username(TEST_USERNAME)
                .phoneNumber("01012345678")  // 하이픈 없음
                .build();

        // when & then
        performPostRequest("/api/auth/register", invalidRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    // ==================== 🔑 로그인 테스트 ====================

    @Test
    @DisplayName("✅ 로그인 성공 - 200 OK (Mock 토큰 반환)")
    void login_success() throws Exception {
        // given: 로그인 요청 (emailOrIdOrPhone 필드 사용)
        UserLoginRequest request = UserLoginRequest.builder()
                .emailOrIdOrPhone(TEST_USER_ID)
                .password(TEST_PASSWORD)
                .build();

        // 💡 Mock에서는 실제 토큰 대신 더미 토큰 반환
        UserLoginResponse response = UserLoginResponse.builder()
                .userId(TEST_USER_ID)
                .username(TEST_USERNAME)
                .accessToken("mock-access-token-xyz123")
                .refreshToken("mock-refresh-token-abc456")
                .build();

        given(authService.login(any(UserLoginRequest.class)))
                .willReturn(response);

        // when & then
        performPostRequest("/api/auth/login", request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.accessToken").value("mock-access-token-xyz123"))
                .andExpect(jsonPath("$.refreshToken").value("mock-refresh-token-abc456"));
    }

    @Test
    @DisplayName("❌ 로그인 실패 - 잘못된 비밀번호 (401)")
    void login_fail_invalid_password() throws Exception {
        // given
        UserLoginRequest request = UserLoginRequest.builder()
                .emailOrIdOrPhone(TEST_USER_ID)
                .password("WrongPassword@123")
                .build();

        given(authService.login(any(UserLoginRequest.class)))
                .willThrow(new InvalidCredentialException("비밀번호가 일치하지 않습니다."));

        // when & then
        performPostRequest("/api/auth/login", request)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."));
    }

    @Test
    @DisplayName("❌ 로그인 실패 - 존재하지 않는 사용자 (401)")
    void login_fail_user_not_found() throws Exception {
        // given
        UserLoginRequest request = UserLoginRequest.builder()
                .emailOrIdOrPhone("nonexistent")
                .password(TEST_PASSWORD)
                .build();

        given(authService.login(any(UserLoginRequest.class)))
                .willThrow(new InvalidCredentialException("등록되지 않은 사용자입니다."));

        // when & then
        performPostRequest("/api/auth/login", request)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("등록되지 않은 사용자입니다."));
    }

    @Test
    @DisplayName("❌ 로그인 실패 - emailOrIdOrPhone 유효성 검사 실패 (400)")
    void login_fail_validation_identifier() throws Exception {
        // given: emailOrIdOrPhone이 4자 미만
        UserLoginRequest invalidRequest = UserLoginRequest.builder()
                .emailOrIdOrPhone("ab")  // 4자 미만
                .password(TEST_PASSWORD)
                .build();

        // when & then
        performPostRequest("/api/auth/login", invalidRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("❌ 로그인 실패 - password 유효성 검사 실패 (400)")
    void login_fail_validation_password() throws Exception {
        // given: password가 8자 미만
        UserLoginRequest invalidRequest = UserLoginRequest.builder()
                .emailOrIdOrPhone(TEST_USER_ID)
                .password("short")  // 8자 미만
                .build();

        // when & then
        performPostRequest("/api/auth/login", invalidRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    // ==================== 🔍 ID 찾기 테스트 ====================

    @Test
    @DisplayName("✅ ID 찾기 성공 - 200 OK (마스킹된 ID 반환)")
    void findId_success() throws Exception {
        // given
        UserIdFindRequest request = UserIdFindRequest.builder()
                .username(TEST_USERNAME)
                .phoneNumber(TEST_PHONE)
                .build();

        UserIdFindResponse response = UserIdFindResponse.builder()
                .maskedUserId("t*********")  // 첫 글자만 노출
                .message("성공적으로 회원님의 ID를 찾았습니다. 마스킹된 ID를 확인해주세요.")
                .build();

        given(authService.findIdByPhoneAndUsername(any(UserIdFindRequest.class)))
                .willReturn(response);

        // when & then
        performPostRequest("/api/auth/find-id", request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maskedUserId").value("t*********"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("❌ ID 찾기 실패 - 일치하는 사용자 없음 (401)")
    void findId_fail_not_found() throws Exception {
        // given
        UserIdFindRequest request = UserIdFindRequest.builder()
                .username("없는사용자")
                .phoneNumber("010-9999-9999")
                .build();

        given(authService.findIdByPhoneAndUsername(any(UserIdFindRequest.class)))
                .willThrow(new AuthenticationException("일치하는 사용자 정보가 없습니다."));

        // when & then
        performPostRequest("/api/auth/find-id", request)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("일치하는 사용자 정보가 없습니다."));
    }

    @Test
    @DisplayName("❌ ID 찾기 실패 - phoneNumber 유효성 검사 실패 (400)")
    void findId_fail_validation_phone() throws Exception {
        // given: phoneNumber가 패턴 미충족
        UserIdFindRequest invalidRequest = UserIdFindRequest.builder()
                .username(TEST_USERNAME)
                .phoneNumber("01012345678")  // 하이픈 없음
                .build();

        // when & then
        performPostRequest("/api/auth/find-id", invalidRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    // ==================== 🔐 비밀번호 재설정 테스트 ====================

    @Test
    @DisplayName("✅ 비밀번호 재설정 성공 - 200 OK")
    void resetPassword_success() throws Exception {
        // given
        UserPasswordResetRequest request = UserPasswordResetRequest.builder()
                .userId(TEST_USER_ID)
                .phoneNumber(TEST_PHONE)
                .newPassword("NewPassword@456")
                .build();

        UserPasswordResetResponse response = UserPasswordResetResponse.builder()
                .success(true)
                .userId(TEST_USER_ID)
                .message("비밀번호가 성공적으로 재설정되었습니다.")
                .build();

        given(authService.resetPassword(any(UserPasswordResetRequest.class)))
                .willReturn(response);

        // when & then
        performPatchRequest("/api/auth/password/reset", request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("비밀번호가 성공적으로 재설정되었습니다."));
    }

    @Test
    @DisplayName("❌ 비밀번호 재설정 실패 - 존재하지 않는 사용자 (401)")
    void resetPassword_fail_user_not_found() throws Exception {
        // given
        UserPasswordResetRequest request = UserPasswordResetRequest.builder()
                .userId("nonexistent")
                .phoneNumber(TEST_PHONE)
                .newPassword("NewPassword@456")
                .build();

        given(authService.resetPassword(any(UserPasswordResetRequest.class)))
                .willThrow(new AuthenticationException("사용자를 찾을 수 없습니다."));

        // when & then
        performPatchRequest("/api/auth/password/reset", request)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("사용자를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("❌ 비밀번호 재설정 실패 - phoneNumber 유효성 검사 실패 (400)")
    void resetPassword_fail_validation_phone() throws Exception {
        // given: phoneNumber가 패턴 미충족
        UserPasswordResetRequest invalidRequest = UserPasswordResetRequest.builder()
                .userId(TEST_USER_ID)
                .phoneNumber("01012345678")  // 하이픈 없음
                .newPassword("NewPassword@456")
                .build();

        // when & then
        performPatchRequest("/api/auth/password/reset", invalidRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("❌ 비밀번호 재설정 실패 - newPassword 유효성 검사 실패 (400)")
    void resetPassword_fail_validation_password() throws Exception {
        // given: newPassword가 패턴 미충족
        UserPasswordResetRequest invalidRequest = UserPasswordResetRequest.builder()
                .userId(TEST_USER_ID)
                .phoneNumber(TEST_PHONE)
                .newPassword("weak")  // 8자 미만, 특수문자 없음
                .build();

        // when & then
        performPatchRequest("/api/auth/password/reset", invalidRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }
}