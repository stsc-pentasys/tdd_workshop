package workshop.microservices.weblog.core;

import java.util.List;
import java.util.Optional;

/**
 * Core service API.
 */
public interface BlogService {

    /**
     * Retrieve all published articles from underlying datastore.
     * Implementation must ensure a proper order, i.e. newest first.
     *
     * @return a list of articles, or an empty list
     */
    List<Article> findAllArticles();

    /**
     * Retrieve a single article from the underlying datastore.
     *
     * @param articleId the article's unique id
     * @return the corresponding article
     */
    Optional<Article> findArticle(String articleId);

    /**
     * Create a new article and save it to the underlying datastore.
     *
     * @param nickName the author's login name
     * @param title the new article's title
     * @param content the new article's content
     * @return the created Article
     */
    Article createNewArticle(String nickName, String title, String content);

    /**
     * Modify an existing article in the underlying datastore.
     * Only title and content will be overwritten, the id remains unchanged regardless of a new title.
     *
     * @param articleId the article's unique id
     * @param nickName the author's login name
     * @param title the article's new title
     * @param content the article's new content
     * @return the modified article
     */
    Optional<Article> modifyArticle(String articleId, String nickName, String title, String content);

}
