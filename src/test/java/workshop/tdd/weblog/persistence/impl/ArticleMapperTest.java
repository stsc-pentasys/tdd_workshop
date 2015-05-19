package workshop.tdd.weblog.persistence.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static workshop.tdd.weblog.TestData.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import workshop.tdd.weblog.core.Article;
import workshop.tdd.weblog.core.ArticleBuilder;
import workshop.tdd.weblog.core.Author;
import workshop.tdd.weblog.persistence.BlogEntryEntity;
import workshop.tdd.weblog.persistence.UserEntity;
import workshop.tdd.weblog.persistence.UserEntityBuilder;

@RunWith(MockitoJUnitRunner.class)
public class ArticleMapperTest {

    @InjectMocks
    private ArticleMapper underTest = new ArticleMapper();

    @Mock
    private AuthorMapper authorMapperMock;

    @Test
    public void mapConvertsArticleToBlogEntryEntity() throws Exception {
        Article article = ArticleBuilder.defaultArticle().build();
        UserEntity userEntity = UserEntityBuilder.defaultUserEntity().build();
        when(authorMapperMock.map(any(Author.class))).thenReturn(userEntity);
        BlogEntryEntity result = underTest.map(article);
        assertThat("Entry id", result.getEntryId(), is(ARTICLE_ID));
        assertThat("Title", result.getTitle(), is(TITLE));
        assertThat("Content", result.getContent(), is(CONTENT));
        assertThat("Author", result.getAuthor(), sameInstance(userEntity));
    }
}