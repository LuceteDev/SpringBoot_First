package springboot_first.pr.exception;

/**
 * 중복된 사용자 (이메일, 전화번호 등) 발생 시 사용하는 예외 클래스.
 * HTTP 400 Bad Request에 매핑됩니다.
 */
public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String message) {
        super(message);
    }
}