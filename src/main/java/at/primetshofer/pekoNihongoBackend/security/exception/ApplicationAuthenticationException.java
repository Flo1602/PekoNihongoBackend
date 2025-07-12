package at.primetshofer.pekoNihongoBackend.security.exception;

public class ApplicationAuthenticationException extends RuntimeException {
    public ApplicationAuthenticationException(String message) {
        super(message);
    }
}
