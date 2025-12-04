package springboot_first.pr.controller.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Slf4j
@RestController // 1️⃣컨트롤러 선언 ✅ 회원가입, 로그인, 토큰 갱신, 비밀번호 찾기 구현
@RequiredArgsConstructor  // 2️⃣ 👍 생성자 자동 생성 -> @Autowired 대신 많이 사용한다고 함
@RequestMapping("/api/auth") // 3️⃣ 기본 경로 설정
public class AuthController {

  // 4️⃣ 서비스 주입
  private final AuthService authService;


  // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ ✅ 로그인 전 로직들 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //


  // ✅ POST - 회원 가입 //
  @PostMapping("/register")
  public ResponseEntity<UserRegisterResponse> register(@Valid @RequestBody UserRegisterRequest request) {
      log.info("POST /api/auth/register 호출됨"); // 💡 [로깅] 요청 진입 확인

      // ✅ [핵심] JSON -> DTO 변환 직후, DTO 객체의 내부 상태를 출력합니다.
      // ⚠️ 얘 출력할거면 요청 DTO에 @ToString 어노테이션 추가해야 함‼️
      log.info("변환된 UserRegisterRequest DTO 내부 상태: {}", request); 
      // 1️⃣ 서비스에 위임하여 회원가입 및 DB 저장
      UserRegisterResponse responseDto = authService.register(request);

      // 2️⃣ 결과 응답
      log.info("회원가입 응답 성공: Status 201 Created"); // 💡 [로깅] 응답 직전
      return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
      // Spring Boot (Jackson 라이브러리)가 ResponseEntity에 담긴 Response DTO 객체를 보고 응답 JSON 문자열로 자동으로 변환

      // ⚠️ 테스트 코드에 201 Created 상태 코드로 응답
      // return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
  }
  
  // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 영역 분리 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

  // ✅ POST - 로그인 //
  @PostMapping("/login")
  public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
      log.info("POST /api/auth/login 호출됨"); // 💡 [로깅] 요청 진입 확인

      // ✅ [핵심] JSON -> DTO 변환 직후, DTO 객체의 내부 상태를 출력합니다.
      // ⚠️ 얘 출력할거면 요청 DTO에 @ToString 어노테이션 추가해야 함‼️
      log.info("변환된 UserLoginRequest DTO 내부 상태: {}", request); 
      // 1️⃣ 서비스에 위임하여 회원가입 및 DB 저장
      UserLoginResponse responseDto = authService.login(request);

      // 2️⃣ 결과 응답
      log.info("로그인 응답 성공: Status 200 OK"); // 💡 [로깅] 응답 직전
      return ResponseEntity.status(HttpStatus.OK).body(responseDto);
      // Spring Boot (Jackson 라이브러리)가 ResponseEntity에 담긴 Response DTO 객체를 보고 응답 JSON 문자열로 자동으로 변환
      // ⚠️ 테스트 코드에 201 Created 상태 코드로 응답
      // return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }  


  // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 영역 분리 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //

  // ✅ GET -> POST로 변경 - 계정 찾기 //
  @PostMapping("/find-id")
  public ResponseEntity<UserIdFindResponse> IdFind(@Valid @RequestBody UserIdFindRequest request) {
      log.info("POST /api/auth/IdFind 호출됨"); // 💡 [로깅] 요청 진입 확인

      // ✅ [핵심] JSON -> DTO 변환 직후, DTO 객체의 내부 상태를 출력합니다.
      // ⚠️ 얘 출력할거면 요청 DTO에 @ToString 어노테이션 추가해야 함‼️
      log.info("변환된 UserIdFindRequest DTO 내부 상태: {}", request); 
      // 1️⃣ 서비스에 위임하여 회원가입 및 DB 저장
      UserIdFindResponse responseDto = authService.findIdByPhoneAndUsername(request);

    
      // 2️⃣ 결과 응답
      log.info("계정 찾기 응답 성공: Status 200 OK"); // 💡 [로깅] 응답 직전
      // return ResponseEntity.status(HttpStatus.OK).body(responseDto);
      // Spring Boot (Jackson 라이브러리)가 ResponseEntity에 담긴 Response DTO 객체를 보고 응답 JSON 문자열로 자동으로 변환
      // ⚠️ 테스트 코드에 201 Created 상태 코드로 응답
      return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }


  // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 영역 분리 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //


  @PatchMapping("/password/reset")
  public ResponseEntity<UserPasswordResetResponse> resetPassword(
    @Valid @RequestBody UserPasswordResetRequest requestDto) {
    
    log.info("비밀번호 초기화/재설정 요청 접수(getUserId) - 인증된 ID: {}", requestDto.getUserId());
    log.info("비밀번호 초기화/재설정 요청 접수(toString) - 인증된 ID: {}", requestDto.toString());
    
    // 1️⃣ 서비스에 위임하여 DB에 비밀번호 변경
    UserPasswordResetResponse response = authService.resetPassword(requestDto);
    
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
