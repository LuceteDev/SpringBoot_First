package springboot_first.pr.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import springboot_first.pr.exception.DuplicateResourceException;
import springboot_first.pr.exception.DuplicateEmailException;
import springboot_first.pr.exception.AuthenticationException;

import springboot_first.pr.exception.PostValidationException;
import springboot_first.pr.exception.UserNotFoundException;



// 모든 @Controller나 @RestController에서 발생하는 예외를 전역적으로 잡아 처리하는 클래스
// 흐름: @RestControllerAdvice 덕분에, 어떤 계층(Service, Controller)에서든 예외가 발생하면 GlobalExceptionHandler로 자동 이동하여 HTTP 응답 코드로 변환됩니다.
@RestControllerAdvice 
public class GlobalExceptionHandler {

    // 💡 409 CONFLICT: 이메일 중복 + 휴대폰 번호 중복 체크
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<String> handleDuplicateResourceException(DuplicateResourceException e) {
        // HttpStatus.CONFLICT (409) 반환
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    // 💡 401 UNAUTHORIZED: 인증 실패 (로그인, 아이디 찾기, 비밀번호 재설정 공통)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException e) {
        // HttpStatus.UNAUTHORIZED (401) 반환
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    // (선택 사항: 처리되지 않은 다른 모든 RuntimeException을 잡는 핸들러)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        // 500 Internal Server Error로 처리
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 내부 오류: " + e.getMessage());
    }

    // 400 Bad Request: 게시글 유효성 오류
    @ExceptionHandler(PostValidationException.class)
    public ResponseEntity<String> handlePostValidation(PostValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    
    // 404 Not Found: 유저 없음
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    // @ExceptionHandler(Exception.class)
    // public ResponseEntity<String> handleGeneral(Exception ex) {
    //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //             .body("서버 내부 오류: " + ex.getMessage());
    // }
}