package workshop.tdd.weblog.persistence.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static workshop.tdd.weblog.TestData.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import workshop.tdd.weblog.core.Article;
import workshop.tdd.weblog.core.ArticleBuilder;
import workshop.tdd.weblog.core.ArticlePersistenceAdapter;
import workshop.tdd.weblog.persistence.BlogEntryEntity;
import workshop.tdd.weblog.persistence.BlogEntryEntityBuilder;
import workshop.tdd.weblog.persistence.BlogEntryRepository;

/**
 * Tests the bridge between the technology-independent adapter and the Spring Data specific implementation.
 */
@RunWith(MockitoJUnitRunner.class)
public class JpaArticlePersistenceAdapterTest {

    public static final String NEW_TITLE = "New title";
    public static final String NEW_CONTENT = "New content";
    @InjectMocks
    private ArticlePersistenceAdapter underTest = new JpaArticlePersistenceAdapter();

    @Mock
    private BlogEntryRepository blogEntryRepositoryMock;

    @Mock
    private ArticleMapper articleMapperMock;

    @Mock
    private BlogEntryEntityMapper blogEntryEntityMapperMock;

    @Test
    public void findByIdMapsFoundEntry() throws Exception {
        BlogEntryEntity entity = new BlogEntryEntity();
        when(blogEntryRepositoryMock.findByEntryId(ARTICLE_ID)).thenReturn(entity);
        Article entry = ArticleBuilder.defaultArticle().build();
        when(blogEntryEntityMapperMock.map(entity)).thenReturn(entry);

        Optional<Article> result = underTest.findById(ARTICLE_ID);

        assertThat("Blog entry", result.get(), sameInstance(entry));
    }

    @Test
    public void findByIdDoesNotMapIfNoEntryFound() throws Exception {
        when(blogEntryRepositoryMock.findByEntryId(ARTICLE_ID)).thenReturn(null);

        Optional<Article> result = underTest.findById(ARTICLE_ID);

        assertThat("Blog entry", result.isPresent(), is(false));
        verifyZeroInteractions(blogEntryEntityMapperMock);
    }

    @Test
    public void findAllReturnsConvertedList() throws Exception {
        BlogEntryEntity blogEntryEntity = BlogEntryEntityBuilder.defaultBlogEntryEntity().build();
        List<BlogEntryEntity> resultSet = Arrays.asList(blogEntryEntity);
        Article convertedEntry = ArticleBuilder.defaultArticle().build();
        when(blogEntryRepositoryMock.findAllByCustomQuery()).thenReturn(resultSet);
        when(blogEntryEntityMapperMock.map(blogEntryEntity)).thenReturn(convertedEntry);

        List<Article> result = underTest.findAll();

        assertThat("List size", result, hasSize(1));
        assertThat("Article", result.get(0), sameInstance(convertedEntry));
    }

    @Test
    public void findAllReturnsEmptyList() throws Exception {
        List<BlogEntryEntity> resultSet = Collections.emptyList();
        when(blogEntryRepositoryMock.findAllByCustomQuery()).thenReturn(resultSet);

        List<Article> result = underTest.findAll();

        assertThat("List size", result, hasSize(0));
    }

    @Test
    public void savePersistsTransformedArticle() throws Exception {
        Article article = ArticleBuilder.defaultArticle().build();
        BlogEntryEntity blogEntryEntity = BlogEntryEntityBuilder.defaultBlogEntryEntity().build();
        when(articleMapperMock.map(article)).thenReturn(blogEntryEntity);

        underTest.save(article);

        verify(blogEntryRepositoryMock).save(blogEntryEntity);
    }

    @Test
    public void updateModifiesTitleAndContent() throws Exception {
        Article article = ArticleBuilder.defaultArticle()
                .withTitle(NEW_TITLE)
                .andContent(NEW_CONTENT)
                .build();
        BlogEntryEntity blogEntryEntity = BlogEntryEntityBuilder.defaultBlogEntryEntity().build();
        when(blogEntryRepositoryMock.findByEntryId(article.getArticleId())).thenReturn(blogEntryEntity);

        underTest.update(article);

        assertThat("Title", blogEntryEntity.getTitle(), is(NEW_TITLE));
        assertThat("Content", blogEntryEntity.getContent(), is(NEW_CONTENT));
    }
}