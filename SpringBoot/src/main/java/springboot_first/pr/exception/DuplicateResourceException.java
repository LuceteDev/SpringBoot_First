package springboot_first.pr.exception;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);  // RuntimeException에 메시지 전달
    }
}
