package springboot_first.pr.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import springboot_first.pr.exception.DuplicateUserException;
import springboot_first.pr.exception.InvalidCredentialException;

// Spring Security를 사용한다면 이 임포트를 사용합니다.
import org.springframework.security.core.AuthenticationException; 
// 만약 Spring Security를 사용하지 않고 다른 AuthenticationException을 쓴다면, 이 줄을 제거하거나, 
// 혹은 사용하는 패키지 경로로 변경해야 합니다. (예: javax.naming.AuthenticationException)

import java.util.HashMap;
import java.util.Map;

/**
 * 전역 예외 처리기 (Global Exception Handler)
 * 애플리케이션 전반에서 발생하는 예외를 일관된 형식으로 처리하여 응답합니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 💡 1. DTO 유효성 검사 실패 처리 (@Valid 관련 예외) - 400 Bad Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String firstErrorMessage = errors.values().iterator().next(); 
        Map<String, String> response = new HashMap<>();
        response.put("message", firstErrorMessage);

        log.warn("유효성 검사 실패 (400): {}", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400
    }

    // 💡 2. 사용자 정의 예외 처리: 중복 회원가입 등 잘못된 요청 - 400 Bad Request
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateUserException(DuplicateUserException ex) {
        log.error("사용자 정의 예외 (400 - Duplicate User): {}", ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400
    }

    // 💡 3. 사용자 정의 예외 처리: 로그인 실패 등 인증 실패 - 401 Unauthorized
    // 서비스 계층에서 던지는 InvalidCredentialException 처리
    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentialException(InvalidCredentialException ex) {
        log.error("사용자 정의 예외 (401 - Invalid Credential): {}", ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED); // 401
    }

    // ✅ 테스트 코드에서 Mocking하여 던지는 AuthenticationException 처리 (401 Unauthorized)
    // Spring Security를 사용한다면, 이 핸들러가 로그인 테스트 실패를 해결합니다.
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthenticationException ex) {
        log.error("인증 시스템 예외 (401 - Authentication Failed): {}", ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED); // 401
    }
    
    // 💡 4. 그 외 예상치 못한 모든 RuntimeException 처리 - 500 Internal Server Error
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleGenericRuntimeException(RuntimeException ex) {
        log.error("예상치 못한 RuntimeException 발생 (500): ", ex);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Internal Server Error: " + ex.getMessage());
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }

    // 💡 5. 최상위 일반 예외 (Exception) 처리 - 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        log.error("최상위 예상치 못한 예외 발생 (500): ", ex);

        Map<String, String> response = new HashMap<>();
        response.put("message", "An unexpected error occurred: " + ex.getMessage());
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }
}