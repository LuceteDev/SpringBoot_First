// package springboot_first.pr.controller.user;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.eq; // 💡 eq() 매처 임포트
// import static org.mockito.BDDMockito.given;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.ResultActions;

// import com.fasterxml.jackson.databind.ObjectMapper;

// import springboot_first.pr.dto.userDTO.request.UserPasswordChangeRequest;
// import springboot_first.pr.dto.userDTO.response.UserPasswordChangeResponse;
// import springboot_first.pr.exception.AuthenticationException;
// import springboot_first.pr.service.user.UserService;
// import springboot_first.pr.security.TokenProvider; 

// @WebMvcTest(
//   controllers = UserController.class,
//   // Spring Security 자동 구성을 제외합니다. (403 에러 방지)
//   excludeAutoConfiguration = SecurityAutoConfiguration.class 
// ) 
// @AutoConfigureMockMvc(addFilters = false) // Spring Security 필터 비활성화 유지
// @DisplayName("Controller 단위 테스트: UserController - 비밀번호 변경 API")
// class UserControllerTest {

//   @Autowired
//   private MockMvc mockMvc; 
  
//   @Autowired
//   private ObjectMapper objectMapper;

//   @MockBean 
//   private UserService userService;
  
//   @MockBean
//   private TokenProvider tokenProvider; 

//   private UserPasswordChangeRequest validRequest;
//   private UserPasswordChangeResponse successResponse;
//   private final String API_URL = "/api/user/password";
//   private final String AUTH_USER_ID = "authenticatedUser";

//   @BeforeEach
//   void setup() {
//     // DTO의 @Pattern 규칙에 완벽하게 맞춘 테스트 데이터
//     this.validRequest = UserPasswordChangeRequest.builder()
//       .oldPassword("OldP@ss12Ab") 
//       .newPassword("N3wP#ss34Cd") 
//       .build();

//     // DTO의 정적 헬퍼 메서드를 사용하여 성공 응답 생성
//     this.successResponse = UserPasswordChangeResponse.success();
//   }

//   // --------------------------------------------------------------------------------
//   // 1. 비밀번호 변경 성공 테스트 (200 OK)
//   // --------------------------------------------------------------------------------
//   @Test
//   @WithMockUser(username = AUTH_USER_ID, roles = {"USER"}) 
//   @DisplayName("성공_케이스: 유효한 요청 시 200 OK와 성공 응답 본문을 반환해야 한다.")
//   void changePassword_success() throws Exception {
//     // GIVEN (준비)
//     // 💡 Mocking 강화: 인증된 사용자 ID (AUTH_USER_ID)를 eq()로 명시하여 Mocking 충돌 방지
//     given(userService.changePassword(eq(AUTH_USER_ID), any(UserPasswordChangeRequest.class)))
//       .willReturn(successResponse);

//     // WHEN & THEN (실행 및 검증)
//     ResultActions result = mockMvc.perform(
//       patch(API_URL) 
//         .contentType(MediaType.APPLICATION_JSON) 
//         .content(objectMapper.writeValueAsString(validRequest))
//     ).andDo(print()); 

//     result
//       .andExpect(status().isOk()) 
//       .andExpect(jsonPath("$.success").value(true)) 
//       .andExpect(jsonPath("$.message").value("비밀번호가 성공적으로 변경되었습니다.")); 
//   }

//   // --------------------------------------------------------------------------------
//   // 2. 비밀번호 변경 실패 테스트: 인증 실패 (401 Unauthorized)
//   // --------------------------------------------------------------------------------
//   @Test
//   @WithMockUser(username = AUTH_USER_ID, roles = {"USER"})
//   @DisplayName("실패_케이스_인증: 기존 비밀번호 불일치 시 401 Unauthorized가 반환되어야 한다.")
//   void changePassword_fail_unauthorized() throws Exception {
//     // GIVEN (준비)
//     final String errorMessage = "기존 비밀번호가 일치하지 않아 변경에 실패했습니다.";

//     // 💡 Mocking 강화: eq(AUTH_USER_ID)로 Mocking 충돌 방지
//     given(userService.changePassword(eq(AUTH_USER_ID), any(UserPasswordChangeRequest.class)))
//       .willThrow(new AuthenticationException(errorMessage));

//     // WHEN & THEN (실행 및 검증)
//     ResultActions result = mockMvc.perform(
//       patch(API_URL)
//         .contentType(MediaType.APPLICATION_JSON)
//         .content(objectMapper.writeValueAsString(validRequest))
//     ).andDo(print());

//     result
//       .andExpect(status().isUnauthorized()) // 401 Unauthorized를 기대
//       .andExpect(jsonPath("$.message").value(errorMessage)); 
//   }
  
//   // --------------------------------------------------------------------------------
//   // 3. 비밀번호 변경 실패 테스트: DTO 유효성 검사 실패 (400 Bad Request)
//   // --------------------------------------------------------------------------------
//   @Test
//   @WithMockUser(username = AUTH_USER_ID, roles = {"USER"})
//   @DisplayName("실패_케이스_유효성: 새 비밀번호가 유효성 규칙을 위반하면 400 Bad Request가 반환되어야 한다.")
//   void changePassword_fail_validation() throws Exception {
//     // GIVEN (준비)
//     UserPasswordChangeRequest invalidRequest = UserPasswordChangeRequest.builder()
//       .oldPassword("OldP@ss12Ab") 
//       .newPassword("Validbut123") // 특수문자 없음 (규칙 위반)
//       .build();

//     // WHEN & THEN (실행 및 검증)
//     ResultActions result = mockMvc.perform(
//       patch(API_URL)
//         .contentType(MediaType.APPLICATION_JSON)
//         .content(objectMapper.writeValueAsString(invalidRequest))
//     ).andDo(print());

//     result
//       .andExpect(status().isBadRequest()) 
//       .andExpect(jsonPath("$.message").exists()); 
//   }
// }