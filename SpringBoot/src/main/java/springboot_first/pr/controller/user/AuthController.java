// package springboot_first.pr.controller.user;

// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import springboot_first.pr.dto.userDTO.request.UserIdFindRequest;
// import springboot_first.pr.dto.userDTO.request.UserLoginRequest;
// import springboot_first.pr.dto.userDTO.request.UserPasswordResetRequest;
// import springboot_first.pr.dto.userDTO.request.UserRegisterRequest;
// import springboot_first.pr.dto.userDTO.response.UserIdFindResponse;
// import springboot_first.pr.dto.userDTO.response.UserLoginResponse;
// import springboot_first.pr.dto.userDTO.response.UserPasswordResetResponse;
// import springboot_first.pr.dto.userDTO.response.UserRegisterResponse;
// import springboot_first.pr.service.auth.AuthService;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.web.bind.annotation.PatchMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;


// @Slf4j
// @RestController // 1️⃣컨트롤러 선언 ✅ 회원가입, 로그인, 토큰 갱신, 비밀번호 찾기 구현
// @RequiredArgsConstructor  // 2️⃣ 👍 생성자 자동 생성 -> @Autowired 대신 많이 사용한다고 함
// @RequestMapping("/api/auth") // 3️⃣ 기본 경로 설정
// public class AuthController {

//   // 4️⃣ 서비스 주입
//   private final AuthService authService;


//   // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ ✅ 로그인 전 로직들 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //


//   // ✅ POST - 회원 가입 //
//   @PostMapping("/register")
//   public ResponseEntity<UserRegisterResponse> register(@Valid @RequestBody UserRegisterRequest request) {
//       log.info("POST /api/auth/register 호출됨"); // 💡 [로깅] 요청 진입 확인

//       // ✅ [핵심] JSON -> DTO 변환 직후, DTO 객체의 내부 상태를 출력합니다.
//       // ⚠️ 얘 출력할거면 요청 DTO에 @ToString 어노테이션 추가해야 함‼️
//       log.info("변환된 UserRegisterRequest DTO 내부 상태: {}", request); 
//       // 1️⃣ 서비스에 위임하여 회원가입 및 DB 저장
//       UserRegisterResponse responseDto = authService.register(request);

//       // 2️⃣ 결과 응답
//       log.info("회원가입 응답 성공: Status 201 Created"); // 💡 [로깅] 응답 직전
//       return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
//       // Spring Boot (Jackson 라이브러리)가 ResponseEntity에 담긴 Response DTO 객체를 보고 응답 JSON 문자열로 자동으로 변환

//       // ⚠️ 테스트 코드에 201 Created 상태 코드로 응답
//       // return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
//   }
  
//   // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 영역 분리 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

//   // ✅ POST - 로그인 //
//   @PostMapping("/login")
//   public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
//       log.info("POST /api/auth/login 호출됨"); // 💡 [로깅] 요청 진입 확인

//       // ✅ [핵심] JSON -> DTO 변환 직후, DTO 객체의 내부 상태를 출력합니다.
//       // ⚠️ 얘 출력할거면 요청 DTO에 @ToString 어노테이션 추가해야 함‼️
//       log.info("변환된 UserLoginRequest DTO 내부 상태: {}", request); 
//       // 1️⃣ 서비스에 위임하여 회원가입 및 DB 저장
//       UserLoginResponse responseDto = authService.login(request);

//       // 2️⃣ 결과 응답
//       log.info("로그인 응답 성공: Status 200 OK"); // 💡 [로깅] 응답 직전
//       return ResponseEntity.status(HttpStatus.OK).body(responseDto);
//       // Spring Boot (Jackson 라이브러리)가 ResponseEntity에 담긴 Response DTO 객체를 보고 응답 JSON 문자열로 자동으로 변환
//       // ⚠️ 테스트 코드에 201 Created 상태 코드로 응답
//       // return new ResponseEntity<>(responseDto, HttpStatus.OK);
//   }  


//   // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 영역 분리 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

//   // ✅ GET -> POST로 변경 - 계정 찾기 //
//   @PostMapping("/find-id")
//   public ResponseEntity<UserIdFindResponse> IdFind(@Valid @RequestBody UserIdFindRequest request) {
//       log.info("POST /api/auth/IdFind 호출됨"); // 💡 [로깅] 요청 진입 확인

