package workshop.tdd.weblog.core;

/**
 * Thrown on an unspecified service failure.
 */
public class BlogServiceException extends RuntimeException {

    public BlogServiceException(String message) {
        super(message);
    }

}
