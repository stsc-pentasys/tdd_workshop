package workshop.tdd.weblog.controller.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static workshop.tdd.weblog.TestData.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import workshop.tdd.weblog.controller.ArticleListResponse;
import workshop.tdd.weblog.controller.ArticleRequest;
import workshop.tdd.weblog.controller.ArticleResponse;
import workshop.tdd.weblog.controller.BlogController;
import workshop.tdd.weblog.core.Article;
import workshop.tdd.weblog.core.ArticleAlreadyExistsException;
import workshop.tdd.weblog.core.ArticleBuilder;
import workshop.tdd.weblog.core.BlogService;
import workshop.tdd.weblog.core.BlogServiceException;
import workshop.tdd.weblog.core.UnknownAuthorException;

@RunWith(MockitoJUnitRunner.class)
public class JaxRsBlogControllerTest {

    public static final String NEW_TITLE = "New title";
    public static final String NEW_CONTENT = "New content";
    @InjectMocks
    private BlogController underTest = new JaxRsBlogController();
    
    @Mock
    private BlogService blogServiceMock;

    @Mock
    private UriInfo uriInfoMock;

    @Mock
    private UriBuilder uriBuilderMock;

    @Test
    public void getArticleCallsServiceAndReturns200() throws Exception {
        Article article = ArticleBuilder.defaultArticle().build();
        when(blogServiceMock.findArticle(ARTICLE_ID)).thenReturn(Optional.of(article));
        Response response = getArticle();
        assertStatus(response, Status.OK);
        assertThat("Response body", ((ArticleResponse) response.getEntity()).getArticleId(), is(article.getArticleId()));
    }

    private Response getArticle() {
        return underTest.getArticle(ARTICLE_ID);
    }

    private void assertStatus(Response response, StatusType status) {
        assertThat("HTTP status code", response.getStatusInfo(), is(status));
    }

    @Test
    public void getEntryReturns404IfEntryNotFound() throws Exception {
        when(blogServiceMock.findArticle(ARTICLE_ID)).thenReturn(Optional.empty());
        Response result = getArticle();
        assertStatus(result, Status.NOT_FOUND);
    }

    private void onGetArticleThrow(Exception exception) throws BlogServiceException {
        when(blogServiceMock.findArticle(ARTICLE_ID)).thenThrow(exception);
    }

    @Test
    public void postArticleReturns201AndLocation() throws Exception {
        when(blogServiceMock.createNewArticle(NICK_NAME, TITLE, CONTENT))
                .thenReturn(ArticleBuilder.defaultArticle().build());
        expectURIConstruction();
        Response result = postNewArticle();
        assertStatus(result, Status.CREATED);
        assertThat("Location header", result.getLocation().toString(), is("/entries/for-testing-purposes-only"));
    }

    private void expectURIConstruction() throws URISyntaxException {
        when(uriInfoMock.getAbsolutePathBuilder()).thenReturn(uriBuilderMock);
        when(uriBuilderMock.path(ARTICLE_ID)).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(new URI("/entries/for-testing-purposes-only"));
    }

    private Response postNewArticle() {
        return underTest.postArticle(createPostArticleRequest());
    }

    private ArticleRequest createPostArticleRequest() {
        return new ArticleRequest(NICK_NAME, TITLE, CONTENT);
    }

    @Test
    public void postArticleReturns403OnNUnknownAuthorException() throws Exception {
        onPostArticleThrow(new UnknownAuthorException("Test"));
        Response result = postNewArticle();
        assertStatus(result, Status.FORBIDDEN);
    }

    private void onPostArticleThrow(Exception exception) throws BlogServiceException {
        when(blogServiceMock.createNewArticle(NICK_NAME, TITLE, CONTENT))
                .thenThrow(exception);
    }

    @Test
    public void postArticleReturns409OnArticleAlreadyExistsException() throws Exception {
        onPostArticleThrow(new ArticleAlreadyExistsException("Test"));
        Response result = postNewArticle();
        assertStatus(result, Status.CONFLICT);
    }

    @Test
    public void getArticlesReturnsList() throws Exception {
        List<Article> articles = Arrays.asList(ArticleBuilder.defaultArticle().build());
        when(blogServiceMock.findAllArticles()).thenReturn(articles);
        expectURIConstruction();
        Response result = underTest.getArticles();
        assertStatus(result, Status.OK );
        assertThat("List size", ((List< ArticleListResponse>) result.getEntity()).size(), is(1));
    }

    @Test
    public void getArticlesReturnsEmptyList() throws Exception {
        List<Article> articles = Collections.emptyList();
        when(blogServiceMock.findAllArticles()).thenReturn(articles);
        Response result = underTest.getArticles();
        assertStatus(result, Status.OK);
        assertThat("List size", ((List<ArticleListResponse>) result.getEntity()).size(), is(0));
        verifyZeroInteractions(uriInfoMock);
    }

    @Test
    public void putArticleReturns200OnSuccess() throws Exception {
        Article article = ArticleBuilder.defaultArticle().withTitle(NEW_TITLE).andContent(NEW_CONTENT).build();
        when(blogServiceMock.modifyArticle(article.getArticleId(), NICK_NAME, NEW_TITLE, NEW_CONTENT))
                .thenReturn(Optional.of(article));
        Response result = underTest.putArticle(ARTICLE_ID, new ArticleRequest(NICK_NAME, NEW_TITLE, NEW_CONTENT));
        assertStatus(result, Status.OK);
        ArticleResponse response = (ArticleResponse) result.getEntity();
        assertThat("Title", response.getTitle(), is(NEW_TITLE));
    }
}
