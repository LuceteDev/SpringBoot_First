package springboot_first.pr.controller.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import springboot_first.pr.dto.userDTO.request.UserRegisterRequest;
import springboot_first.pr.dto.userDTO.response.UserRegisterResponse;
import springboot_first.pr.service.auth.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController // 1️⃣컨트롤러 선언 ✅ 회원가입, 로그인, 토큰 갱신, 비밀번호 찾기 구현
@RequiredArgsConstructor  // 2️⃣ 👍 생성자 자동 생성 -> @Autowired 대신 많이 사용한다고 함
@RequestMapping("/api/auth") // 3️⃣ 기본 경로 설정
public class AuthController {

  // 4️⃣ 서비스 주입
  private final AuthService authService;


  // 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ 영역 분리 〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️〰️ //
  // ✅ POST - 회원 가입 //
  @PostMapping("/register")
  public ResponseEntity<UserRegisterResponse> register(@RequestBody UserRegisterRequest request) {

    // 1️⃣ 서비스에 위임하여 회원가입 및 DB 저장
      UserRegisterResponse responseDto = authService.register(request);

    // 2️⃣ 결과 응답
      return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
  }
  
}
