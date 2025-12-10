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


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Slf4j
@RestController
@RequiredArgsConstructor 
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;


	// 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ ✅ 비인증 사용자 로직 (Spring Security 미적용) 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

	/**
	 * POST /api/auth/register : 1️⃣ 회원 가입
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
	 * POST /api/auth/login : 2️⃣ 로그인 및 Access Token/Refresh Token 발급
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
	 * POST /api/auth/find-id : 3️⃣ 사용자 ID 찾기 (휴대폰 번호와 본명으로)
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
	 * PATCH /api/auth/password/reset : 4️⃣ 비밀번호 재설정/초기화
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
	 * POST /api/auth/logout : 5️⃣ 로그아웃 처리
	 * Access Token으로 인증을 수행하며, @AuthenticationPrincipal로 userId를 추출하여
	 * DB에 저장된 Refresh Token을 무효화
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


	/**
	 * POST /api/auth/refresh : Access Token 재발급
	 * 이 엔드포인트는 Refresh Token으로 인증을 수행하며,
	 * @AuthenticationPrincipal을 통해 Refresh Token의 payload(userId)를 추출
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

}