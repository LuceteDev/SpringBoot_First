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

// 💡 테스트 코드에서 사용하는 커스텀 예외로 임포트
import springboot_first.pr.exception.AuthenticationException; 

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

        // 클라이언트에게 가장 첫 번째 발생한 에러 메시지만 전달
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

    /**
     * 💡 3. 사용자 정의 예외 처리: 로그인 실패, ID 찾기 실패 등 인증/자격 증명 실패 - 401 Unauthorized
     * InvalidCredentialException과 AuthenticationException을 통합하여 처리합니다.
     */
    @ExceptionHandler({InvalidCredentialException.class, AuthenticationException.class})
    public ResponseEntity<Map<String, String>> handleAuthenticationAndCredentialExceptions(RuntimeException ex) {
        // RuntimeException의 서브클래스이므로 ex.getMessage()를 안전하게 사용합니다.
        log.error("사용자 정의 인증/자격 증명 예외 (401 Unauthorized): {}", ex.getMessage()); 
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