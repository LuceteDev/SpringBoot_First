// 	HTTP 상태 코드	목적
// 	409 Conflict	 회원가입 시 이메일, 휴대폰 번호가 중복될 때 사용

package springboot_first.pr.exception;

// RuntimeException을 상속받아 Unchecked Exception으로 만듭니다.
public class DuplicateResourceException extends RuntimeException {
// 메시지를 인수로 받는 생성자를 만듭니다.
    public DuplicateResourceException(String message) {
        super(message);  // RuntimeException에 메시지 전달
    }
}
