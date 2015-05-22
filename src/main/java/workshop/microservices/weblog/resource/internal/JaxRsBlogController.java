package workshop.microservices.weblog.resource.internal;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import workshop.microservices.weblog.resource.ArticleListResponse;
import workshop.microservices.weblog.resource.ArticleRequest;
import workshop.microservices.weblog.resource.ArticleResponse;
import workshop.microservices.weblog.resource.BlogController;
import workshop.microservices.weblog.core.Article;
import workshop.microservices.weblog.core.ArticleAlreadyExistsException;
import workshop.microservices.weblog.core.BlogService;
import workshop.microservices.weblog.core.BlogServiceException;
import workshop.microservices.weblog.core.UnknownAuthorException;
import workshop.microservices.weblog.core.WrongAuthorException;

/**
 * REST API implementation.
 */
@Component
public class JaxRsBlogController implements BlogController {

    // NOT on setter Method - not supported by Jersey/Spring integration
    @Autowired
    private BlogService blogService;

    @Context
    private UriInfo uriInfo;

    public void setBlogService(BlogService blogService) {
        this.blogService = blogService;
    }

    public void setUriInfo(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    @Override
    public Response getArticles() {
        List<Article> articles = blogService.index();
        List<ArticleListResponse> views = createViewList(articles);
        return Response.ok(views).build();
    }

    private List<ArticleListResponse> createViewList(List<Article> articles) {
        return articles.stream()
                .map(this::createArticleListResponse)
                .collect(Collectors.toList());
    }

    private ArticleListResponse createArticleListResponse(Article a) {
        return new ArticleListResponse(
                a.getTitle(),
                a.getCreated(),
                createUri(a.getArticleId()).toASCIIString());
    }

    private URI createUri(String articleId) {
        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        return builder.path(articleId).build();
    }

    @Override
    public Response getArticle(String entryId) {
        Optional<Article> entry = blogService.read(entryId);
        return entry.map(e -> Response.ok(createBlogEntryView(e)))
                .orElse(Response.status(Status.NOT_FOUND)).build();
    }

    private ArticleResponse createBlogEntryView(Article entry) {
        return new ArticleResponse(
                entry.getArticleId(),
                entry.getTitle(),
                entry.getContent(),
                entry.getPublishedBy().getFullName(),
                entry.getPublishedBy().getEmailAddress(),
                entry.getCreated()
        );
    }

    @Override
    public Response postArticle(ArticleRequest request) {
        ResponseBuilder response;
        try {
            response = postArticleResponse(request);
        } catch (UnknownAuthorException e) {
            response = errorResponse(e, Status.FORBIDDEN);
        } catch (ArticleAlreadyExistsException e) {
            response = errorResponse(e, Status.CONFLICT);
        } catch (BlogServiceException e) {
            response = errorResponse(e, Status.INTERNAL_SERVER_ERROR);
        }
        return response.build();
    }

    private ResponseBuilder postArticleResponse(ArticleRequest request) throws BlogServiceException {
        Article entry = blogService.publish(
                request.getNickName(),
                request.getTitle(),
                request.getContent()
        );
        return Response.created(createUri(entry.getArticleId()));
    }

    private ResponseBuilder errorResponse(BlogServiceException e, Status status) {
        return Response.status(status).entity(new ErrorMessage(e.getMessage()));
    }

    @Override
    public Response putArticle(String entryId, ArticleRequest request) {
        ResponseBuilder response;
        try {
            response = putArticleResponse(entryId, request);
        } catch (WrongAuthorException e) {
            response = errorResponse(e, Status.FORBIDDEN);
        } catch (BlogServiceException e) {
            response = errorResponse(e, Status.INTERNAL_SERVER_ERROR);
        }
        return response.build();
    }

    private ResponseBuilder putArticleResponse(String entryId, ArticleRequest request) throws BlogServiceException {
        Optional<Article> updated = blogService.edit(
                entryId,
                request.getNickName(),
                request.getTitle(),
                request.getContent()
        );

        return updated.map(e -> Response.ok(createBlogEntryView(e)))
                .orElse(Response.status(Status.NOT_FOUND));
    }

}
