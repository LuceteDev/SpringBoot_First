package springboot_first.pr.exception;

// RuntimeException 상속: Unchecked Exception으로 정의하여 명시적 throws 없이 사용 가능
public class ResourceNotFoundException extends RuntimeException {

    // 1. 기본 생성자
    public ResourceNotFoundException() {
        super("요청한 리소스를 찾을 수 없습니다.");
    }
    
    // 2. 메시지를 인수로 받는 생성자 (PostService에서 사용)
    public ResourceNotFoundException(String message) {
        super(message);
    }

    // 3. 메시지와 원인(Throwable)을 받는 생성자
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}