package springboot_first.pr.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springboot_first.pr.model.User;
import springboot_first.pr.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")  // React 개발 서버 주소를 추가해야 CORS 설정안했다고 차단된거  해결됨
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 회원가입 : mode/User.java에서 엔티티를 받아오고? -> 스프링부트의 service/AuthService로 위임
    // HTTP 요청(JSON)을 받음 → @RequestBody User user로 매핑
    // 비즈니스 로직은 하지 않고 Service에 위임
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            // System.out.println("회원가입 요청 데이터: " + user);
            System.out.println("AuthController에서 받은 이메일: " + user.getEmail());

            authService.register(user); // 서비스에게 위임
            return ResponseEntity.status(HttpStatus.CREATED).body(user.getUsername());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            User loggedUser = authService.login(user.getEmail(), user.getPassword());
            // return ResponseEntity.ok(loggedUser.getUsername());
            //👉 username만 리턴하면 React에서 sessionStorage에 유저 정보 저장하기가 힘듭니다.
            // 최소한 User 객체 전체(비밀번호 제외)를 JSON으로 리턴하는 게 좋아요.
            return ResponseEntity.ok(loggedUser);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
