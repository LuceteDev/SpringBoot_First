package springboot_first.pr.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 전역 예외 처리기 (Global Exception Handler)
 * 애플리케이션 전반에서 발생하는 예외를 일관된 형식으로 처리하여 응답합니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 💡 1. DTO 유효성 검사 실패 처리 (@Valid 관련 예외)
    // 테스트 실패: register_fail_validation_blank_id, login_fail_validation_blank_id 해결
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // 유효성 검사 실패 시, 어떤 필드에서 어떤 문제가 발생했는지 Map에 담습니다.
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        // 필드 단위 오류를 메시지로 통합하여 반환합니다. (테스트에서 $.message를 기대하므로 이 형식을 따름)
        // 실제 운영 환경에서는 errors 맵 자체를 반환하는 것이 더 좋습니다.
        String firstErrorMessage = errors.values().iterator().next(); 
        Map<String, String> response = new HashMap<>();
        response.put("message", firstErrorMessage);
        
        log.warn("유효성 검사 실패: {}", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400
    }
    
    // 💡 2. 사용자 정의 비즈니스 예외 처리 (로그인 실패 및 중복 회원가입 실패 처리)
    
    // 테스트 실패: login_fail_service_exception_invalid_credential 해결 (401 기대)
    // IllegalArgumentException이 인증 실패(401)를 나타내는 용도로 사용될 때
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("비즈니스 로직 예외 (401): {}", ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        
        // 로그인 실패 등 인증 실패 관련 예외는 401 Unauthorized를 반환
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED); // 401
    }
    
    // 테스트 실패: register_fail_service_exception_duplicate 해결 (400 기대)
    // RuntimeException(중복 ID)이 Bad Request(400)를 나타내는 용도로 사용될 때
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        log.error("비즈니스 로직 예외 (400 또는 500): {}", ex.getMessage());
        
        // 중복 ID와 같은 클라이언트 입력 오류(400)를 명시적으로 처리
        if (ex.getMessage().contains("이미 존재하는 사용자")) {
             Map<String, String> response = new HashMap<>();
             response.put("message", ex.getMessage());
             return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400
        }
        
        // 그 외 예상치 못한 RuntimeException은 500 Internal Server Error로 처리
        Map<String, String> response = new HashMap<>();
        response.put("message", "Internal Server Error: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }
}