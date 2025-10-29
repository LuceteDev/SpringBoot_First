package springboot_first.pr.exception;

public class PostValidationException extends RuntimeException {
    public PostValidationException(String message) {
        super(message);
    }
}
