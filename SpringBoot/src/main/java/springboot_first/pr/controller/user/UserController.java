package springboot_first.pr.controller.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot_first.pr.dto.response.CommonResponse;
import springboot_first.pr.dto.userDTO.request.UserPasswordChangeRequest;
import springboot_first.pr.dto.userDTO.request.UserWithdrawalRequest;
import springboot_first.pr.dto.userDTO.response.UserWithdrawalResponse;
import springboot_first.pr.service.user.UserService;

@Slf4j
@RestController // 1️⃣컨트롤러 선언 
@RequiredArgsConstructor  // 2️⃣ 👍 생성자 자동 생성 -> @Autowired 대신 많이 사용한다고 함
@RequestMapping("/api/user") // 3️⃣ 기본 경로 설정
public class UserController { // ✅ 로그인 후 회원 로직

  // 4️⃣ 서비스 주입
  private final UserService userService;

  // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ ✅ 비밀번호 변경 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

  @PatchMapping("/password/change")
  public ResponseEntity<CommonResponse<?>> changePassword(
    @AuthenticationPrincipal String authenticatedUserId, // JWT/세션 기반 인증에서 ID를 자동으로 가져옴
    @Valid @RequestBody UserPasswordChangeRequest requestDto) {
    
    log.info("비밀번호 변경 요청 접수 - 인증된 ID: {}", authenticatedUserId);
    
    // 1️⃣ 서비스에 위임하여 DB에 비밀번호 변경
    CommonResponse response = userService.changePassword(authenticatedUserId, requestDto);
    
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ ✅ 회원 탈퇴 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //
  @DeleteMapping("/withdrawal") // DELETE HTTP 메서드 사용
  public ResponseEntity<UserWithdrawalResponse> withdraw(
          // Access Token에서 추출된 userId를 @AuthenticationPrincipal로 받습니다.
          @AuthenticationPrincipal String userId, 
          @Valid @RequestBody UserWithdrawalRequest requestDto
  ) {
      log.info("회원 탈퇴 API 요청 수신. Target UserId: {}", userId);
      
      UserWithdrawalResponse response = userService.withdraw(userId, requestDto);
      return ResponseEntity.ok(response);
  }



}
