package workshop.microservices.weblog.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Weblog micro-service REST API.
 */
@Path("/articles")
public interface BlogController {

    /**
     * Retrieve all articles.
     *
     * @return contains a list of Article representations
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    Response getArticles();

    /**
     * Retrieve a single blog-entry identified by it's normalized title.
     *
     * @param entryId the normalized title
     * @return contains the entry's representation in case of success
     */
    @GET
    @Path("{articleId}")
    @Produces(MediaType.APPLICATION_JSON)
    Response getArticle(@PathParam("articleId") String entryId);

    /**
     * Create a new entry. Returns the entry's URI as <i>Location</i> header.
     *
     * @param request carries the new entry's data
     * @return HTTP 201 Created in case of success
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response postArticle(ArticleRequest request);

    /**
     * Modify an existing article, identified by its unique id.
     *
     * @param entryId the article's id
     * @param request the modified article
     * @return the modified article
     */
    @PUT
    @Path("{articleId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response putArticle(@PathParam("articleId") String entryId, ArticleRequest request);
}
