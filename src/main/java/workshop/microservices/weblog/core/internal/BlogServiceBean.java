package workshop.microservices.weblog.core.internal;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import workshop.microservices.weblog.core.*;

/**
 * Core service implementation.
 */
@Service
public class BlogServiceBean implements BlogService {

    private ArticlePersistenceAdapter blogEntryAdapter;

    private AuthorPersistenceAdapter authorAdapter;

    private IdNormalizer idNormalizer;

    private NotificationAdapter notificationAdapter = new NotificationAdapter() {
        @Override
        public void created(Article newEntry) {
            // do nothing
        }

        @Override
        public void edited(Article existingEntry) {
            // do nothing
        }
    };

    /**
     * Set mandatory dependencies only.
     *
     * @param blogEntryAdapter
     * @param authorAdapter
     * @param idNormalizer
     */
    @Autowired
    public BlogServiceBean(
            ArticlePersistenceAdapter blogEntryAdapter,
            AuthorPersistenceAdapter authorAdapter,
            IdNormalizer idNormalizer) {
        this.blogEntryAdapter = blogEntryAdapter;
        this.authorAdapter = authorAdapter;
        this.idNormalizer = idNormalizer;
    }

    /**
     * Test only.
     */
    BlogServiceBean() {}

    /**
     * The NotificationAdapter is optional.
     * A noop implementation is provided by default.
     *
     * @param notificationAdapter
     */
    @Autowired(required = false)
    public void setNotificationAdapter(NotificationAdapter notificationAdapter) {
        this.notificationAdapter = notificationAdapter;
    }

    @Override
    public List<Article> index() {
        return blogEntryAdapter.findAll();
    }

    @Override
    public Article publish(String nickName, String title, String content) {
        verifyNotEmpty(nickName, title, content);
        Author author = verifyAuthor(nickName);
        String entryId = createUniqueId(title);
        Article newEntry = new Article(entryId, title, content, author, now());
        blogEntryAdapter.save(newEntry);
        notificationAdapter.created(newEntry);
        return newEntry;
    }

    private void verifyNotEmpty(String nickName, String title, String content) throws BlogServiceException {
        if (StringUtils.isBlank(nickName) || StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
            throw new BlogServiceException("Nickname, title and content must not be empty");
        }
    }

    private Author verifyAuthor(String nickName) throws UnknownAuthorException {
        Optional<Author> author = authorAdapter.findById(nickName);
        return author.orElseThrow(() -> new UnknownAuthorException("No author found with id " + nickName));
    }

    private String createUniqueId(String title) throws ArticleAlreadyExistsException {
        String normalized = idNormalizer.normalizeTitle(title);
        Optional<Article> article = blogEntryAdapter.findById(normalized);
        if (article.isPresent()) {
            throw new ArticleAlreadyExistsException("An entry with id '" + normalized + "' already exists");
        }
        return normalized;
    }

    private Date now() {
        return new Date();
    }

    @Override
    public Optional<Article> edit(String articleId, String editor, String title, String content) {
        verifyNotEmpty(editor, title, content);
        Optional<Article> published = read(articleId);
        return published.map(a -> executeUpdate(a, editor, title, content));
    }

    private Article executeUpdate(Article published, String nickName, String title, String content) {
        verifySameAuthor(published, nickName);
        Article updated = new Article(published, title, content);
        blogEntryAdapter.update(updated);
        notificationAdapter.edited(updated);
        return updated;
    }

    @Override
    public Optional<Article> read(String articleId) {
        return blogEntryAdapter.findById(articleId);
    }

    private void verifySameAuthor(Article published, String nickName) throws WrongAuthorException {
        String originalAuthor = published.getPublishedBy().getNickName();
        if (!originalAuthor.equals(nickName)) {
            throw new WrongAuthorException(nickName + " is not the author of " + published.getArticleId());
        }
    }
}
