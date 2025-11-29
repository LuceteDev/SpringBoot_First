package springboot_first.pr.exception;

/**
 * 로그인 실패, 토큰 검증 실패 등 인증 관련 문제로 인해 발생하는 예외를 나타냅니다.
 * HTTP Status Code 401 Unauthorized에 매핑됩니다.
 */
public class InvalidCredentialException extends RuntimeException {

    public InvalidCredentialException(String message) {
        super(message);
    }
}