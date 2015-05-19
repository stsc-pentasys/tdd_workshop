package workshop.tdd.weblog.core;

/**
 * Represents a unique key violation.
 */
public class ArticleAlreadyExistsException extends BlogServiceException {
    public ArticleAlreadyExistsException(String message) {
        super(message);
    }
}