//       // ✅ [핵심] JSON -> DTO 변환 직후, DTO 객체의 내부 상태를 출력합니다.
//       // ⚠️ 얘 출력할거면 요청 DTO에 @ToString 어노테이션 추가해야 함‼️
//       log.info("변환된 UserIdFindRequest DTO 내부 상태: {}", request); 
//       // 1️⃣ 서비스에 위임하여 회원가입 및 DB 저장
//       UserIdFindResponse responseDto = authService.findIdByPhoneAndUsername(request);

    
//       // 2️⃣ 결과 응답
//       log.info("계정 찾기 응답 성공: Status 200 OK"); // 💡 [로깅] 응답 직전
//       // return ResponseEntity.status(HttpStatus.OK).body(responseDto);
//       // Spring Boot (Jackson 라이브러리)가 ResponseEntity에 담긴 Response DTO 객체를 보고 응답 JSON 문자열로 자동으로 변환
//       // ⚠️ 테스트 코드에 201 Created 상태 코드로 응답
//       return new ResponseEntity<>(responseDto, HttpStatus.OK);
//   }


//   // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 영역 분리 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //


//   @PatchMapping("/password/reset")
//   public ResponseEntity<UserPasswordResetResponse> resetPassword(
//     @Valid @RequestBody UserPasswordResetRequest requestDto) {
    
//     log.info("비밀번호 초기화/재설정 요청 접수(getUserId) - 인증된 ID: {}", requestDto.getUserId());
//     log.info("비밀번호 초기화/재설정 요청 접수(toString) - 인증된 ID: {}", requestDto.toString());
    
//     // 1️⃣ 서비스에 위임하여 DB에 비밀번호 변경
//     UserPasswordResetResponse response = authService.resetPassword(requestDto);
    
//     return ResponseEntity.status(HttpStatus.OK).body(response);
//   }


// 	// 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ ✅ 로그아웃 로직 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //


// 	// ✅ POST - 로그아웃 //
// 	@PostMapping("/logout")
// 	// @AuthenticationPrincipal을 사용하려면 Spring Security 설정과 JWT Filter가 선행되어야 합니다.
// 	public ResponseEntity<String> logout(@AuthenticationPrincipal String userId) {
		
// 		// JWT Filter에서 인증된 사용자 ID가 없으면 이 메서드에 도달하지 않지만, 방어적인 코드를 유지합니다.
// 		if (userId == null) {
// 			log.warn("로그아웃 실패: 인증 주체가 null입니다.");
// 			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
// 		}

// 		authService.logout(userId);

// 		log.info("로그아웃 성공. UserId: {}", userId);
// 		return ResponseEntity.ok("로그아웃 성공. 클라이언트 측 토큰을 제거하십시오.");
// 	}
// }


package springboot_first.pr.controller.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// DTOs
import springboot_first.pr.dto.authDTO.response.TokenRefreshResponse;
import springboot_first.pr.dto.userDTO.request.UserIdFindRequest;
import springboot_first.pr.dto.userDTO.request.UserLoginRequest;
import springboot_first.pr.dto.userDTO.request.UserPasswordResetRequest;
import springboot_first.pr.dto.userDTO.request.UserRegisterRequest;
import springboot_first.pr.dto.userDTO.response.UserIdFindResponse;
import springboot_first.pr.dto.userDTO.response.UserLoginResponse;
import springboot_first.pr.dto.userDTO.response.UserPasswordResetResponse;
import springboot_first.pr.dto.userDTO.response.UserRegisterResponse;

import springboot_first.pr.service.auth.AuthService;

// Spring Web & Security
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


/**
 * 회원가입, 로그인, 계정 찾기, 토큰 갱신 등 인증 관련 요청을 처리하는 컨트롤러.
 * 기본 경로: /api/auth
 */
