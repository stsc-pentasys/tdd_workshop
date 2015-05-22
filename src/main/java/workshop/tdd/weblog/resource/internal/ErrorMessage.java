package workshop.tdd.weblog.resource.internal;

/**
 * Wrapper for error messages to prevent stack traces on REST API calls.
 */
public class ErrorMessage {

    private final String message;

    public ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
