// package springboot_first.pr.controller.user;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration; // 💡 임포트 추가
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockitoBean;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;

// import springboot_first.pr.dto.userDTO.request.UserPasswordResetRequest;
// import springboot_first.pr.dto.userDTO.response.UserPasswordResetResponse;
// import springboot_first.pr.exception.AuthenticationException;
// import springboot_first.pr.service.user.UserService;
// // import springboot_first.pr.security.TokenProvider; // 💡 더 이상 필요 없으므로 주석 처리하거나 제거

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.BDDMockito.given;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.times;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// // 💡 중요 수정: Spring Security 자동 설정을 제외하여 TokenProvider 의존성 문제를 근본적으로 해결
// @WebMvcTest(
//     controllers = UserController.class,
//     excludeAutoConfiguration = SecurityAutoConfiguration.class // 시큐리티 관련 빈 로딩을 막습니다.
// ) 
// @DisplayName("Controller 단위 테스트: UserController - 비밀번호 재설정 API")
// class UserControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private ObjectMapper objectMapper;

//     // UserService는 UserController의 주입 대상입니다.
//     @MockitoBean
//     private UserService userService; 
    
//     // 💡 TokenProvider Mocking을 제거합니다. 이제 SecurityAutoConfiguration이 로드되지 않아 필요 없습니다.
//     // private TokenProvider tokenProvider; 
    
//     // 테스트용 상수
//     private final String TEST_USER_ID = "testuser123";
//     private final String TEST_PHONE_NUMBER = "010-1234-5678";
//     private final String RESET_NEW_PASSWORD = "resetpass!@#";
//     private final String RESET_URL = "/api/v1/users/reset-password"; 

//     private UserPasswordResetRequest createValidResetRequest() {
//         return UserPasswordResetRequest.builder()
//                 .userId(TEST_USER_ID)
//                 .phoneNumber(TEST_PHONE_NUMBER)
//                 .newPassword(RESET_NEW_PASSWORD)
//                 .build();
//     }
    
//     // =================================================================================
//     // 1. 비밀번호 재설정 성공 테스트 (HTTP 200 OK)
//     // =================================================================================
//     @Test
//     @DisplayName("비밀번호재설정_성공: 유효한 요청 시 200 OK와 성공 응답 JSON을 반환해야 한다")
//     void resetPassword_success() throws Exception {
//         // given (준비)
//         UserPasswordResetRequest requestDto = createValidResetRequest();
        
//         // 서비스가 성공적으로 비밀번호를 재설정하고 응답 DTO를 반환하도록 Mocking
//         UserPasswordResetResponse successResponse = UserPasswordResetResponse.success(TEST_USER_ID);
//         given(userService.resetPassword(any(UserPasswordResetRequest.class)))
//             .willReturn(successResponse);

//         // when & then (실행 및 검증)
//         mockMvc.perform(post(RESET_URL)
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(requestDto)))
                
//                 // 1. HTTP 상태 코드 검증: 200 OK
//                 .andExpect(status().isOk()) 
                
//                 // 2. 응답 JSON 본문 검증
//                 .andExpect(jsonPath("$.success").value(true))
//                 .andExpect(jsonPath("$.userId").value(TEST_USER_ID));

//         // 3. 서비스 호출 검증
//         verify(userService, times(1)).resetPassword(any(UserPasswordResetRequest.class));
//     }


//     // =================================================================================
//     // 2. 비밀번호 재설정 실패 테스트 (HTTP 401 Unauthorized)
//     // =================================================================================
//     @Test
//     @DisplayName("비밀번호재설정_실패: 사용자 정보 불일치 시 401 Unauthorized와 오류 메시지를 반환해야 한다")
//     void resetPassword_fail_authentication_exception() throws Exception {
//         // given (준비)
//         UserPasswordResetRequest requestDto = createValidResetRequest();
//         final String errorMessage = "입력 정보와 일치하는 계정이 없습니다.";
        
//         // 서비스 호출 시 AuthenticationException이 발생하도록 Mocking
//         given(userService.resetPassword(any(UserPasswordResetRequest.class)))
//             .willThrow(new AuthenticationException(errorMessage));

//         // when & then (실행 및 검증)
//         mockMvc.perform(post(RESET_URL)
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(requestDto)))
                
//                 // 1. HTTP 상태 코드 검증: 401 Unauthorized
//                 .andExpect(status().isUnauthorized()) 
                
//                 // 2. 응답 JSON 본문 검증: message 필드 확인
//                 .andExpect(jsonPath("$.message").value(errorMessage));

//         // 3. 서비스 호출 검증
//         verify(userService, times(1)).resetPassword(any(UserPasswordResetRequest.class));
//     }
// }