@Slf4j
@RestController
@RequiredArgsConstructor 
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;


	// 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ ✅ 비인증 사용자 로직 (Spring Security 미적용) 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

	/**
	 * POST /api/auth/register : 회원 가입
	 * @param request 유효성 검사가 적용된 회원가입 요청 DTO
	 * @return HTTP 201 Created와 응답 DTO
	 */
	@PostMapping("/register")
	public ResponseEntity<UserRegisterResponse> register(@Valid @RequestBody UserRegisterRequest request) {
		log.info("POST /api/auth/register 호출됨"); 
		
		UserRegisterResponse responseDto = authService.register(request);

		log.info("회원가입 응답 성공: Status 201 Created"); 
		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}
	

	/**
	 * POST /api/auth/login : 로그인 및 Access Token/Refresh Token 발급
	 * @param request 유효성 검사가 적용된 로그인 요청 DTO
	 * @return HTTP 200 OK와 토큰 포함 응답 DTO
	 */
	@PostMapping("/login")
	public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
		log.info("POST /api/auth/login 호출됨"); 
		
		UserLoginResponse responseDto = authService.login(request);

		log.info("로그인 응답 성공: Status 200 OK, UserId: {}", responseDto.getUserId()); 
		return ResponseEntity.status(HttpStatus.OK).body(responseDto);
	} 	


	/**
	 * POST /api/auth/find-id : 사용자 ID 찾기 (휴대폰 번호와 본명으로)
	 * @param request 유효성 검사가 적용된 ID 찾기 요청 DTO
	 * @return HTTP 200 OK와 마스킹된 ID 포함 응답 DTO
	 */
	@PostMapping("/find-id")
	public ResponseEntity<UserIdFindResponse> IdFind(@Valid @RequestBody UserIdFindRequest request) {
		log.info("POST /api/auth/find-id 호출됨"); 
		
		UserIdFindResponse responseDto = authService.findIdByPhoneAndUsername(request);

		log.info("계정 찾기 응답 성공: Status 200 OK"); 
		return ResponseEntity.status(HttpStatus.OK).body(responseDto);
	}


	/**
	 * PATCH /api/auth/password/reset : 비밀번호 재설정/초기화
	 * @param requestDto 유효성 검사가 적용된 비밀번호 재설정 요청 DTO (ID, 폰번호, 새 비밀번호 포함)
	 * @return HTTP 200 OK와 성공 메시지 포함 응답 DTO
	 */
	@PatchMapping("/password/reset")
	public ResponseEntity<UserPasswordResetResponse> resetPassword(
		@Valid @RequestBody UserPasswordResetRequest requestDto) {
		
		log.info("PATCH /api/auth/password/reset 요청 접수. UserId: {}", requestDto.getUserId());
		
		UserPasswordResetResponse response = authService.resetPassword(requestDto);
		
		log.info("비밀번호 재설정 성공: UserId: {}", requestDto.getUserId());
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}


	// 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ ✅ 인증된 사용자 로직 (Spring Security 적용) 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //


	/**
	 * POST /api/auth/refresh : Access Token 재발급
	 * 이 엔드포인트는 Refresh Token으로 인증을 수행하며,
	 * @AuthenticationPrincipal을 통해 Refresh Token의 payload(userId)를 추출합니다.
	 * @param userId Refresh Token의 payload에서 추출된 사용자 ID
	 * @param refreshTokenHeader 요청 헤더에서 추출된 Refresh Token (Bearer 접두사 포함)
	 * @return HTTP 200 OK와 새 Access Token 포함 응답 DTO
	 */
	@PostMapping("/refresh")
	public ResponseEntity<TokenRefreshResponse> refreshToken(
		@AuthenticationPrincipal String userId, 
		@RequestHeader("Authorization") String refreshTokenHeader) {
		
		log.info("POST /api/auth/refresh 호출됨. userId: {}", userId);
		
		// "Bearer " 접두사 제거
		String refreshToken = refreshTokenHeader.replace("Bearer ", "");
		
		TokenRefreshResponse response = authService.refreshToken(userId, refreshToken);
		
		log.info("토큰 재발급 성공: userId: {}", userId);
		return ResponseEntity.ok(response);
	}


	/**
	 * POST /api/auth/logout : 로그아웃 처리
	 * Access Token으로 인증을 수행하며, @AuthenticationPrincipal로 userId를 추출하여
	 * DB에 저장된 Refresh Token을 무효화합니다.
	 * @param userId Access Token의 payload에서 추출된 사용자 ID
	 * @return HTTP 200 OK와 성공 메시지
	 */
	@PostMapping("/logout")
	public ResponseEntity<String> logout(@AuthenticationPrincipal String userId) {
		
		if (userId == null) {
			log.warn("로그아웃 실패: 인증 주체가 null입니다.");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
		}

		authService.logout(userId);

		log.info("로그아웃 성공. UserId: {}", userId);
		return ResponseEntity.ok("로그아웃 성공. 클라이언트 측 Access Token을 제거하십시오.");
	}
}