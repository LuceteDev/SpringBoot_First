
// 	HTTP 상태 코드	목적
// 	409 Conflict	 회원가입 시 이메일이 중복될 때 사용






package springboot_first.pr.exception;

// RuntimeException을 상속받아 Unchecked Exception으로 만듭니다.
public class DuplicateEmailException extends RuntimeException {
    // 메시지를 인수로 받는 생성자를 만듭니다.
    public DuplicateEmailException(String message) {
        super(message);
    }
}