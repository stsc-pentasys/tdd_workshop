package workshop.tdd.weblog.core.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static workshop.tdd.weblog.TestData.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import workshop.tdd.weblog.core.*;

@RunWith(MockitoJUnitRunner.class)
public class BlogServicePojoTest {

    public static final String NEW_TITLE = "New title";
    public static final String NEW_CONTENT = "New content";

    @InjectMocks
    private BlogService underTest = new BlogServicePojo();

    @Mock
    private ArticlePersistenceAdapter articlePersistenceAdapterMock;

    @Mock
    private AuthorPersistenceAdapter authorPersistenceAdapterMock;

    @Mock
    private IdNormalizer idNormalizerMock;

    @Mock
    private NotificationAdapter notificationAdapterMock;

    @Captor
    private ArgumentCaptor<Article> articleCaptor;

    @Test
    public void findArticlePassesFoundArticle() throws Exception {
        Article entry = ArticleBuilder.defaultArticle().build();
        when(articlePersistenceAdapterMock.findById(ARTICLE_ID)).thenReturn(Optional.of(entry));
        Optional<Article> result = underTest.findArticle(ARTICLE_ID);
        assertThat("Blog entry", result.get(), sameInstance(entry));
    }

    @Test
    public void findArticleThrowsExceptionOnMissingEntry() throws Exception {
        when(articlePersistenceAdapterMock.findById(ARTICLE_ID)).thenReturn(Optional.empty());
        Optional<Article> result = underTest.findArticle(ARTICLE_ID);
        assertThat("Article", result.isPresent(), is(false));
    }

    @Test
    public void findAllArticlesReturnsUnmodifiedList() throws Exception {
        // unmodifiable by definition
        List<Article> articles = Collections.emptyList();
        when(articlePersistenceAdapterMock.findAll()).thenReturn(articles);
        List<Article> result = underTest.findAllArticles();
        assertThat("Article list", result, sameInstance(articles));
    }

    @Test
    public void createNewArticleSuccess() throws Exception {
        when(authorPersistenceAdapterMock.findById(NICK_NAME))
                .thenReturn(Optional.of(AuthorBuilder.defaultAuthor().build()));
        when(idNormalizerMock.normalizeTitle(TITLE)).thenReturn(ARTICLE_ID);
        when(articlePersistenceAdapterMock.findById(ARTICLE_ID)).thenReturn(Optional.empty());

        Article result = underTest.createNewArticle(NICK_NAME, TITLE, CONTENT);

        assertThat("Article id", result.getArticleId(), is(ARTICLE_ID));
        verify(notificationAdapterMock).created(result);
    }

    @Test(expected = UnknownAuthorException.class)
    public void createNewArticleFailsForUnknownAuthor() throws Exception {
        when(authorPersistenceAdapterMock.findById(NICK_NAME)).thenReturn(Optional.empty());
        underTest.createNewArticle(NICK_NAME, TITLE, CONTENT);
    }

    @Test(expected = ArticleAlreadyExistsException.class)
    public void createNewArticleFailsForDuplicateId() throws Exception {
        when(authorPersistenceAdapterMock.findById(NICK_NAME))
                .thenReturn(Optional.of(AuthorBuilder.defaultAuthor().build()));
        when(idNormalizerMock.normalizeTitle(TITLE)).thenReturn(ARTICLE_ID);
        when(articlePersistenceAdapterMock.findById(ARTICLE_ID))
                .thenReturn(Optional.of(ArticleBuilder.defaultArticle().build()));
        underTest.createNewArticle(NICK_NAME, TITLE, CONTENT);
    }

    @Test
    public void modifyArticleSucceeds() throws Exception {
        Article article = ArticleBuilder.defaultArticle().build();
        when(articlePersistenceAdapterMock.findById(ARTICLE_ID)).thenReturn(Optional.of(article));

        Optional<Article> result = underTest.modifyArticle(ARTICLE_ID, NICK_NAME, NEW_TITLE, NEW_CONTENT);

        verify(articlePersistenceAdapterMock).update(articleCaptor.capture());
        Article updated = articleCaptor.getValue();
        assertThat("Title", updated.getTitle(), is(NEW_TITLE));
        assertThat("Content", updated.getContent(), is(NEW_CONTENT));
        assertThat("Article", result.get(), sameInstance(updated));
    }

    @Test(expected = WrongAuthorException.class)
    public void modifyArticleFailsForWrongNickName() throws Exception {
        Article article = ArticleBuilder.defaultArticle().build();
        when(articlePersistenceAdapterMock.findById(ARTICLE_ID)).thenReturn(Optional.of(article));
        underTest.modifyArticle(ARTICLE_ID, "intruder", NEW_TITLE, NEW_CONTENT);
    }

    @Test
    public void modifyArticleFailsForUnknownId() throws Exception {
        when(articlePersistenceAdapterMock.findById(ARTICLE_ID)).thenReturn(Optional.empty());
        Optional<Article> result =underTest.modifyArticle(ARTICLE_ID, NICK_NAME, NEW_TITLE, NEW_CONTENT);
        assertThat("Article", result.isPresent(), is(false));
    }
}