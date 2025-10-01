package springboot_first.pr.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springboot_first.pr.model.User;
import springboot_first.pr.service.AuthService;

import springboot_first.pr.dto.UserSignUpRequestDTO; // 💡 요청 DTO
import springboot_first.pr.dto.UserLoginRequestDTO; // 💡 로그인 요청 DTO
import springboot_first.pr.dto.UserResponseDTO; // 💡 응답 DTO


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")  // React 개발 서버 주소를 추가해야 CORS 설정안했다고 차단된거  해결됨
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 비즈니스 로직은 하지 않고 Service에 위임
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserSignUpRequestDTO requestDTO) {
        // 💡 try-catch 블록 제거!

        System.out.println("AuthController에서 받은 이메일: " + requestDTO);
        
        // Service 호출 (예외 발생 시 자동으로 GlobalExceptionHandler로 전파됨)
        User newUser = authService.register(requestDTO); 
        
        // 성공 시 응답 DTO로 변환
        UserResponseDTO responseDTO = UserResponseDTO.fromEntity(newUser);
        
        // 성공 시 201 Created 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        
        // 💡 catch (RuntimeException e) { ... } 블록을 완전히 삭제합니다.
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequestDTO loginDTO) {
        // 💡 try-catch 블록 제거!
        
        // Service 호출 (예외 발생 시 자동으로 GlobalExceptionHandler로 전파됨)
        User loggedUser = authService.login(loginDTO); 
        
        // 성공 시 응답 DTO로 변환
        UserResponseDTO responseDTO = UserResponseDTO.fromEntity(loggedUser);
        
        // 성공 시 200 OK 반환
        return ResponseEntity.ok(responseDTO);
        
    }
}
