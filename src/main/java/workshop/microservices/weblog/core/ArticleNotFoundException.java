package workshop.microservices.weblog.core;

/**
 * Created by schulzst on 23.05.2015.
 */
public class ArticleNotFoundException extends BlogServiceException {

    public ArticleNotFoundException(String message) {
        super(message);
    }
}
