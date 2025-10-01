
// 	HTTP 상태 코드	    목적
// 	401 Unauthorized	 로그인 실패 시 (사용자 없음, 비밀번호 불일치) 사용







package springboot_first.pr.exception;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}